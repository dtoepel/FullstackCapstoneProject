package org.example.backend.model.count;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;

public class DetailedResult {
    private final Vector<String> electedCandidateIDs = new Vector<>();
    private final Vector<String> excludedCandidateIDs = new Vector<>();
    private final HashMap<String,Vector<String>> voteCounts = new HashMap<>();
    private static final DecimalFormat PERCENT = new DecimalFormat("0.00");

    private void recordVoteCount(Candidate candidate, double votes) {
        Vector<String> voteList = voteCounts.computeIfAbsent(candidate.getDbCandidate().id(), k -> new Vector<>());

        if(candidate.getStatus() == Candidate.CandidateStatus.ELECTED) {
            voteList.add("ELECTED");
        } else if(candidate.getStatus() == Candidate.CandidateStatus.EXCLUDED) {
            voteList.add("EXCLUDED");
        } else {
            voteList.add(PERCENT.format(votes*100)+"%");
        }
    }

    /*
     * Records the current round of voting
     */
    void recordVoteCount(Count count) {
        for(Candidate candidate : count.voteCount().keySet()) {
            recordVoteCount(candidate, count.voteCount().get(candidate)/count.total());
        }
    }

    // Record candidates as elected or excluded. The order determines the order for the result
    void recordElectedCandidate(Candidate candidate) {electedCandidateIDs.add(candidate.getDbCandidate().id());}
    void recordExcludedCandidate(Candidate candidate) {excludedCandidateIDs.add(candidate.getDbCandidate().id());}

    public record ResultItem(String candidateID, Vector<String> votes) {}

    public Vector<ResultItem> get() {
        Vector<ResultItem> items = new Vector<>();
        for(String candidateID : electedCandidateIDs) {
            items.add(new ResultItem(candidateID, voteCounts.getOrDefault(candidateID, new Vector<>())));
        }
        for(String candidateID : excludedCandidateIDs.reversed()) {
            items.add(new ResultItem(candidateID, voteCounts.getOrDefault(candidateID, new Vector<>())));
        }
        return items;
    }
}
