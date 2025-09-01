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
    color:string|null
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
    strikethrough:false,
    color:null
}

export default function CandidateBox(props:Readonly<CandidateBoxProps>) {
    props = {...CandidateBox.defaultProps, ...props}
    const color:string = props.color===null?("#"+props.candidate.color):props.color;
    return(
        <div className={"small-card-candidate"} style={{backgroundColor:color}}>

            {props.color!=null?
                <div className={"small-card-candidate-partybutton"}
                     style={{backgroundColor:("#"+props.candidate.color)}}>&nbsp;</div>
            :""}

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