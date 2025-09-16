import type {Candidate, Election} from "./ElectionData.ts";
import CandidateBox from "./CandidateBox.tsx";
import {useState} from "react";
import PageSelector from "./PageSelector.tsx";
import ElectionCard from "./ElectionCard.tsx";

export type ElectionTableProps = {
    elections:Election[];
    candidates:Candidate[];
    onCreateElection:()=>void;
    onEditElection:(election:Election)=>void;
    onAdvanceElection:(election:Election)=>void;
    onGetResult:(election:Election)=>void;
    onDeleteElection:(election:Election)=>void;
    onVote:(election:Election)=>void;
    isArchive:boolean;
    isAdmin:boolean;
}

export default function ElectionTable(props:Readonly<ElectionTableProps>) {
    const [page, setPage] = useState<number>(0);
    const filteredElections:Election[] = props.elections
        .filter(election => (election.electionState === "ARCHIVED") === props.isArchive)
    const itemCount:number = filteredElections.length;
    const maxItems:number = 4;
    const maxPages:number = itemCount==0?1:Math.floor((itemCount + maxItems - 1) / maxItems);
    if(page >= maxPages) setPage(0);
    const electionsOnPage:Election[] = filteredElections.slice(maxItems * page, maxItems * (page+1));

    function getDeleteButton(election:Election) {
        if(props.isAdmin) {
            return (<button onClick={() => props.onDeleteElection(election)}>Delete</button>)
        } else {
            return (<button disabled>Delete</button>)
        }
    }

    function getResultButton(election:Election) {
        return (<button onClick={() => props.onGetResult(election)}>Result</button>)
    }

    function getEditButton(election:Election) {
        if(props.isAdmin) {
            return (<button onClick={() => props.onEditElection(election)}>Edit</button>)
        } else {
            return (<button onClick={() => props.onEditElection(election)}>Show</button>)
        }
    }

    function getAdvanceButton(election:Election, label:string) {
        if(props.isAdmin) {
            return (<button onClick={() => props.onAdvanceElection(election)}>{label}</button>)
        } else {
            return (<button disabled>{label}</button>)
        }
    }

    function getVoteButton(election:Election) {
        if(!props.isAdmin) {
            return (<button onClick={() => props.onVote(election)}>Vote</button>)
        } else {
            return("")
        }
    }

    function getButtons(election:Election) {
        if(election.electionState==="OPEN") {
            return(<>
                {getEditButton(election)}
                {getAdvanceButton(election, "Open Voting")}
                <button onClick={() => props.onGetResult(election)}>(Peek)</button>
                {getDeleteButton(election)}
            </>)
        } else if(election.electionState==="VOTING") {
            return(<>
                {getEditButton(election)}
                {getAdvanceButton(election, "Close Voting")}
                {getVoteButton(election)}
                <button onClick={() => props.onGetResult(election)}>(Peek)</button>
                {getDeleteButton(election)}
            </>)
        } else if(election.electionState==="CLOSED") {
            return(<>
                {getResultButton(election)}
                {getAdvanceButton(election, "Archive")}
                {getDeleteButton(election)}
            </>)
        } else { // ARCHIVED
            return (<>
                {getResultButton(election)}
                {getDeleteButton(election)}
            </>)
        }
    }

    function getVotes(election:Election):string {
        const totalVoters = election.voterEmails==null?0:election.voterEmails.length;
        const votesCast = election.votes.length;

        if(election.electionState == "OPEN") {
            return totalVoters + " Voters";
        } else if(election.electionState == "VOTING") {
            return votesCast + "/" + totalVoters + " Votes in";
        } else {
            return votesCast + "/" + totalVoters + " Turnout";
        }
    }

    return(
        <>
            <div style={{display:"flex", flexDirection:"row"}}>
                {(props.isArchive||!props.isAdmin)?"":<button onClick={() => props.onCreateElection()}>Create</button>}
                <PageSelector page={page} setPage={setPage} maxPages={maxPages}/>
            </div>
            <div className={"election-table"}>
                {electionsOnPage
                    .map((election) => { return (<ElectionCard
                        key={election.id}
                        election={election}
                        candidates={props.candidates}
                        onEditElection={() => props.onEditElection(election)}
                        onAdvanceElection={() => props.onAdvanceElection(election)}
                        onGetResult={() => props.onGetResult(election)}
                        onDeleteElection={() => props.onDeleteElection(election)}
                        onVote={() => props.onVote(election)}
                        isArchive={props.isArchive}
                        isAdmin={props.isAdmin}
                    />)}) }
            </div>
            <table border={1}>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Info</th>
                    <th>Candidates</th>
                    <th>Seats</th>
                    <th>Votes</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {electionsOnPage
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
                                    <CandidateBox candidate={candidate} key={candidate.id}/>
                                )})}
                            </div></td>
                            <td>{election.seats}</td>
                            <td>{getVotes(election)}</td>
                            <td>{getButtons(election)}</td>
                        </tr>
                    )})}
                </tbody>
            </table>

        </>
    )
}