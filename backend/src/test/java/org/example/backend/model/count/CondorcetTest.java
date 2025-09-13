package org.example.backend.model.count;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class CondorcetTest {

    @Test
    void performCondorcetAnalysis() {
        //GIVEN
        ArrayList<String> candidates = new ArrayList<>();
        ArrayList<org.example.backend.model.db.Vote> votes = new ArrayList<>();
        //WHEN
        CondorcetAlgorithm.performCondorcetAlgorithm(candidates, votes);

        //THEN
    }
}
