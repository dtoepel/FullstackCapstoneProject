import {type Candidate, type Election, getAllCandidateTypes} from "./ElectionData.ts";
import {type FormEvent} from "react";
import CandidateBox from "./CandidateBox.tsx";
import {useNavigate} from "react-router-dom";

export type EditElectionFormProps = {
    editMode:boolean;
    election:Election;
    error:string|null;
    candidates:Candidate[];
    onEdit:(election:Election)=>void;
    onSubmit:()=>void
    isAdmin:boolean
}

export default function ElectionForm(props:Readonly<EditElectionFormProps>) {
    const nav = useNavigate();
    const candidateTypes:string[] = getAllCandidateTypes(props.candidates);

    function submit(e:FormEvent<HTMLFormElement>):void {
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
            candidateType:value,
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

    function changeEmails(value:string) {
        const voterEmailsArray:string[] = value.split(",");
        const election = {
            ...props.election,
            voterEmails:voterEmailsArray,
        }
        props.onEdit(election)
    }

    function getAvailableCandidates(election:Election, candidates:Candidate[]) {
        return candidates
            .filter(candidate => !candidate.archived)
            .filter(candidate => election.candidateIDs.indexOf(candidate.id) == -1)
            .filter(candidate => {return election.candidateType===null || election.candidateType==="" || election.candidateType===candidate.type});
    }

    function getRunningCandidates(election:Election, candidates:Candidate[]) {
        let result:Candidate[] = [];
        election.candidateIDs.forEach((ecID) => {
            const c:Candidate[] = candidates.filter((candidate) => candidate.id == ecID);
            if(c.length == 1) {
                result = result.concat(c);
            }
        })
        return result;
    }

    function csv(a:string[]):string {
        if(a == null) return "";
        if(a.length==0) return "";
        let s:string = "";
        a.forEach(item => {s += "," + item});
        return s.substring(1);
    }

    function getInvalid(a:string[]):string {
        if(a == null) return "";
        let s:string = "";
        a.forEach(item => {
            if(null == /^(?![_.-])((?![_.-][_.-])[a-zA-Z\d_.-]){0,63}[a-zA-Z\d]@((?!-)((?!--)[a-zA-Z\d-]){0,63}[a-zA-Z\d]\.){1,2}([a-zA-Z]{2,14}\.)?[a-zA-Z]{2,14}$/.exec(item))
            s += "," + item});

        if(s.length==0) return "";
        return s.substring(1);
    }

    function getInvalidRow(emails:string[]) {
        const invalid:string = getInvalid(emails);
        if(invalid == "") {
            return ("");
        } else {
            return(
                <tr>
                    <td>invalid Emails</td>
                    <td>
                        <p className={"error-message"}>{
                            invalid
                        }</p>
                    </td>
                </tr>
            )
        }
    }

    const candidatesEditable:boolean = props.election.electionState=="OPEN" && props.isAdmin;
    const availableCandidates:Candidate[] = candidatesEditable?
        getAvailableCandidates(props.election, props.candidates):[];
    const runningCandidates:Candidate[] =
        getRunningCandidates(props.election, props.candidates);

    function up(candidate:Candidate):void {
        const index:number = props.election.candidateIDs.indexOf(candidate.id, 0);
        if (index > 0) {
            props.election.candidateIDs.splice(index, 1);
            props.election.candidateIDs.splice(index-1, 0, candidate.id);
            props.onEdit(props.election)
            nav(props.editMode?"/editElection/":"/createElection/");
        }
    }

    function down(candidate:Candidate):void {
        const index:number = props.election.candidateIDs.indexOf(candidate.id, 0);
        if (index > -1 && index < props.election.candidateIDs.length-1) {
            props.election.candidateIDs.splice(index, 1);
            props.election.candidateIDs.splice(index+1, 0, candidate.id);
            props.onEdit(props.election)
            nav(props.editMode?"/editElection/":"/createElection/");
        }
    }

    function add(candidate:Candidate):void {
        props.election.candidateIDs.push(candidate.id);
        props.onEdit(props.election)
        nav(props.editMode?"/editElection/":"/createElection/");
    }

    function remove(candidate:Candidate):void {
        const index:number = props.election.candidateIDs.indexOf(candidate.id, 0);
        if (index > -1) {
            props.election.candidateIDs.splice(index, 1);
            props.onEdit(props.election)
            nav(props.editMode?"/editElection/":"/createElection/");
        }
    }

    return(
        <div style={{display:"flex", flexDirection:"column"}}>
            <table>
                <thead>
                    <tr>
                        <th>Key</th><th>Value</th>
                    </tr>
                </thead>
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
                                  onChange={(e) => changeDescription(e.target.value)}
                                  style={{width:"min(400px,60vw)",height:"80px"}}/>
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
                        <select name="election-types" id="election-types"
                                onChange={(e) => {changeMethod(e.target.value)}}
                                defaultValue={props.election.electionMethod}>
                            <option value={"STV"}>STV</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Candidate Type</td>
                    <td>
                        {props.election.electionState === "OPEN"?
                            <select name="candidate-types" id="candidate-types"
                                    onChange={e => changeType(e.target.value)}>
                                {candidateTypes.map(type => {
                                    if(type === props.election.candidateType)
                                        return(<option value={type} key={"candidate-type-option-"+type} selected>{type}</option>)
                                    else
                                        return(<option value={type} key={"candidate-type-option-"+type}>{type}</option>)
                                })}
                            </select>
                        :props.election.candidateType
                        }
                    </td>
                </tr>
                <tr>
                    <td>Voter Emails<br/>
                        as CSV</td>
                    <td>
                        <textarea value={csv(props.election.voterEmails)}
                                  onChange={(e) => changeEmails(e.target.value)}
                                  style={{width:"min(400px,60vw)",height:"80px"}}/>
                    </td>
                </tr>
                {getInvalidRow(props.election.voterEmails)}
                </tbody>
            </table>
            <table>
                <thead>
                <tr><th>Running Candidates</th><th>Available Candidates</th></tr>
                </thead>
                <tbody>
                <tr>

                    <td><div style={{
                        display:"flex",
                        flexDirection: "column",
                        alignItems: "flex-start"}}>{runningCandidates.map((candidate, index, array) => {return (
                        <CandidateBox key={candidate.id}
                                      candidate={candidate}
                                      addAvailable={false}
                                      onAdd={() => {}}
                                      upAvailable={candidatesEditable && index > 0}
                                      onUp={() => {up(candidate)}}
                                      downAvailable={candidatesEditable && index < array.length-1}
                                      onDown={() => {down(candidate)}}
                                      deleteAvailable={candidatesEditable}
                                      onDelete={() => {remove(candidate)}}

                        />
                    )})}</div></td>

                    <td><div style={{
                        display:"flex",
                        flexDirection: "row",
                        flexWrap: "wrap",
                        alignItems: "flex-start"}}>{availableCandidates.map((candidate) => {return (
                        <CandidateBox key={candidate.id}
                                      candidate={candidate}
                                      addAvailable={candidatesEditable}
                                      onAdd={() => {add(candidate)}}
                                      upAvailable={false}
                                      onUp={() => {}}
                                      downAvailable={false}
                                      onDown={() => {}}
                                      deleteAvailable={false}
                                      onDelete={() => {}}

                        />
                    )})}</div></td>
                </tr>
                </tbody>
            </table>
            {props.error?<p className={"error-message"}>{props.error}</p>:""}
            {props.isAdmin?<form onSubmit={(e) => submit(e)}><button>Submit</button></form>:""}
        </div>
    )
}