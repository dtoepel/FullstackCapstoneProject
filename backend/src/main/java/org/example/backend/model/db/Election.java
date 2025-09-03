package org.example.backend.model.db;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

public record Election(
        String id, String name, String description,
        List<String> candidateIDs,
        List<Vote> votes,
        ElectionState electionState,
        ElectionType electionMethod,
        String candidateType,
        int seats) {

    public Election advance() {
        if(electionState == ElectionState.ARCHIVED) {throw new IllegalArgumentException("Election has already been archived");}
        ElectionState next = electionState==ElectionState.OPEN?ElectionState.VOTING:
                electionState==ElectionState.VOTING?ElectionState.CLOSED:ElectionState.ARCHIVED;
        return new Election(id, name, description, candidateIDs, votes, next, electionMethod, candidateType, seats);
    }

    public Election vote(Vote vote) {
        if(electionState != ElectionState.VOTING) {throw new IllegalArgumentException("Vote cannot be cast if not open for voting");}
        List<Vote> votes = new ArrayList<>(this.votes);
        votes.add(vote);
        return new Election(id, name, description, candidateIDs, votes, electionState, electionMethod, candidateType, seats);
    }

    public enum ElectionState {OPEN, VOTING, CLOSED, ARCHIVED}
    public enum ElectionType {STV, VICE}

    @ResponseStatus(value=HttpStatus.FORBIDDEN, reason=DuplicateIdException.reason)
    public static class DuplicateIdException extends RuntimeException {
        public static final String reason = "Duplicate ID";
    }

    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason=IdNotFoundException.reason)
    public static class IdNotFoundException extends RuntimeException {
        public static final String reason = "ID not found";
    }

    @ResponseStatus(value=HttpStatus.FORBIDDEN)
    public static class IllegalManipulationException extends RuntimeException {
        public IllegalManipulationException(String message) {
            super(message);
        }
    }
}
