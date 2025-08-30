package org.example.backend.model.count;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DetailedResult {
    private final ArrayList<String> electedCandidateIDs = new ArrayList<>();
    private final ArrayList<String> excludedCandidateIDs = new ArrayList<>();
    private final HashMap<String,ArrayList<String>> voteCounts = new HashMap<>();
    private static final DecimalFormat PERCENT = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.UK));

    private void recordVoteCount(Candidate candidate, double votes) {
        ArrayList<String> voteList = voteCounts.computeIfAbsent(candidate.getDbCandidate().id(), k -> new ArrayList<>());

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

    public record ResultItem(String candidateID, List<String> votes) {}

    public List<ResultItem> get() {
        ArrayList<ResultItem> items = new ArrayList<>();
        for(String candidateID : electedCandidateIDs) {
            items.add(new ResultItem(candidateID, voteCounts.getOrDefault(candidateID, new ArrayList<>())));
        }
        for(String candidateID : excludedCandidateIDs.reversed()) {
            items.add(new ResultItem(candidateID, voteCounts.getOrDefault(candidateID, new ArrayList<>())));
        }
        return items;
    }
}
