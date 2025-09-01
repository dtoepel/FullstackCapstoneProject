import type {Candidate, STVResultItem} from "./ElectionData.ts";

export type ResultTableProps = {
    result:STVResultItem[];
    allCandidates:Candidate[];
}

function getResultCell(votes: string, key: string) {
    if(votes==="ELECTED") {
        return <td key={key} style={{backgroundColor:"#6b9"}}>★</td>;
    } else if(votes==="EXCLUDED") {
        return <td key={key} style={{backgroundColor:"#f8c"}}>☓</td>;
    } else {
        return <td key={key}>{votes}</td>;
    }
}

export default function ResultTable(props:Readonly<ResultTableProps>) {
    const firstLineU:STVResultItem|undefined = props.result.at(0);
    const firstLine:STVResultItem = firstLineU ?? {candidateID:"",votes:[]};

    return(
        <div style={{overflow:"scroll", height:"400px", width:"95vw"}}>
        <table border={1}>
            <thead>
                <tr>
                    <th>Rank</th>
                    <th style={{width:"10px"}}>&nbsp;</th>
                    <th>Candidate</th>
                    {firstLine.votes.map((_x, i) => {return(<th key={"result-column-"+i}>Count {i+1}</th>)})}
                </tr>
            </thead>
            <tbody>
            {props.result.map((item, y) => {
                const candidateU:Candidate|undefined = props.allCandidates
                    .filter(c => c.id === item.candidateID)
                    .at(0);
                const candidate:Candidate = candidateU ?? {
                    name:"Candidate not found",id:"",type:"",archived:false,party:"",color:"",description:""};

              return (
                  <tr key={"result-row-" + y}>
                      <th>{y+1}</th>
                      <th style={{backgroundColor:"#"+candidate.color}}>&nbsp;</th>
                      <th>{candidate.name}</th>
                      {item.votes.map((votes, x) => {
                          const key:string = "result-cell-" + x +"-" + y;
                          return(getResultCell(votes, key))})}
                  </tr>
              )})}
            </tbody>
        </table>
        </div>
    )
}