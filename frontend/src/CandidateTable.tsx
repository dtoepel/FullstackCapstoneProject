import type {Candidate} from "./ElectionData.ts";
import {type ChangeEvent, useState} from "react";

export type CandidateTableProps = {
    value:Candidate[];
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

export default function CandidateTable(props:Readonly<CandidateTableProps>) {
    const types:string[] = getAllTypes(props.value);

    const [typeFilter, setTypeFilter] = useState<string|null>(null);

    function filterChanged(e:ChangeEvent<HTMLSelectElement>):void {
        const selectedType = e.target.value;
        setTypeFilter(selectedType === "Show All"?null:selectedType);
    }

    return(
        <>
            <div style={{display:"flex", flexDirection:"row"}}>
                <label htmlFor="candidate-types">Filter by Type:</label>
                <select name="candidate-types" id="candidate-types" onChange={filterChanged}>
                    <option value={"Show All"}>Show All</option>
                    {types.map(type => { return(
                        <option value={type}>{type}</option>
                    )})}
                </select>
            </div>

            <table border={1}>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Party</th>
                    <th>Info</th>
                    <th>Type</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {props.value
                    .filter(candidate => {return typeFilter===null || typeFilter===candidate.type})
                    .map((candidate) => {
                    return (
                        <tr>
                            <td>{candidate.id}</td>
                            <td>{candidate.name}</td>
                            <td>{candidate.party}</td>
                            <td>{candidate.description}</td>
                            <td>{candidate.type}</td>
                            <td>TBD</td>
                        </tr>
                    )
                })}
                </tbody>
            </table>

        </>
    )
}