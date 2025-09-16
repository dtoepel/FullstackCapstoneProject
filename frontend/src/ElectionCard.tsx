import type {Candidate, Election} from "./ElectionData.ts";
import CandidateBox from "./CandidateBox.tsx";

export type ElectionCardProps = {
    election:Election;
    candidates:Candidate[];
    onEditElection:()=>void;
    onAdvanceElection:()=>void;
    onGetResult:()=>void;
    onDeleteElection:()=>void;
    onVote:()=>void;
    isArchive:boolean;
    isAdmin:boolean;
}

export default function ElectionCard(props: Readonly<ElectionCardProps>) {
    let candidates:Candidate[] = [];
    props.election.candidateIDs.forEach((id) => {
        candidates = candidates.concat(props.candidates.filter(
            candidate => candidate.id == id))
    });


    function getDeleteButton() {
        if(props.isAdmin) {
            return (<button onClick={() => props.onDeleteElection()}>Delete</button>)
        } else {
            return (<button disabled>Delete</button>)
        }
    }

    function getResultButton() {
        return (<button onClick={() => props.onGetResult()}>Result</button>)
    }

    function getEditButton() {
        if(props.isAdmin) {
            return (<button onClick={() => props.onEditElection()}>Edit</button>)
        } else {
            return (<button onClick={() => props.onEditElection()}>Show</button>)
        }
    }

    function getAdvanceButton(label:string) {
        if(props.isAdmin) {
            return (<button onClick={() => props.onAdvanceElection()}>{label}</button>)
        } else {
            return (<button disabled>{label}</button>)
        }
    }

    function getVoteButton() {
        if(!props.isAdmin) {
            return (<button onClick={() => props.onVote()}>Vote</button>)
        } else {
            return("")
        }
    }

    function getButtons(election:Election) {
        if(election.electionState==="OPEN") {
            return(<>
                {getEditButton()}
                {getAdvanceButton("Open Voting")}
                <button onClick={() => props.onGetResult()}>(Peek)</button>
                {getDeleteButton()}
            </>)
        } else if(election.electionState==="VOTING") {
            return(<>
                {getEditButton()}
                {getAdvanceButton("Close Voting")}
                {getVoteButton()}
                <button onClick={() => props.onGetResult()}>(Peek)</button>
                {getDeleteButton()}
            </>)
        } else if(election.electionState==="CLOSED") {
            return(<>
                {getResultButton()}
                {getAdvanceButton("Archive")}
                {getDeleteButton()}
            </>)
        } else { // ARCHIVED
            return (<>
                {getResultButton()}
                {getDeleteButton()}
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
        <div className={"election-table-item"}>

            <div style={{backgroundColor:"var(--mydarkblue)",color:"#fff",
                display:"flex", flexDirection:"column", alignItems:"stretch",
                borderRadius: "16px 96px 16px 16px /  16px 48px 16px 16px",
                minHeight:"100px",margin:"0"
            }}>
                <p style={{alignSelf:"flex-start",padding:"0 0 0 12px",margin:"0",color:"var(--myblue)"}}>
                    {props.election.id}</p>
                <p style={{fontWeight:"bold",fontSize:"24px",margin:"0",color:"#fff"}}>
                    {props.election.name}</p>
            </div>
            <p style={{textAlign:"left",color:"var(--mydarkblue)",
                minHeight:"100px"}}>{props.election.description}</p>
            <div style={{display:"flex", flexDirection:"row", flexWrap:"wrap",backgroundColor:"black",borderRadius:"9px"}}>
                {candidates.map(candidate => {return(
                    <CandidateBox candidate={candidate} key={candidate.id}/>
                )})}
            </div>
            <p style={{color:"var(--mydarkblue)"}}>
                {props.election.seats} {props.election.seats==1?"seat":"seats"} to be distributed by Single Transferable Vote
            </p>
            <p style={{color:"white",backgroundColor:"var(--mydarkblue)"}}>
                {getVotes(props.election)}
            </p>
            <div style={{margin:"4px"}}>
                {getButtons(props.election)}
            </div>
        </div>
    )
}