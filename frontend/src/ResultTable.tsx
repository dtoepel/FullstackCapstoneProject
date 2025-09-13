import type {AnalysisResult, Candidate, CondorcetResult, STVResultItem} from "./ElectionData.ts";
import CandidateBox from "./CandidateBox.tsx";
import type {JSX} from "react";

export type ResultTableProps = {
    result:STVResultItem[];
    resultAnalysis:AnalysisResult;
    resultCondorcet:CondorcetResult;
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

    console.log(props.resultAnalysis)

    function getCandidateBox(id: string | undefined):JSX.Element {
        const candidate:Candidate|undefined = props.allCandidates.filter(c => c.id == id).at(0);
        if(candidate != undefined) {
            return (<CandidateBox candidate={candidate}/>);
        } else {
            return (<p className={"errorMessage"}>candidate undefined</p>)
        }
    }

    function getComparisonRow(lowerU: string[] | undefined, upper: string[], lowerCount: number):JSX.Element {
        const lower:string[] = lowerU?lowerU:[];
        const gain:string[] = upper.filter(id => lower.indexOf(id)<0);
        const lose:string[] = lower.filter(id => upper.indexOf(id)<0);

        if(lose.length == 0)
            // no paradox
            return (<tr><td>From {lowerCount} to {lowerCount+1} seats, candidate {getCandidateBox(gain.at(0))}
                would be elected</td></tr>);
        else
            return (<tr><td>From {lowerCount} to {lowerCount+1} seats,
                a <b>paradoxon</b> would occur: <br/>
                While candidates {gain.map(c => getCandidateBox(c))} would have been elected,
                {lose.length==1?" candidate":" candidates"} {lose.map(c => getCandidateBox(c))}
                would <b>not</b> have been elected.</td></tr>);
    }

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
        <hr/>
        <table border={1}>
            {props.resultAnalysis.electedIdsBySeats.map((result, index, array) => {
                if(index == 0)
                    return (getComparisonRow([], result, index))
                else
                    return (getComparisonRow(array.at(index-1), result, index))}
            )}
        </table>
        <hr/>
        <table border={1}>
            <thead>
                <tr><th>&nbsp;</th>
                    {props.resultCondorcet.candidateIDs.map((candidateID, indexY) => {
                        const candidateU:Candidate|undefined = props.allCandidates
                            .filter(c => c.id === candidateID)
                            .at(0);
                        return(<th style={{backgroundColor:candidateU?("#"+candidateU.color):"#fff"}}>{indexY+1}</th>)
                    })}
                </tr>
            </thead>
            {props.resultCondorcet.candidateIDs.map((candidateID, indexY) => {
                const myDuels = props.resultCondorcet.duels[indexY];
                return(<tr><td><div style={{display:"flex",flexDirection:"row"}}><span>{(indexY+1) + ": "}</span>{getCandidateBox(candidateID)}</div></td>

                    {myDuels.map((num, indexX) => {
                        if (indexX == indexY) {
                            return (<td style={{backgroundColor: "#000"}}>&nbsp;</td>)
                        } else if (num > 0) {
                            return (<td style={{backgroundColor: "var(--mylightgreen)"}}>{num}</td>)
                        } else if (num < 0) {
                            return (<td style={{backgroundColor: "var(--mylightred)"}}>{num}</td>)
                        } else {
                            return (<td>{num}</td>)
                        }
                    })}

                </tr>
            )})}
        </table>
    </div>
    )
}