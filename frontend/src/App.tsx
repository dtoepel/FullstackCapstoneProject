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


    return (
    <>
        <h1>Elections</h1>
        <Routes>
            <Route path={"/"} element={<ElectionTable value={elections} />}/>
        </Routes>
        <br/>
        <hr/>
        <Link to={"/"}>Main</Link>
        &nbsp;-&nbsp;
        ...
        &nbsp;-&nbsp;
        ...
    </>
  )
}

export default App
