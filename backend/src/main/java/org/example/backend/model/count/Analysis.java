package org.example.backend.model.count;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Election;

import java.util.ArrayList;
import java.util.List;

public class Analysis {
    private Analysis() {}

    public static AnalysisResult analyseDistributionAnomalies(Election election, List<Candidate> allCandidates) {
        /* the election is analysed for possible paradoxa, regarding the number of seats.
            the question is whether the method (the meek-algorithm) is monotonic.
            I.e. when there is one more seats to be distributed, then the same candidates as before are elected plus a new one,
            or whether one candidate elected for fewer seats paradoxically is no longer elected.
            (Compare Alabama-Paradoxon)

            To do so, the election is simulated for various numbers of seats, starting with 1 and continuing up to
            2 seats more than the supplied election had by default,
            if the number of candidates is sufficient, i.e. at least one more than seats.
         */

        ArrayList<List<String>> electedBySeats = new ArrayList<>();
        int minSeats = 1;
        int maxSeats = Math.min(election.seats() + 2, election.candidateIDs().size()-1);
        if(minSeats >= maxSeats) {throw new RuntimeException("Analysis pointless: Only " +
                election.candidateIDs().size() + " candidates available." );}

        for(int i = minSeats; i <= maxSeats; i++) {
            List<Candidate> runningCandidates = election.getCandidates(allCandidates);
            MeekAlgorithm meek = new MeekAlgorithm(runningCandidates, election.votes(), i);
            DetailedResult result = meek.perform();
            electedBySeats.add(result.getElected());
        }

        /*
        The simulated results are in a list now, with the index + 1 being the number of candidates elected
         */

        return new AnalysisResult(electedBySeats);
    }
}
