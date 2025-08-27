import './App.css'
import {Link, Route, Routes} from "react-router-dom";
import {useEffect, useState} from "react";
import type {Election} from "./ElectionData.ts";
import ElectionTable from "./ElectionTable.tsx";
import axios from "axios";

function App() {

    // main model
    const [elections, setElections] = useState<Election[]>([]);

    // only place to update data
    // could be split to reduce traffic by a small amount
    function getAllElectionsAndCandidates():void {
        axios.get("/api/election").then(
            (response) => {
                setElections(response.data);
                /*axios.get("/api/election/candidates").then(
                    (response) => {
                        setCandidates(response.data);
                    }
                )*/
            }
        )
    }

    useEffect(() => {
        getAllElectionsAndCandidates()
    },[])

    // authorization
    const [user, setUser] = useState<string | undefined | null>(undefined);

    const loadUser = () => {
        axios.get('/api/auth/me')
            .then(response => {
                console.log(response); setUser(response.data)
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
    <>
        <h1>Election Manager</h1>
        <h3>User: {user === undefined ? "undefined" : user === null ? "null" : user}</h3>
        <Routes>
            <Route path={"/"} element={<ElectionTable value={elections} />}/>
        </Routes>
        <br/>
        <hr/>
        <Link to={"/"}>Elections</Link>
        &nbsp;-&nbsp;
        <button onClick={login}>Login</button>
        &nbsp;-&nbsp;
        <button onClick={logout}>Logout</button>
    </>
  )
}

export default App
