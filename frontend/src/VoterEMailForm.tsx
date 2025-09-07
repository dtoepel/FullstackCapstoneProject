import type {Election, Voter} from "./ElectionData.ts";
import {useState} from "react";

export type VoterEMailFormProps = {
    voterList:Voter[],
    elections:Election[],
    onSubmit:(email:string)=>void
}

export default function VoterEMailForm(props:Readonly<VoterEMailFormProps>) {
    const [email, setEmail] = useState<string>("");

    function getElectionName(electionID:string):string {
        const filteredElections:Election[] = props.elections.filter(election => election.id == electionID);
        const firstElection:Election|undefined = filteredElections.at(0);
        if(firstElection) {
            return firstElection.name;
        } else {
            return "[election not found]"
        }
    }

    return(
        <div style={{display:"flex", flexDirection:"column"}}>
            <p>
                This form simulates the emails containing the validation codes sent to the voters.
                Enter the email address to receive the active validation codes for the voter.
            </p>
            <input value={email}
                    onChange={e => setEmail(e.target.value)}/>
            <button onClick={() => props.onSubmit(email)}>Get Codes</button>
            <table border={1}>
                <thead>
                    <tr>
                        <th>Election</th>
                        <th>Code</th>
                    </tr>
                </thead>
                <tbody>
                    {props.voterList.map(voter => {
                        return(
                            <tr key={voter.id}>
                                <td>
                                    {getElectionName(voter.electionID)}
                                </td>
                                <td>
                                    {voter.validationCode}
                                </td>
                            </tr>
                    )})}
                </tbody>
            </table>
        </div>
    )
}