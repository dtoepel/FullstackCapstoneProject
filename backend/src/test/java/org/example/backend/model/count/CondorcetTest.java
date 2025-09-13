package org.example.backend.model.count;

import org.example.backend.model.db.Vote;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CondorcetTest {

    @Test
    void performCondorcetAnalysis() {
        //GIVEN
        List<String> candidates = Arrays.asList("A", "C", "E", "B", "D");
        List<org.example.backend.model.db.Vote> votes = Arrays.asList(
                new Vote(Arrays.asList("C", "B", "E", "A", "D")),
                new Vote(Arrays.asList("E", "B", "A", "D", "C")),
                new Vote(Arrays.asList("B", "A", "D", "C")),
                new Vote(Arrays.asList("D", "A", "C")),
                new Vote(Arrays.asList("A", "C")));

        //WHEN
        CondorcetAlgorithm.CondorcetResult result = CondorcetAlgorithm.performCondorcetAlgorithm(candidates, votes);
        List<String> ranking = result.candidateIDs();
        int[][] duels = result.duels();

        //THEN
        assertEquals(Arrays.asList("A", "B", "C", "D", "E"), ranking);

        assertEquals(0, duels[0][0]);
        assertEquals(-1, duels[0][1]);
        assertEquals(3, duels[0][2]);
        assertEquals(3, duels[0][3]);
        assertEquals(1, duels[0][4]);
        assertEquals(1, duels[1][4]);
    }
}
