import './App.css'

import electionLogo from './assets/election.svg'
import candidatesLogo from './assets/candidates.svg'
import archiveLogo from './assets/archive-inv.svg'
import logoutLogo from './assets/logout.svg'
import loginLogo from './assets/login.svg'
import voteLogo from './assets/vote.svg'

import type {Candidate, Election} from "./ElectionData.ts";

import ElectionTable from "./ElectionTable.tsx";
import NavigationItem from "./NavigationItem.tsx";
import CandidateTable from "./CandidateTable.tsx";
import ElectionForm from "./ElectionForm.tsx";
import CandidateForm from "./CandidateForm.tsx";

import {Route, Routes, useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";

function App() {
    const nav = useNavigate();

    // main model
    const [elections, setElections] = useState<Election[]>([]);
    const [candidates, setCandidates] = useState<Candidate[]>([]);

    // default values for forms
    const defaultElection:Election = {
        name:"new Election",id:"ABC00",candidateIDs:[],votes:[],electionState:"OPEN",
        description:"Enter description here...",seats:1,electionMethod:"STV",candidateType:"Person"
    };
    const defaultCandidate:Candidate = {
        id:"(automatically assigned)", name:"John Doe",
        description:"an average candidate",
        party:"Independent", color:"888", type:"Person", archived:false
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


    // only place to update data
    // could be split to reduce traffic by a small amount
    function getAllElectionsAndCandidates():void {
        axios.get("/api/election").then(
            (response) => {
                setElections(response.data);
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
                console.log(error);
                console.log(error.status);
                if(error.status == 403) {
                    console.log(error.response.data.message);
                    setEditElectionProps({...editElectionProps, error:error.response.data.message})
                }
            })
    }

    function updateElection(election:Election):void {
        axios.put("/api/election", election)
            .then(() => {getAllElectionsAndCandidates(); editElectionProps.onSuccess()})
            .catch(error => {
                if(error.response && error.response.status == 404) {
                    setEditElectionProps({...editElectionProps, error:error.response.data.message})
                }
            })
    }

    function openVoting(election:Election):void {
        axios.put("/api/election/status" + election.id, "VOTING")
            .then(() => {getAllElectionsAndCandidates(); editElectionProps.onSuccess()})
            .catch(error => { console.log(error) })
    }

    function closeVoting(election:Election):void {
        axios.put("/api/election/status" + election.id, "CLOSED")
            .then(() => {getAllElectionsAndCandidates(); editElectionProps.onSuccess()})
            .catch(error => { console.log(error) })
    }

    function archiveElection(election:Election):void {
        axios.put("/api/election/status" + election.id, "ARCHIVED")
            .then(() => {getAllElectionsAndCandidates(); editElectionProps.onSuccess()})
            .catch(error => { console.log(error) })
    }

    function deleteElection(election:Election):void {
        axios.delete("/api/election/" + election.id)
            .then(() => {getAllElectionsAndCandidates(); editElectionProps.onSuccess()})
            .catch(error => { console.log(error) })
    }

    function createCandidate(candidate:Candidate):void {
        axios.post("/api/election/candidates", candidate)
            .then(() => {getAllElectionsAndCandidates(); editCandidateProps.onSuccess()})
            .catch(error => {
                console.log(error);
                console.log(error.status);
                if(error.status == 403) {
                    console.log(error.response.data.message);
                    setEditCandidateProps({...editCandidateProps, error:error.response.data.message})
                }
            })
    }

    function updateCandidate(candidate:Candidate) {
        axios.put("/api/election/candidates", candidate)
            .then(() => {getAllElectionsAndCandidates(); editCandidateProps.onSuccess()})
            .catch(error => {
                console.log(error);
                if(error.response && error.response.status == 404) {
                    setEditCandidateProps({...editCandidateProps, error:error.response.data.message})
                }
            })
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
                console.log(response.data)
            }
        )
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
            <NavigationItem
                text={"Candidates"}
                symbolFile={candidatesLogo}
                onClick={() => nav("/candidates/")}/>
            <NavigationItem
                text={"Vote"}
                symbolFile={voteLogo}
                onClick={() => nav("/vote/")}/>
            <NavigationItem
                text={"Archive"}
                symbolFile={archiveLogo}
                onClick={() => nav("/archive/")}/>
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
                onOpenVoting={openVoting}
                onCloseVoting={closeVoting}
                onArchiveElection={archiveElection}
                onDeleteElection={deleteElection}
            />}/>
            <Route path={"/createElection/"} element={<ElectionForm
                election={editElectionProps.election}
                candidates={candidates}
                editMode={editElectionProps.editMode}
                error={editElectionProps.error}
                onEdit={(election) => setEditElectionProps({...editElectionProps, election:election})}
                onSubmit={createElection}
            />}/>
            <Route path={"/editElection/"} element={<ElectionForm
                election={editElectionProps.election}
                candidates={candidates}
                editMode={editElectionProps.editMode}
                error={editElectionProps.error}
                onEdit={(election) => setEditElectionProps({...editElectionProps, election:election})}
                onSubmit={() => updateElection(editElectionProps.election)}
            />}/>
            <Route path={"/candidates/"} element={<CandidateTable
                value={candidates}
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
            <Route path={"/vote/"} element={"This is the vote page"}/>
            <Route path={"/archive/"} element={<ElectionTable
                    elections={elections}
                    candidates={candidates}
                    onGetResult={getElectionResults}
                    isArchive={true}
                    onCreateElection={() => {}}
                    onEditElection={() => {}}
                    onOpenVoting={() => {}}
                    onCloseVoting={() => {}}
                    onArchiveElection={() => {}}
                    onDeleteElection={deleteElection}
            />}/>
            <Route path={"/result/"} element={"This is the result page"}/>
        </Routes>
    </div>
  )
}

export default App
