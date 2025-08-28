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

import {Route, Routes, useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";

function App() {
    const nav = useNavigate();

    // main model
    const [elections, setElections] = useState<Election[]>([]);
    const [candidates, setCandidates] = useState<Candidate[]>([]);

    // default values for froms
    const defaultElection:Election = {
        name:"new Election",id:"ABC00",candidateIDs:[],votes:[],electionState:"OPEN",
        description:"Enter description here...",seats:1,electionMethod:"STV",candidateType:"Person"
    };

    // temporary variables for forms
    const [editElection, setEditElection] = useState<Election>(defaultElection);


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
        axios.post("/api/election", editElection)
            .then(() => {getAllElectionsAndCandidates(); nav("/")})
            .catch(error => {
                console.log(error);
                if(error.response && error.response.code == 400) {
                    console.log("error handling");
                }
            })
    }

    function updateElection(election:Election) {
        axios.put("/api/election", election)
            .then(getAllElectionsAndCandidates)
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
                value={elections}
                onCreateElection={() => {
                    setEditElection(defaultElection);
                    nav("/createElection/")
                }}
                onEditElection={(election) => {
                    setEditElection(election);
                    nav("/editElection/")
                }}
            />}/>
            <Route path={"/createElection/"} element={<ElectionForm
                election={editElection}
                candidates={candidates}
                editMode={false}
                onEdit={setEditElection}
                onSubmit={createElection}
            />}/>
            <Route path={"/editElection/"} element={<ElectionForm
                election={editElection}
                candidates={candidates}
                editMode={true}
                onEdit={setEditElection}
                onSubmit={() => updateElection(editElection)}
            />}/>
            <Route path={"/candidates/"} element={<CandidateTable value={candidates}/>}/>
            <Route path={"/vote/"} element={"This is the vote page"}/>
            <Route path={"/archive/"} element={"This is the archive page"}/>
            <Route path={"/result/"} element={"This is the result page"}/>
        </Routes>
    </div>
  )
}

export default App
