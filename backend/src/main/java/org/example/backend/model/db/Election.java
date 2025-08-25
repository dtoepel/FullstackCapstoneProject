package org.example.backend.model.db;


import java.util.Vector;

public record Election(
        String id, String name, String description,
        Vector<String> candidateIDs,
        Vector<Vote> votes,
        ElectionState electionState,
        ElectionType electionType,
        String candidateType,
        int seats) {

    public enum ElectionState {OPEN, VOTING, CLOSED}
    public enum ElectionType {STV, VICE}

}
