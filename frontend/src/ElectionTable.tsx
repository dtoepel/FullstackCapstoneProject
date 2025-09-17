import type {Candidate, Election} from "./ElectionData.ts";
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
        </>
    )
}