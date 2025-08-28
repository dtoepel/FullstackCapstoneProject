import type {Candidate, Election} from "./ElectionData.ts";
import type {FormEvent} from "react";

export type EditElectionFormProps = {
    editMode:boolean;
    election:Election;
    error:string|null;
    candidates:Candidate[];
    onEdit:(election:Election)=>void;
    onSubmit:()=>void
}

export default function ElectionForm(props:Readonly<EditElectionFormProps>) {

    function submit(e:FormEvent<HTMLFormElement> ):void {
        e.preventDefault();
        props.onSubmit();
    }

    function changeID(value:string) {
        const election = {
            ...props.election,
            id:value,
        }
        props.onEdit(election)
    }

    function changeName(value:string) {
        const election = {
            ...props.election,
            name:value,
        }
        props.onEdit(election)
    }

    function changeDescription(value:string) {
        const election = {
            ...props.election,
            description:value,
        }
        props.onEdit(election)
    }

    function changeType(value:string) {
        const election = {
            ...props.election,
            electionType:value,
        }
        props.onEdit(election)
    }

    function changeSeats(value:number) {
        const election = {
            ...props.election,
            seats:value,
        }
        props.onEdit(election)
    }

    function changeMethod(value:string) {
        const election = {
            ...props.election,
            electionMethod:value,
        }
        props.onEdit(election)
    }

    function getAvailableCandidates(election:Election, candidates:Candidate[]) {
        return candidates.filter((cc) => election.candidateIDs.indexOf(cc.id) == -1);
    }

    function getRunningCandidates(election:Election, candidates:Candidate[]) {
        let result:Candidate[] = [];
        election.candidateIDs.forEach((ecID) => {
            const c:Candidate[] = candidates.filter((cc) => cc.id == ecID);
            if(c.length == 1) {
                console.log("result: "+result);
                console.log("c: "+c);
                result = result.concat(c);
            }
        })
        return result;
    }

    const candidatesEditable:boolean = props.election.electionState=="OPEN";
    const availableCandidates:Candidate[] = candidatesEditable?
        getAvailableCandidates(props.election, props.candidates):[];
    const runningCandidates:Candidate[] =
        getRunningCandidates(props.election, props.candidates);


    return(
        <div style={{display:"flex", flexDirection:"column"}}>
            <table>
                <tbody>
                <tr>
                    <td>id</td>
                    <td>
                        {props.editMode?props.election.id:
                            <input value={props.election.id}
                               onChange={(e) => changeID(e.target.value)}/>
                        }
                    </td>
                </tr>
                <tr>
                    <td>Name</td>
                    <td>
                        <input value={props.election.name}
                               onChange={(e) => changeName(e.target.value)}/>
                    </td>
                </tr>
                <tr>
                    <td>Description</td>
                    <td>
                        <textarea value={props.election.description}
                                  onChange={(e) => changeDescription(e.target.value)}/>
                    </td>
                </tr>
                <tr>
                    <td>Seats</td>
                    <td>
                        <input
                            type={"number"}
                            value={props.election.seats}
                            onChange={(e) => changeSeats(e.target.valueAsNumber)}/>
                    </td>
                </tr>
                <tr>
                    <td>Election Method</td>
                    <td>
                        <select name="candidate-types" id="candidate-types"
                                onChange={(e) => {changeMethod(e.target.value)}}
                                defaultValue={props.election.electionMethod}>
                            <option value={"STV"}>STV</option>
                            <option value={"Test"}>Test</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Candidate Type</td>
                    <td>
                        <input value={props.election.candidateType}
                               onChange={(e) => changeType(e.target.value)}/>
                    </td>
                </tr>
                </tbody>
            </table>
            <table>
                <thead>
                <tr><td>Running Candidates</td><td>Available Candidates</td></tr>
                </thead>
                <tbody>
                <tr>
                    <td><div style={{
                        display:"flex",
                        flexDirection: "column",
                        alignItems: "flex-start"}}>{runningCandidates.map((candidate) => {return (candidate.name)})}
                    </div></td>

                    <td><div style={{
                        display:"flex",
                        flexDirection: "row",
                        flexWrap: "wrap",
                        alignItems: "flex-start"}}>{availableCandidates.map((candidate) => {return (candidate.name)})}
                    </div></td>
                </tr>
                </tbody>
            </table>
            {props.error?<p className={"error-message"}>{props.error}</p>:""}
            <form onSubmit={(e) => submit(e)}><button>Submit</button></form>
        </div>
    )
}