package org.example.backend.model.count;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Election;

import java.util.ArrayList;
import java.util.List;

public class CountService {
    private CountService() {}

    public static List<DetailedResult.ResultItem> getElectionResult(
            Election election,
            List<Candidate> candidatesDB) {
        ArrayList<Candidate> candidates = new ArrayList<>();
        for(String eId : election.candidateIDs()) {
            for(Candidate cc : candidatesDB) {
                if(cc.id().equals(eId)) {
                    candidates.add(cc);
                }
            }
        }
        MeekAlgorithm meek = new MeekAlgorithm(candidates, election.votes(), election.seats());
        DetailedResult result = meek.perform();
        return result.get();
    }
}
