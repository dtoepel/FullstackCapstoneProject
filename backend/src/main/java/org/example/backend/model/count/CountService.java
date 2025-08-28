package org.example.backend.model.count;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Election;

import java.util.List;
import java.util.Vector;

public class CountService {

    public static List<DetailedResult.ResultItem> getElectionResult(
            Election election,
            List<Candidate> candidatesDB) {
        Vector<Candidate> candidates = new Vector<>();
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
