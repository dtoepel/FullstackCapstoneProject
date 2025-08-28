package org.example.backend.model.count;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Vote;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeekAlgorithmTest {

    private ArrayList<Candidate> getDefaultCandidates() {
        ArrayList<Candidate> candidates = new ArrayList<>();
        candidates.add(new Candidate("A", "Alice", "", "", "", ""));
        candidates.add(new Candidate("B", "Bob", "", "", "", ""));
        candidates.add(new Candidate("C", "Charlie", "", "", "", ""));
        candidates.add(new Candidate("D", "David", "", "", "", ""));
        candidates.add(new Candidate("E", "Eve", "", "", "", ""));
        return candidates;
    };

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

}