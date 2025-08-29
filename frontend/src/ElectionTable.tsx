import type {Candidate, Election} from "./ElectionData.ts";
import CandidateBox from "./CandidateBox.tsx";

export type ElectionTableProps = {
    elections:Election[];
    candidates:Candidate[];
    onCreateElection:()=>void;
    onEditElection:(election:Election)=>void;
    onGetResult:(election:Election)=>void;
}

export default function ElectionTable(props:Readonly<ElectionTableProps>) {
    return(
        <>
            <button onClick={() => props.onCreateElection()}>Create</button>
            <table border={1}>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Status</th>
                    <th>Info</th>
                    <th>Candidates</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {props.elections.map((election) => {
                    let candidates:Candidate[] = [];
                    election.candidateIDs.forEach((id) => {
                        candidates = candidates.concat(props.candidates.filter(
                            candidate => candidate.id == id))
                    });

                    return(
                        <tr key={election.id}>
                            <td>{election.id}</td>
                            <td>{election.name}</td>
                            <td>{election.electionState}</td>
                            <td>{election.description}</td>
                            <td><div style={{display:"flex", flexDirection:"row", flexWrap:"wrap"}}>
                                {candidates.map(candidate => {return(
                                    <CandidateBox candidate={candidate}/>
                                )})}
                            </div></td>
                            <td>
                                <button onClick={() => props.onEditElection(election)}>Edit</button>
                                <button onClick={() => props.onGetResult(election)}>Result</button>
                            </td>
                        </tr>
                    )})}
                </tbody>
            </table>

        </>
    )
}