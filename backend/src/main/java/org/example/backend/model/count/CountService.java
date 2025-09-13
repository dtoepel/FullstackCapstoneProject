package org.example.backend.model.count;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Election;

import java.util.List;

public class CountService {
    private CountService() {}

    public static List<DetailedResult.ResultItem> getElectionResult(
            Election election,
            List<Candidate> allCandidates) {
        List<Candidate> runningCandidates = election.getCandidates(allCandidates);
        MeekAlgorithm meek = new MeekAlgorithm(runningCandidates, election.votes(), election.seats());
        DetailedResult result = meek.perform();
        return result.get();
    }
}
