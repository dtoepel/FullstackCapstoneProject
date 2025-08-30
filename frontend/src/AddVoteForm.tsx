import type {ChangeEvent, FormEvent} from "react";
import {useNavigate} from "react-router-dom";
import CandidateBox from "./CandidateBox.tsx";
import type {Candidate, Election, Vote} from "./ElectionData.ts";

export type AddVoteFormProps = {
    election:Election|null
    setElection:(election:Election)=>void;
    allElections:Election[];
    allCandidates:Candidate[];
    vote:Vote;
    setVote:(vote:Vote)=>void;
    onVoteSubmit:()=>void;
}

export default function AddVoteForm(props:Readonly<AddVoteFormProps>) {
    const nav = useNavigate();

    function submit(e:FormEvent<HTMLFormElement> ):void {
        e.preventDefault();
        props.onVoteSubmit();
    }

    function voteUp(candidate:Candidate):void {
        const index:number = props.vote.rankingIDs.indexOf(candidate.id, 0);
        if (index > 0) {
            props.vote.rankingIDs.splice(index, 1);
            props.vote.rankingIDs.splice(index-1, 0, candidate.id);
            props.setVote(props.vote);
            nav("/vote/");
        }
    }

    function voteDown(candidate:Candidate):void {
        const index:number = props.vote.rankingIDs.indexOf(candidate.id, 0);
        if (index > -1 && index < props.vote.rankingIDs.length-1) {
            props.vote.rankingIDs.splice(index, 1);
            props.vote.rankingIDs.splice(index+1, 0, candidate.id);
            props.setVote(props.vote);
            nav("/vote/");
        }
    }

    function voteAdd(candidate:Candidate):void {
        props.vote.rankingIDs.push(candidate.id);
        props.setVote(props.vote);
        nav("/vote/");
    }

    function voteRemove(candidate:Candidate):void {
        const index:number = props.vote.rankingIDs.indexOf(candidate.id, 0);
        if (index > -1) {
            props.vote.rankingIDs.splice(index, 1);
            props.setVote(props.vote);
            nav("/vote/");
        }
    }

    function electionChanged(event:ChangeEvent<HTMLSelectElement>):void {
        const matchingElections = props.allElections.filter(election => election.id===event.target.value)
        if(matchingElections.length == 1) {
            const election = matchingElections.at(0)
            if(election != undefined) {
                props.setElection(election)
            }
        }
    }

    const unrankedIDs:string[] = props.election==null?[]:props.election.candidateIDs.filter((cid) => props.vote.rankingIDs.indexOf(cid)<0)

    let unranked:Candidate[] = [];
     unrankedIDs.forEach((cid) => {
        unranked = unranked.concat(props.allCandidates.filter((cc) => cc.id == cid));
    })

    let ranked:Candidate[] = [];
    props.vote.rankingIDs.forEach((cid) => {
        ranked = ranked.concat(props.allCandidates.filter((cc) => cc.id == cid));
    })

    let ballotNo:string = props.election==null?"0":(""+(props.election.votes.length+1));
    while (ballotNo.length<5) {
        ballotNo = "0"+ballotNo;
    }
    ballotNo = ballotNo.substring(0,2)+"-"+ ballotNo.substring(2,3)+"-"+ ballotNo.substring(3,5)

    return(
            <table border={1}>
                <tbody>
                <tr>
                    <td>
                        <select id={"selectElection"} onChange={electionChanged}>
                            {props.allElections.map(election => {
                                return( <option value={election.id} key={election.id}>{election.name}</option>
                            )})}
                        </select>
                    </td>
                </tr>
                <tr>
                    <td colSpan={2}>Ballot No. {ballotNo}</td>
                </tr>
                <tr>
                    <td>
                        ranked Candidates
                    </td>
                    <td><div style={{
                        display:"flex",
                        flexDirection: "column",
                        alignItems: "flex-start"}}>{ranked.map((candidate, index, array) => {return (
                            <CandidateBox key={candidate.id}
                                candidate={candidate}
                                onDelete={() => {voteRemove(candidate)}}
                                onUp={() => {voteUp(candidate)}}
                                onDown={() => {voteDown(candidate)}}
                                deleteAvailable={true}
                                upAvailable={index > 0}
                                downAvailable={index < array.length-1}
                                rank={index+1}/>
                    )})}</div></td>
                </tr>
                <tr>
                    <td>
                        Unranked Candidates
                    </td>
                    <td><div style={{
                    display:"flex",
                    flexDirection: "column",
                    alignItems: "flex-start"}}>{unranked.map((candidate) => {return (
                        <CandidateBox key={candidate.id} candidate={candidate} addAvailable={true} onAdd={() => {voteAdd(candidate)}}/>
                )})}</div></td>
                </tr>
                <tr>
                    <td colSpan={2}>
                        <form onClick={(e)=>submit(e)}><button>Submit</button></form>
                    </td>
                </tr>
                </tbody>
            </table>
    )
}