import type {Election} from "./ElectionData.ts";

export type ElectionTableProps = {
    value:Election[];
    onGetResult:(election:Election)=>void;
}

export default function ElectionTable(props:Readonly<ElectionTableProps>) {
    return(
        <>
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
                        <tr>
                            <td>{election.id}</td>
                            <td>{election.name}</td>
                            <td>{election.electionState}</td>
                            <td>{election.description}</td>
                            <td>TBD</td>
                            <td><button onClick={() => props.onGetResult(election)}>Result</button></td>
                        </tr>
                    )})}
                </tbody>
            </table>

        </>
    )
}