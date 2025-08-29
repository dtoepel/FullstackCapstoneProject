import type {Candidate, Election} from "./ElectionData.ts";
import CandidateBox from "./CandidateBox.tsx";

export type ElectionTableProps = {
    elections:Election[];
    candidates:Candidate[];
    onCreateElection:()=>void;
    onEditElection:(election:Election)=>void;
    onGetResult:(election:Election)=>void;
    isArchive:boolean;
}

export default function ElectionTable(props:Readonly<ElectionTableProps>) {
    return(
        <>
        {props.isArchive?"":<button onClick={() => props.onCreateElection()}>Create</button>}
            <table border={1}>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Info</th>
                    <th>Candidates</th>
                    <th>Seats</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {props.elections
                    .filter(election => (election.electionState === "ARCHIVED") === props.isArchive)
                    .map((election) => {
                    let candidates:Candidate[] = [];
                    election.candidateIDs.forEach((id) => {
                        candidates = candidates.concat(props.candidates.filter(
                            candidate => candidate.id == id))
                    });

                    return(
                        <tr key={election.id}>
                            <td>{election.id}</td>
                            <td>{election.name}</td>
                            <td>{election.description}</td>
                            <td><div style={{display:"flex", flexDirection:"row", flexWrap:"wrap"}}>
                                {candidates.map(candidate => {return(
                                    <CandidateBox candidate={candidate}/>
                                )})}
                            </div></td>
                            <td>{election.seats}</td>
                            <td>
                                {election.electionState === "OPEN"?
                                    <>
                                        <button onClick={() => props.onEditElection(election)}>Edit</button>
                                        <button><s>Open Voting</s></button>
                                        <button onClick={() => props.onGetResult(election)}>(Peek)</button>
                                    </>
                                :election.electionState === "VOTING"?
                                    <>
                                        <button><s>Close Voting</s></button>
                                        <button><s>Vote</s></button>
                                        <button onClick={() => props.onGetResult(election)}>(Peek)</button>
                                    </>
                                :election.electionState === "CLOSED"?
                                    <>
                                        <button onClick={() => props.onGetResult(election)}>Result</button>
                                        <button><s>Archive</s></button>
                                    </>
                                :
                                    <>
                                        <button onClick={() => props.onGetResult(election)}>Result</button>
                                    </>
                                }
                                <button><s>Delete</s></button>
                            </td>
                        </tr>
                    )})}
                </tbody>
            </table>

        </>
    )
}