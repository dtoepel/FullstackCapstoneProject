import type {Election} from "./ElectionData.ts";

export type ElectionTableProps = {
    value:Election[];
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
                            <td>TBD</td>
                        </tr>
                    )})}
                </tbody>
            </table>

        </>
    )
}