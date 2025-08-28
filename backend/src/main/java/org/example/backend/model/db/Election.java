package org.example.backend.model.db;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    @ResponseStatus(value= HttpStatus.FORBIDDEN, reason="Duplicate ID")
    public static class DuplicateIdException extends RuntimeException {}

    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="ID not found")
    public static class IdNotFoundException extends RuntimeException {}
}
