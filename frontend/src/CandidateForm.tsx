import type {Candidate} from "./ElectionData.ts";
import type {FormEvent} from "react";

export type AddCandidateFormProps = {
    editMode:boolean;
    candidate:Candidate;
    onEdit:(candidate:Candidate)=>void;
    onSubmit:()=>void;
    error:string|null;
}

export default function CandidateForm(props:Readonly<AddCandidateFormProps>) {

    function submit(e:FormEvent<HTMLFormElement> ):void {
        e.preventDefault();
        props.onSubmit();
    }

    function changeName(value:string) {
        const candidate = {
            ...props.candidate,
            name:value,
        }
        props.onEdit(candidate)
    }

    function changeDescription(value:string) {
        const candidate = {
            ...props.candidate,
            description:value,
        }
        props.onEdit(candidate)
    }

    function changeParty(value:string) {
        const candidate = {
            ...props.candidate,
            party:value,
        }
        props.onEdit(candidate)
    }

    function changeColor(value:string) {
        const candidate = {
            ...props.candidate,
            color:value,
        }
        props.onEdit(candidate)
    }

    function changeType(value:string) {
        const candidate = {
            ...props.candidate,
            type:value,
        }
        props.onEdit(candidate)
    }

    return(
        <form onSubmit={(e) => submit(e)}>
            <table>
                <tbody>
                <tr>
                    <td>
                        ID
                    </td>
                    <td>
                        {props.candidate.id}
                    </td>
                </tr>
                <tr>
                    <td>
                        Name
                    </td>
                    <td>
                        <input value={props.candidate.name}
                               onChange={(e) => changeName(e.target.value)}/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Description
                    </td>
                    <td>
                        <textarea value={props.candidate.description}
                                  onChange={(e) => changeDescription(e.target.value)}/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Party
                    </td>
                    <td>
                        <input value={props.candidate.party}
                               onChange={(e) => changeParty(e.target.value)}/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Color
                    </td>
                    <td>
                        <input value={props.candidate.color}
                               onChange={(e) => changeColor(e.target.value)}/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Type
                    </td>
                    <td>
                        <input value={props.candidate.type}
                               onChange={(e) => changeType(e.target.value)}/>
                    </td>
                </tr>
                <tr>
                    <td colSpan={2}>
                        {props.error?<p className={"error-message"}>{props.error}</p>:""}
                    </td>
                </tr>
                <tr>
                    <td colSpan={2}>
                        <button>Submit</button>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
    )
}