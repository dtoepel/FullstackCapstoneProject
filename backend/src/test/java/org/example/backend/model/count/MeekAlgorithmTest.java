package org.example.backend.model.count;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Election;
import org.example.backend.model.db.Vote;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeekAlgorithmTest {

    private ArrayList<Candidate> getDefaultCandidates() {
        ArrayList<Candidate> candidates = new ArrayList<>();
        candidates.add(new Candidate("A", "Alice", "", "", "", "", false));
        candidates.add(new Candidate("B", "Bob", "", "", "", "", false));
        candidates.add(new Candidate("C", "Charlie", "", "", "", "", false));
        candidates.add(new Candidate("D", "David", "", "", "", "", false));
        candidates.add(new Candidate("E", "Eve", "", "", "", "", false));
        return candidates;
    }

    @Test
    void performElection() {
        //GIVEN
        ArrayList<Candidate> candidates = getDefaultCandidates();
        ArrayList<org.example.backend.model.db.Vote> votes = new ArrayList<>();
        votes.add(new Vote(List.of("A")));
        votes.add(new Vote(Arrays.asList("B", "C")));
        int seats = 2;

        //WHEN
        MeekAlgorithm algorithm = new MeekAlgorithm(candidates, votes, seats);
        DetailedResult result = algorithm.perform();

        //THEN
        for(DetailedResult.ResultItem item : result.get()) {
            assertTrue(item.votes().getLast().equals("ELECTED") || item.votes().getLast().equals("EXCLUDED"));
            if(Arrays.asList("A", "B").contains(item.candidateID())) assertEquals("ELECTED", item.votes().getLast());
            if(Arrays.asList("C", "D", "E").contains(item.candidateID())) assertEquals("EXCLUDED", item.votes().getLast());
        }
    }

    @Test
    void performElectionWithCountService() {
        //GIVEN
        ArrayList<Vote> votes = new ArrayList<>();
        for(int i = 0; i < 6; i++) votes.add(new Vote(List.of("A")));
        for(int i = 0; i < 5; i++) votes.add(new Vote(List.of("B")));
        for(int i = 0; i < 2; i++) votes.add(new Vote(Arrays.asList("C", "B")));
        Election election = new Election(
                "any", "any", "any",
                Arrays.asList("A", "B", "C"),
                votes,
                new ArrayList<>(),
                Election.ElectionState.CLOSED,
                Election.ElectionType.STV,
                "any",
                1);

        ArrayList<Candidate> allCandidates = getDefaultCandidates();

        //WHEN
        List<DetailedResult.ResultItem> result = CountService.getElectionResult(election, allCandidates);

        //THEN
        assertEquals(3, result.size());
        for(DetailedResult.ResultItem item : result) {
            assertTrue(item.votes().getLast().equals("ELECTED") || item.votes().getLast().equals("EXCLUDED"));
            if("B".equals(item.candidateID())) {
                assertEquals("38.46%", item.votes().getFirst());
                assertEquals("ELECTED", item.votes().getLast());
            }
            if(Arrays.asList("A", "C").contains(item.candidateID())) assertEquals("EXCLUDED", item.votes().getLast());
        }
    }

    @Test
    void analyseElectionTest() {
        //GIVEN
        ArrayList<Vote> votes = new ArrayList<>();
        for(int i = 0; i < 6; i++) votes.add(new Vote(Arrays.asList("A", "D")));
        for(int i = 0; i < 5; i++) votes.add(new Vote(Arrays.asList("B", "E")));
        for(int i = 0; i < 2; i++) votes.add(new Vote(Arrays.asList("C", "B")));
        for(int i = 0; i < 2; i++) votes.add(new Vote(Arrays.asList("C", "D")));
        Election election = new Election(
                "any", "any", "any",
                Arrays.asList("A", "B", "C", "D", "E"),
                votes,
                new ArrayList<>(),
                Election.ElectionState.CLOSED,
                Election.ElectionType.STV,
                "any",
                2);

        //WHEN
        AnalysisResult result = Analysis.analyseDistributionAnomalies(election, getDefaultCandidates());

        //THEN
        assertEquals(election.seats()+2, result.electedIdsBySeats().size());
        for(int i = 0; i < election.seats()+2; i++) {
            assertEquals(i+1, result.electedIdsBySeats().get(i).size());
        }
    }

    @Test
    void analyseElectionTestFail() {
        //GIVEN
        ArrayList<Vote> votes = new ArrayList<>();
        for(int i = 0; i < 6; i++) votes.add(new Vote(List.of("A")));
        for(int i = 0; i < 5; i++) votes.add(new Vote(List.of("B")));
        Election election = new Election(
                "any", "any", "any",
                Arrays.asList("A", "B"),
                votes,
                new ArrayList<>(),
                Election.ElectionState.CLOSED,
                Election.ElectionType.STV,
                "any",
                1);

        //WHEN/THEN
        assertThrows(Analysis.CannotAnalyseElectionException.class,
                () -> Analysis.analyseDistributionAnomalies(election, getDefaultCandidates()));
    }

}