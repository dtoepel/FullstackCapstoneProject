import './App.css'

import electionLogo from './assets/election.svg'
import candidatesLogo from './assets/candidates.svg'
import archiveLogo from './assets/archive-inv.svg'
import logoutLogo from './assets/logout.svg'
import loginLogo from './assets/login.svg'
import voteLogo from './assets/vote.svg'
import mailLogo from './assets/email.svg'

import type {
    AnalysisResult,
    Candidate,
    CondorcetResult,
    Election,
    MyError,
    STVResultItem,
    Vote,
    Voter
} from "./ElectionData.ts";

import ElectionTable from "./ElectionTable.tsx";
import NavigationItem from "./NavigationItem.tsx";
import CandidateTable from "./CandidateTable.tsx";
import ElectionForm from "./ElectionForm.tsx";
import CandidateForm from "./CandidateForm.tsx";

import {Route, Routes, useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import axios, {type AxiosError} from "axios";
import AddVoteForm from "./AddVoteForm.tsx";
import ResultTable from "./ResultTable.tsx";
import ModalConfirmation from "./ModalConfirmation.tsx";
import ErrorMessage from "./ErrorMessage.tsx";
import VoterEMailForm from "./VoterEMailForm.tsx";

function App() {
    const nav = useNavigate();

    // main model
    const [elections, setElections] = useState<Election[]>([]);
    const [candidates, setCandidates] = useState<Candidate[]>([]);
    const [voterCodes, setVoterCodes] = useState<Voter[]>([])

    // default values for forms
    const defaultElection:Election = {
        name:"new Election",id:"ABC00",description:"Enter description here...",
        candidateIDs:[],votes:[],voterEmails:["voter1@example.com",
            "voter2@example.com","voter3@example.com","voter4@example.com",
            "voter5@example.com","voter6@example.com","voter7@example.com"],
        electionState:"OPEN",seats:1,electionMethod:"STV",candidateType:"Person"
    };
    const defaultCandidate:Candidate = {
        id:"(automatically assigned)", name:"John Doe",
        description:"an average candidate",
        party:"Independent", color:"888", type:"Person", archived:false
    };
    const defaultVote:Vote = {
        rankingIDs:[],
        validationCode:"******"
    };

    // temporary variables for forms
    type EditElectionProps = {
        election:Election
        editMode:boolean;
        error:string|null;
        onSuccess:()=>void;
    }

    type EditCandidateProps = {
        candidate:Candidate
        editMode:boolean;
        error:string|null;
        onSuccess:()=>void;
    }

    const [editElectionProps, setEditElectionProps] = useState<EditElectionProps>({
        election:defaultElection,
        editMode:false,
        error:null,
        onSuccess:()=>{}});

    const [editCandidateProps, setEditCandidateProps] = useState<EditCandidateProps>({
        candidate:defaultCandidate,
        editMode:false,
        error:null,
        onSuccess:()=>{}});

    const [currentElection, setCurrentElection] = useState<Election|null>(null);
    const [newVote, setNewVote] = useState<Vote>(defaultVote)
    const [result, setResult] = useState<STVResultItem[]>([])
    const [resultAnalysis, setResultAnalysis] = useState<AnalysisResult>({electedIdsBySeats:[]})
    const [resultCondorcet, setResultCondorcet] = useState<CondorcetResult>({candidateIDs:[], duels:[]})

    const [confirmDeleteElection, setConfirmDeleteElection] = useState<Election|null>(null)
    const [confirmDeleteCandidate, setConfirmDeleteCandidate] = useState<Candidate|null>(null)
    const [confirmRetireCandidate, setConfirmRetireCandidate] = useState<Candidate|null>(null)
    const [error, setError] = useState<MyError>({status:200, message:"test", message2:null})

    // only place to update data
    // could be split to reduce traffic by a small amount
    function getAllElectionsAndCandidates():void {
        axios.get("/api/election").then(
            (response) => {
                setElections(response.data);
                if(currentElection == null && elections.length > 0) {
                    const election = elections.at(0);
                    if(election) setCurrentElection(election);
                }
                axios.get("/api/election/candidates").then(
                    (response) => {
                        setCandidates(response.data);
                    }
                )
            }
        )
    }

    useEffect(() => {
        getAllElectionsAndCandidates()
    },[])

    // functions to backend for editing
    function createElection():void {
        axios.post("/api/election", editElectionProps.election)
            .then(() => {getAllElectionsAndCandidates(); editElectionProps.onSuccess()})
            .catch(error => {
                if(error.status == 403) {
                    setEditElectionProps({...editElectionProps, error:error.response.data.message})
                } else {
                    handleError(error);
                }
            })
    }

    function updateElection(election:Election):void {
        axios.put("/api/election", election)
            .then(() => {getAllElectionsAndCandidates(); editElectionProps.onSuccess()})
            .catch(error => {
                if(error.response) {
                    setEditElectionProps({...editElectionProps, error:error.response.data.message})
                }
            })
    }

    function advanceElection(election:Election):void {
        axios.post("api/election/advance/" + election.id)
            .then(() => {getAllElectionsAndCandidates(); editElectionProps.onSuccess()})
            .catch(error => { handleError(error) })
    }

    function deleteElection(election:Election):void {
        axios.delete("/api/election/" + election.id)
            .then(() => {getAllElectionsAndCandidates(); editElectionProps.onSuccess()})
            .catch(error => { handleError(error) })
    }

    function createCandidate(candidate:Candidate):void {
        axios.post("/api/election/candidates", candidate)
            .then(() => {getAllElectionsAndCandidates(); editCandidateProps.onSuccess()})
            .catch(error => {
                if(error.status == 403) {
                    setEditCandidateProps({...editCandidateProps, error:error.response.data.message})
                } else {
                    handleError(error);
                }
            })
    }

    function updateCandidate(candidate:Candidate) {
        axios.put("/api/election/candidates", candidate)
            .then(() => {getAllElectionsAndCandidates(); editCandidateProps.onSuccess()})
            .catch(error => {
                if(error.response) {
                    setEditCandidateProps({...editCandidateProps, error:error.response.data.message})
                }
            })
    }

    function retireCandidate(candidate:Candidate) {
        axios.put("/api/election/candidates", {... candidate, archived: true})
            .then(() => {getAllElectionsAndCandidates(); editCandidateProps.onSuccess()})
            .catch(error => { handleError(error) })
    }

    function deleteCandidate(candidate:Candidate):void {
        axios.delete("/api/election/candidates/" + candidate.id)
            .then(() => {getAllElectionsAndCandidates(); editElectionProps.onSuccess()})
            .catch(error => { handleError(error) })
    }

    function getVoterCodes(email:string):void {
        if(email) axios.get("/api/election/email/" + email)
            .then(response => {
                setVoterCodes(response.data);})
            .catch(error => { handleError(error) })
    }

    function handleError(error:AxiosError) {
        const status:number|undefined = error.status;
        const message:string = error.message;

        console.log("Handling Error: " );
        console.log(error);

        if(error.response) {
            const message2:string = (error.response.data as never)["message"]
            setError({status:status, message:message, message2:message2})
        } else {
            setError({status:status, message:message, message2:null})
        }

        nav("/error/")
    }

    // authorization
    const [user, setUser] = useState<string | undefined | null>(undefined);

    const loadUser = () => {
        axios.get('/api/auth/me')
            .then(response => {
                setUser(response.data===""?null:response.data)
            })
            .catch(() => setUser(null));
    }

    function login() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080': window.location.origin
        window.open(host + '/oauth2/authorization/github', '_self')
    }

    function logout() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080': window.location.origin
        window.open(host + '/logout', '_self')
    }

    function getElectionResults(election:Election):void {
        axios.get("/api/election/results/" + election.id).then(
            (response) => {
                setResult(response.data);
                axios.get("/api/election/result-analysis/" + election.id).then(
                    (response) => {
                        setResultAnalysis(response.data);
                        axios.get("/api/election/result-condorcet/" + election.id).then(
                            (response) => {
                                setResultCondorcet(response.data)
                                nav("/result/")
                            }
                        )
                    }
                )
            }
        )
    }

    function submitVote():void {
        const election:Election|null = currentElection
        if(election) {
            axios.post("/api/election/vote/" + election.id, newVote).then(() => {
                setNewVote(defaultVote);
                setCurrentElection(null);
                getAllElectionsAndCandidates();
                nav("/");
            }).catch(error => {
                handleError(error)
            })
        }
    }

    useEffect(() => {
        loadUser();
    }, []);

    return (
    <div style={{display:"flex",
        flexDirection:"column",
        justifyContent:"flex-start",
        alignItems:"center"}}>
        <div className={"navigation-bar"}>
            <NavigationItem
                text={"Elections"}
                symbolFile={electionLogo}
                onClick={() => nav("/")}/>
            {user?<NavigationItem
                text={"Candidates"}
                symbolFile={candidatesLogo}
                onClick={() => nav("/candidates/")}/>:""}
            {user?"":<NavigationItem
                text={"Vote"}
                symbolFile={voteLogo}
                onClick={() => nav("/vote/")}/>}
            <NavigationItem
                text={"Archive"}
                symbolFile={archiveLogo}
                onClick={() => nav("/archive/")}/>
            {user?"":<NavigationItem
                text={"Get Code"}
                symbolFile={mailLogo}
                onClick={() => nav("/voterEmail/")}/>}
            <NavigationItem
                text={user?"Logout":"Login"}
                symbolFile={user?logoutLogo:loginLogo}
                onClick={user?logout:login}/>
        </div>

        <h1>Election Manager</h1>
        <h3>User: {user === undefined ? "undefined" : user === null ? "null" : user}</h3>
        <Routes>
            <Route path={"/"} element={<ElectionTable
                elections={elections}
                candidates={candidates}
                onGetResult={getElectionResults}
                isArchive={false}
                onVote={(election) => {
                    setCurrentElection(election); setNewVote(defaultVote);
                    nav("/vote/")
                }}
                onCreateElection={() => {
                    setEditElectionProps({
                        election:defaultElection,
                        editMode:false,
                        error:null,
                        onSuccess:() => nav("/")
                    });
                    nav("/createElection/")
                }}
                onEditElection={(election) => {
                    setEditElectionProps({
                        election:election,
                        editMode:true,
                        error:null,
                        onSuccess:() => nav("/")
                    })
                    nav("/editElection/")
                }}
                onAdvanceElection={advanceElection}
                onDeleteElection={setConfirmDeleteElection}
                isAdmin={!!user}
            />}/>
            <Route path={"/createElection/"} element={<ElectionForm
                election={editElectionProps.election}
                candidates={candidates}
                editMode={editElectionProps.editMode}
                error={editElectionProps.error}
                onEdit={(election) => setEditElectionProps({...editElectionProps, election:election})}
                onSubmit={createElection}
                isAdmin={!!user}
            />}/>
            <Route path={"/editElection/"} element={<ElectionForm
                election={editElectionProps.election}
                candidates={candidates}
                editMode={editElectionProps.editMode}
                error={editElectionProps.error}
                onEdit={(election) => setEditElectionProps({...editElectionProps, election:election})}
                onSubmit={() => updateElection(editElectionProps.election)}
                isAdmin={!!user}
            />}/>
            <Route path={"/candidates/"} element={<CandidateTable
                value={candidates}
                elections={elections}
                onCreateCandidate={() => {
                    setEditCandidateProps({
                        candidate:defaultCandidate,
                        editMode:false,
                        error:null,
                        onSuccess:() => nav("/candidates/")
                    });
                    nav("/createCandidate/")
                }}
                onEditCandidate={(candidate) => {
                    setEditCandidateProps({
                        candidate:candidate,
                        editMode:true,
                        error:null,
                        onSuccess:() => nav("/candidates/")
                    })
                    nav("/editCandidate/")
                }}
                onDeleteCandidate={setConfirmDeleteCandidate}
                onRetireCandidate={setConfirmRetireCandidate}
            />}/>
            <Route path={"/createCandidate/"} element={<CandidateForm
                candidate={editCandidateProps.candidate}
                editMode={editCandidateProps.editMode}
                error={editCandidateProps.error}
                onEdit={(candidate) => setEditCandidateProps({...editCandidateProps, candidate:candidate})}
                onSubmit={() => createCandidate(editCandidateProps.candidate)}
            />}/>
            <Route path={"/editCandidate/"} element={<CandidateForm
                candidate={editCandidateProps.candidate}
                editMode={editCandidateProps.editMode}
                error={editCandidateProps.error}
                onEdit={(candidate) => setEditCandidateProps({...editCandidateProps, candidate:candidate})}
                onSubmit={() => updateCandidate(editCandidateProps.candidate)}
            />}/>
            <Route path={"/vote/"} element={<AddVoteForm
                election={currentElection}
                setElection={(election) => {setCurrentElection(election); setNewVote(defaultVote)}}
                allElections={elections}
                allCandidates={candidates}
                vote={newVote}
                setVote={setNewVote}
                onVoteSubmit={submitVote}
                setValidationCode={code => setNewVote({...newVote, validationCode:code})}
            />}/>
            <Route path={"/archive/"} element={<ElectionTable
                    elections={elections}
                    candidates={candidates}
                    onGetResult={getElectionResults}
                    isArchive={true}
                    onCreateElection={() => {}}
                    onEditElection={() => {}}
                    onAdvanceElection={() => {}}
                    onVote={() => {}}
                    onDeleteElection={setConfirmDeleteElection}
                    isAdmin={!!user}
            />}/>
            <Route path={"/result/"} element={<ResultTable
            result={result}
            resultAnalysis={resultAnalysis}
            resultCondorcet={resultCondorcet}
            allCandidates={candidates}/>}/>
            <Route path={"/error/"} element={
                <ErrorMessage error={error}/>}/>
            <Route path={"/voterEmail/"} element={
               <VoterEMailForm
                   elections={elections}
                   onSubmit={getVoterCodes}
                   voterList={voterCodes}/>
            }/>
        </Routes>

        {confirmDeleteElection != null && (
            <ModalConfirmation title={"Confirm Delete "+confirmDeleteElection.name}
                               onClose={() => setConfirmDeleteElection(null)}
                               onConfirm={() => {deleteElection(confirmDeleteElection);
                                   setConfirmDeleteElection(null)}}>
                <p>
                    The election {confirmDeleteElection.name} will be deleted permanently.
                </p>
            </ModalConfirmation>
        )}

        {confirmDeleteCandidate != null && (
            <ModalConfirmation title={"Confirm Delete "+confirmDeleteCandidate.name}
                               onClose={() => setConfirmDeleteCandidate(null)}
                               onConfirm={() => {deleteCandidate(confirmDeleteCandidate);
                                   setConfirmDeleteCandidate(null)}}>
                <p>
                    The candidate {confirmDeleteCandidate.name} will be deleted permanently.
                </p>
            </ModalConfirmation>
        )}

        {confirmRetireCandidate != null && (
            <ModalConfirmation title={"Confirm Archiving "+confirmRetireCandidate.name}
                               onClose={() => setConfirmRetireCandidate(null)}
                               onConfirm={() => {retireCandidate(confirmRetireCandidate);
                                   setConfirmRetireCandidate(null)}}>
                <p>
                    The candidate {confirmRetireCandidate.name} will be archived and can no longer be assigned to elections.
                </p>
            </ModalConfirmation>
        )}

    </div>
  )
}

export default App
