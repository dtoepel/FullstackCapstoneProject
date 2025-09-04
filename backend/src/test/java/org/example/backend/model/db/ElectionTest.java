package org.example.backend.model.db;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ElectionTest {

    private final Election DEFAULT_ELECTION = new Election("id", "name", "description",
            List.of("candidateId"), new ArrayList<>(), Election.ElectionState.VOTING, Election.ElectionType.STV,
            "Person", 2);

    @Test
    void advance() {
        //GIVEN
        Election e = DEFAULT_ELECTION;

        //WHEN
        Election f = e.advance();

        //THEN
        assertEquals(e.id(), f.id());
        assertEquals(e.votes(), f.votes());
        assertEquals(Election.ElectionState.CLOSED, f.electionState());
    }

    @Test
    void advanceFail() {
        //GIVEN
        //WHEN
        final Election f = DEFAULT_ELECTION.advance().advance();

        //THEN
        assertThrows(Exception.class, f::advance);
    }

    @Test
    void vote() {
        //GIVEN
        Election e = DEFAULT_ELECTION;

        //WHEN
        Election f = e.vote(new Vote(Arrays.asList("A", "B")));
        Election g = f.vote(new Vote(Arrays.asList("C", "D")));

        //THEN
        assertEquals(e.id(), g.id());
        assertEquals(e.electionState(), g.electionState());
        assertEquals(Arrays.asList(new Vote(Arrays.asList("A", "B")), new Vote(Arrays.asList("C", "D"))), g.votes());
    }

    @Test
    void voteFail() {
        //GIVEN
        Election e = DEFAULT_ELECTION;

        //WHEN
        e = e.vote(new Vote(Arrays.asList("A", "B")));
        final Election f = e.advance();

        //THEN
        assertThrows(Exception.class, () -> f.vote(new Vote(Arrays.asList("C", "D"))));
    }
}