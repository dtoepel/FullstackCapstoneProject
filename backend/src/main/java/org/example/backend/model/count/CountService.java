package org.example.backend.model.count;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Election;

import java.util.ArrayList;
import java.util.List;

public class CountService {
    private CountService() {}

    public static List<DetailedResult.ResultItem> getElectionResult(
            Election election,
            List<Candidate> allCandidates) {
        ArrayList<Candidate> runningCandidates = new ArrayList<>();
        for(String eId : election.candidateIDs()) {
            for(Candidate cc : allCandidates) {
                if(cc.id().equals(eId)) {
                    runningCandidates.add(cc);
                }
            }
        }
        MeekAlgorithm meek = new MeekAlgorithm(runningCandidates, election.votes(), election.seats());
        DetailedResult result = meek.perform();
        return result.get();
    }
}
