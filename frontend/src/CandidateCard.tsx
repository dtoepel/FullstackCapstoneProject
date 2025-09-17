import type {Candidate, Election} from "./ElectionData.ts";

export type CandidateCardProps = {
    candidate:Candidate;
    elections:Election[];
    onEditCandidate:()=>void;
    onRetireCandidate:()=>void;
    onDeleteCandidate:()=>void;
}

export default function CandidateCard(props:Readonly<CandidateCardProps>) {

    function getDeleteButton(candidate:Candidate, elections:Election[]) {
        const candidateIsInUse:boolean = elections.filter(election => election.candidateIDs.indexOf(candidate.id)>=0).length>0;
        if(candidateIsInUse)
            return (<button disabled>Delete</button>)
        else
            return (<button onClick={() => props.onDeleteCandidate()}>Delete</button>)
    }

    return(
        <div className={"candidate-table-item"}>
            <div style={{backgroundColor:"#"+props.candidate.color,color:"#fff",
                display:"flex", flexDirection:"column", alignItems:"stretch",
                borderRadius: "16px 96px 16px 16px /  16px 48px 16px 16px",
                minHeight:"100px",margin:"0", boxShadow:"2px 2px 6px black"
            }}>
                <p style={{fontSize:"12px",alignSelf:"flex-start",padding:"0 0 0 12px",
                    margin:"0",color:"#fff",textShadow:"1px 1px 3px black",}}>
                {props.candidate.party}</p>
                <p style={{fontWeight:"bold",fontSize:"24px",
                    color:"#fff", textShadow:"1px 1px 2px black",
                    margin:"12px 4px 0 4px"}}>
                    {props.candidate.name}</p>
            </div>
            <p style={{textAlign:"left",color:"var(--mydarkgray)",
                minHeight:"100px"}}>{props.candidate.description}</p>
            <div>
                {props.candidate.archived?"":<>
                    <button onClick={() => props.onEditCandidate()}>Edit</button>
                    <button onClick={() => props.onRetireCandidate()}>Retire</button>
                </>}
                {getDeleteButton(props.candidate, props.elections)}
            </div>
        </div>
    )
}