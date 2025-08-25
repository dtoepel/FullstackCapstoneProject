export type Election = {
    id:string;
    name:string;
    description:string;
    electionState:string;
    seats:number;
    candidateIDs:string[];
    candidateType:string;
    electionType:string;
    votes:Vote[];
}

export type Vote = {
    rankingIDs:string[];
}

export type Candidate = {
    id:string;
    name:string;
    description:string;
    party:string;
    color:string;
    type:string;
}

export type ElectionResultItem = {
    candidate:Candidate;
    firstVotes:string;
    elected:boolean;
    electedAs:string;
}
