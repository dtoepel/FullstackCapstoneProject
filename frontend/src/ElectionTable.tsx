import type {Election} from "./ElectionData.ts";

export type ElectionTableProps = {
    value:Election[];
    onCreateElection:()=>void;
    onEditElection:(election:Election)=>void;
}

export default function ElectionTable(props:Readonly<ElectionTableProps>) {
    return(
        <>
            <button onClick={() => props.onCreateElection()}>Create</button>
            <table border={1}>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Status</th>
                    <th>Info</th>
                    <th>Candidates</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {props.value.map((election) => {

                    return(
                        <tr key={election.id}>
                            <td>{election.id}</td>
                            <td>{election.name}</td>
                            <td>{election.electionState}</td>
                            <td>{election.description}</td>
                            <td>TBD</td>
                            <td>
                                <button onClick={() => props.onEditElection(election)}>Edit</button>
                            </td>
                        </tr>
                    )})}
                </tbody>
            </table>

        </>
    )
}