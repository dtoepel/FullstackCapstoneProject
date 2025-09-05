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

    function getElectionSelector() {
        return(
            <select id={"selectElection"} onChange={electionChanged}>
                {props.allElections.map(election => {
                    if (election == props.election) return (
                        <option value={election.id} key={election.id} selected>{election.name}</option>)
                    else return (
                        <option value={election.id} key={election.id}>{election.name}</option>)
                })}
            </select>
        )
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

    if(props.election == null && props.allElections.length>0) {
        const election = props.allElections.at(0);
        if(election) props.setElection(election);
    }

    return(<>
            {getElectionSelector()}
            <div className={"ballot"} style={{maxWidth:"min(500px,95vw)"}}>
                <p style={{fontWeight:"bold",fontSize:"48px",color:"var(--mydarkblue)"}}>Ballot</p>
                <div className={"ballot-item"} style={{flexDirection:"column",backgroundColor:"var(--mydarkblue)"}}>
                    <p style={{fontWeight:"bold",fontSize:"20px",color:"#fff"}}>No. {ballotNo}</p>
                    <p style={{fontWeight:"bold",fontSize:"12px",color:"#fff"}}>for the</p>
                    <p style={{fontWeight:"bold",fontSize:"20px",color:"#fff"}}>{props.election?props.election.name:"(Election not selected!)"}</p>
                </div>
                <div className={"ballot-item"} style={{flexDirection:"row"}}>
                    <p style={{fontWeight:"bold",fontSize:"16px",color:"var(--mydarkblue)"}}>Validation Code:</p>
                    <input style={{width:"33%",margin:"0 0 0 10px"}}/>
                </div>
                <div className={"ballot-item"} style={{flexDirection:"column",backgroundColor:"var(--mydarkblue)"}}>
                    <p style={{fontWeight:"bold",fontSize:"24px",color:"#fff"}}>Ranked Candidates:</p>
                    <div style={{display:"flex", flexDirection:"column"}}>
                        {ranked.map((candidate, index, array) => {return (
                            <CandidateBox key={candidate.id}
                                          candidate={candidate}
                                          onDelete={() => {voteRemove(candidate)}}
                                          onUp={() => {voteUp(candidate)}}
                                          onDown={() => {voteDown(candidate)}}
                                          deleteAvailable={true}
                                          upAvailable={index > 0}
                                          downAvailable={index < array.length-1}
                                          rank={index+1}
                                          color={"var(--myblue)"}
                            />
                        )})}
                    </div>
                </div>
                <div className={"ballot-item"} style={{flexDirection:"column"}}>
                    <p style={{fontWeight:"bold",fontSize:"24px",color:"var(--mydarkblue)"}}>Unranked Candidates:</p>
                    <div style={{display:"flex", flexDirection:"row",flexWrap:"wrap",alignContent:"flex-start"}}>
                        {unranked.map((candidate) => {return (
                            <CandidateBox key={candidate.id} candidate={candidate}
                                          addAvailable={true} onAdd={() => {voteAdd(candidate)}}
                                          color={"var(--mydarkblue)"}/>
                        )})}
                    </div>
                </div>
                <form onSubmit={e=>submit(e)}>
                    <button style={{backgroundColor:"var(--mydarkblue)", color:"#fff",
                        fontWeight:"bold",fontSize:"24px"}}>Submit</button></form>
            </div>
        </>
    )
}