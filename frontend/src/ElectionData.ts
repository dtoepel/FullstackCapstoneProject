export type Election = {
    id:string;
    name:string;
    description:string;
    electionState:string;
    seats:number;
    candidateIDs:string[];
    candidateType:string;
    electionMethod:string;
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
    archived:boolean;
}

export type ElectionResultItem = {
    candidate:Candidate;
    firstVotes:string;
    elected:boolean;
    electedAs:string;
}

export type STVResultItem = {
    candidateID:string;
    votes:string[];
}

export function getAllCandidateTypes(candidates : Candidate[]):string[] {
    const types:string[] = [];

    candidates
        .filter(candidate => candidate.type)
        .forEach(candidate => {
            const index = types.indexOf(candidate.type);
            if(index < 0) {
                types.push(candidate.type);
            }
        })
    return types;
}