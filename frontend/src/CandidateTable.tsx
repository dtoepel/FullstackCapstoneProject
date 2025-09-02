import type {Candidate, Election} from "./ElectionData.ts";
import {type ChangeEvent, useState} from "react";

export type CandidateTableProps = {
    value:Candidate[];
    elections:Election[];
    onCreateCandidate:()=>void;
    onEditCandidate:(candidate:Candidate)=>void;
}

function getAllTypes(candidates : Candidate[]):string[] {
    const types:string[] = [];

    candidates
    .filter(candidate => candidate.type)
    .forEach(candidate => {
        const index = types.indexOf(candidate.type);
        if(index < 0) {
            types.push(candidate.type);
        }
    })
    return types;
}

function getPreviousButton(
    page: number,
    setPage: (value:number) => void) {

    if(page > 0) {
        return(<button onClick={() => setPage(page-1)}>&lt;&lt;</button>)
    } else {
        return(<button disabled>&lt;&lt;</button>)
    }
}

function getNextButton(
    maxPages: number,
    page: number,
    setPage: (value:number) => void) {

    if(page < maxPages-1) {
        return(<button onClick={() => setPage(page+1)}>&gt;&gt;</button>)
    } else {
        return(<button disabled>&gt;&gt;</button>)
    }
}

function getPageSelector(
    maxPages: number,
    page: number,
    setPage: (value:number) => void) {
    const pageNums:number[] = [];

    for (let i:number = 0; i < maxPages; i++) {
        pageNums.push(i);
    }

    return(
        <select onChange={e =>
            setPage(Number.parseInt(e.target.value))
        }>
            {pageNums.map(n => {
                if(n==page)
                    return(<option key={"page-option"+n} value={n+""} selected>Page {n+1}</option>)
                else
                    return(<option key={"page-option"+n} value={n+""}>Page {n+1}</option>)
            })}
        </select>
    )
}

function getDeleteButton(candidate:Candidate, elections:Election[]) {
    const candidateIsInUse:boolean = elections.filter(election => election.candidateIDs.indexOf(candidate.id)>=0).length>0;
    if(candidateIsInUse)
        return (<button disabled>Delete</button>)
    else
        return (<button disabled><s>Delete</s></button>)
}

export default function CandidateTable(props:Readonly<CandidateTableProps>) {
    const types:string[] = getAllTypes(props.value);

    const [typeFilter, setTypeFilter] = useState<string|null>(null);
    const [page, setPage] = useState<number>(0);
    const filteredCandidates:Candidate[] = props.value
        .filter(candidate => {return typeFilter===null || typeFilter===candidate.type});
    const itemCount:number = filteredCandidates.length;
    const maxItems:number = 12;
    const maxPages:number = itemCount==0?1:Math.floor((itemCount + maxItems - 1) / maxItems);
    const candidatesOnPage:Candidate[] = filteredCandidates.slice(maxItems * page, maxItems * (page+1))

    function filterChanged(e:ChangeEvent<HTMLSelectElement>):void {
        const selectedType = e.target.value;
        setTypeFilter(selectedType === "Show All"?null:selectedType);
        setPage(0);
    }

    return(
        <>
            <div style={{display:"flex", flexDirection:"row", alignItems:"baseline"}}>
                <label htmlFor="candidate-types">Filter by Type:</label>
                <select name="candidate-types" id="candidate-types" onChange={filterChanged}>
                    <option value={"Show All"}>Show All</option>
                    {types.map(type => { return(
                        <option value={type}>{type}</option>
                    )})}
                </select>
                {getPreviousButton(page, setPage)}
                {getPageSelector(maxPages, page, setPage)}
                {getNextButton(maxPages, page, setPage)}
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
                                <button onClick={() => props.onEditCandidate(candidate)}>Edit</button>
                                <button disabled><s>Retire</s></button>
                                {getDeleteButton(candidate, props.elections)}
                            </td>
                        </tr>
                    )
                })}
                </tbody>
            </table>

        </>
    )
}