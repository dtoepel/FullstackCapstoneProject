import type {Candidate} from "./ElectionData.ts";

export type CandidateBoxProps = {
    candidate:Candidate;
    onAdd:()=>void;
    onDelete:()=>void;
    onUp:()=>void;
    onDown:()=>void;
    addAvailable:boolean;
    deleteAvailable:boolean;
    upAvailable:boolean;
    downAvailable:boolean;
    rank:number|undefined;
    strikethrough:boolean;
}

CandidateBox.defaultProps = {
    onAdd:()=>{},
    onDelete:()=>{},
    onUp:()=>{},
    onDown:()=>{},
    addAvailable:false,
    deleteAvailable:false,
    upAvailable:false,
    downAvailable:false,
    rank:null,
    strikethrough:false
}


export default function CandidateBox(props:Readonly<CandidateBoxProps>) {
    return(
        <div className={"small-card-candidate"} style={{background:"#"+props.candidate.color}}>

            <span className={"small-card-candidate"}>{props.strikethrough?(
                <s>{(props.rank==null?"":(props.rank+". "))+props.candidate.name}</s>
            ):(
                (props.rank==null?"":(props.rank+". "))+props.candidate.name
                )}</span>

            {props.addAvailable?<button className={"small-card-candidate"}
                                    onClick={() => {props.onAdd()}}>✚</button>:""}
            {props.upAvailable?<button className={"small-card-candidate"}
                                    onClick={() => {props.onUp()}}>▲</button>:""}
            {props.downAvailable?<button className={"small-card-candidate"}
                                    onClick={() => {props.onDown()}}>▼</button>:""}
            {props.deleteAvailable?<button className={"small-card-candidate"}
                                    onClick={() => {props.onDelete()}}>✖</button>:""}

        </div>
    )
}