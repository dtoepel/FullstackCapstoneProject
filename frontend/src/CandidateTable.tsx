import {type Candidate, type Election, getAllCandidateTypes} from "./ElectionData.ts";
import {type ChangeEvent, useState} from "react";
import PageSelector from "./PageSelector.tsx";

export type CandidateTableProps = {
    value:Candidate[];
    elections:Election[];
    onCreateCandidate:()=>void;
    onEditCandidate:(candidate:Candidate)=>void;
    onRetireCandidate:(candidate:Candidate)=>void;
    onDeleteCandidate:(candidate:Candidate)=>void;
}

export default function CandidateTable(props:Readonly<CandidateTableProps>) {
    const types:string[] = getAllCandidateTypes(props.value);
    const [typeFilter, setTypeFilter] = useState<string|null>(null);
    const [showArchived, setShowArchived] = useState<boolean>(false);
    const [page, setPage] = useState<number>(0);
    const filteredCandidates:Candidate[] = props.value
        .filter(candidate => {return (typeFilter===null || typeFilter===candidate.type) && (showArchived || !candidate.archived)});
    const itemCount:number = filteredCandidates.length;
    const maxItems:number = 12;
    const maxPages:number = itemCount==0?1:Math.floor((itemCount + maxItems - 1) / maxItems);
    const candidatesOnPage:Candidate[] = filteredCandidates.slice(maxItems * page, maxItems * (page+1));

    function getDeleteButton(candidate:Candidate, elections:Election[]) {
        const candidateIsInUse:boolean = elections.filter(election => election.candidateIDs.indexOf(candidate.id)>=0).length>0;
        if(candidateIsInUse)
            return (<button disabled>Delete</button>)
        else
            return (<button onClick={() => props.onDeleteCandidate(candidate)}>Delete</button>)
    }

    function filterChanged(e:ChangeEvent<HTMLSelectElement>):void {
        const selectedType = e.target.value;
        setTypeFilter(selectedType === "Show All"?null:selectedType);
        setPage(0);
    }

    return(
        <div style={{display:"flex", flexDirection:"column", alignItems:"stretch"}}>
            <div style={{display:"flex", flexDirection:"row", alignItems:"baseline", justifyContent:"space-evenly"}}>
                <div style={{display:"flex", flexDirection:"row", alignItems:"baseline", justifyContent:"center"}}>
                    <label htmlFor="candidate-types">Filter by Type:</label>
                    <select name="candidate-types" id="candidate-types" onChange={filterChanged}>
                        <option value={"Show All"}>Show All</option>
                        {types.map(type => {
                            return (
                                <option value={type}>{type}</option>
                            )
                        })}
                    </select>
                </div>
                <div style={{display:"flex", flexDirection:"row", alignItems:"baseline", justifyContent:"center"}}>
                    <label>Show Archived:</label><br/>
                    <input type="checkbox" checked={showArchived} onChange={e => setShowArchived(e.target.checked)}/>
                </div>
                <PageSelector page={page} setPage={setPage} maxPages={maxPages}/>
                <button onClick={() => props.onCreateCandidate()}>Create</button>
            </div>

            <table border={1}>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Party</th>
                    <th>Info</th>
                    <th>Type</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {   candidatesOnPage
                    .map((candidate) => {
                    return (
                        <tr key={candidate.id}>
                            <td>{candidate.name}</td>
                            <td>{candidate.party}</td>
                            <td>{candidate.description}</td>
                            <td>{candidate.type}</td>
                            <td>
                                {candidate.archived?"":<>
                                    <button onClick={() => props.onEditCandidate(candidate)}>Edit</button>
                                    <button onClick={() => props.onRetireCandidate(candidate)}>Retire</button>
                                    </>}
                                {getDeleteButton(candidate, props.elections)}
                            </td>
                        </tr>
                    )
                })}
                </tbody>
            </table>
        </div>
    )
}