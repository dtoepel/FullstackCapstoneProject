package org.example.backend.model.db;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public record Election(
        String id, String name, String description,
        List<String> candidateIDs,
        List<Vote> votes,
        List<String> voterEmails,
        ElectionState electionState,
        ElectionType electionMethod,
        String candidateType,
        int seats) {

    public Election advance() {
        return new Election(id, name, description, candidateIDs, votes, voterEmails, electionState.next(), electionMethod, candidateType, seats);
    }

    public Election vote(Vote vote) {
        if(electionState != ElectionState.VOTING) {throw new IllegalArgumentException("Vote cannot be cast if not open for voting");}
        List<Vote> votes = new ArrayList<>(this.votes);
        votes.add(vote);
        return new Election(id, name, description, candidateIDs, votes, voterEmails, electionState, electionMethod, candidateType, seats);
    }

    public List<Voter> createVoterCodes() {
        ArrayList<Voter> voters = new ArrayList<>();
        SecureRandom random = new SecureRandom();

        for(String voterEmail : voterEmails) {
            if(!"".equals(voterEmail)) {
                String randomCode = getRandomCode(random);
                voters.add(new Voter(null, voterEmail, id, randomCode));
            }
        }
        return voters;
    }

    private String getRandomCode(SecureRandom random) {
        StringBuilder s = new StringBuilder();
        char[] chars = "123456789BCDFGHJKLMNPQRSTVWXYZ".toCharArray();
        for(int i = 0; i < 6; i++) {
            int r = random.nextInt(chars.length);
            s.append(chars[r]);
        }
        return s.toString();
    }

    public enum ElectionState {
        OPEN, VOTING, CLOSED, ARCHIVED;

        private ElectionState next()  {
            if(this == ElectionState.ARCHIVED) throw new IllegalArgumentException("Election has already been archived");
            if(this == ElectionState.CLOSED) return ElectionState.ARCHIVED;
            if(this == ElectionState.VOTING) return ElectionState.CLOSED;
            return ElectionState.VOTING;
        }
    }
    public enum ElectionType {STV, VICE}

    @ResponseStatus(value=HttpStatus.FORBIDDEN, reason=DuplicateIdException.REASON)
    public static class DuplicateIdException extends RuntimeException {
        public static final String REASON = "Duplicate ID";
    }

    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason=IdNotFoundException.REASON)
    public static class IdNotFoundException extends RuntimeException {
        public static final String REASON = "ID not found";
    }

    @ResponseStatus(value=HttpStatus.UNAUTHORIZED, reason=VoteNotAuthorizedException.REASON)
    public static class VoteNotAuthorizedException extends RuntimeException {
        public static final String REASON = "Validation code not accepted";
    }

    public static class IllegalManipulationException extends ResponseStatusException {
        public IllegalManipulationException(String message) {
            super(HttpStatus.FORBIDDEN, message);
        }

        public static final String MSG_ALREADY_ARCHIVED = "Status Archived cannot be advanced";
        public static final String MSG_TOO_FEW_CANDIDATES = "Election has too few candidates to be opened";
        public static final String MSG_TOO_FEW_VOTES = "Election has too few votes to be closed";
        public static final String MSG_VOTES_CANNOT_BE_CAST = "Votes cannot be cast in this state";
        public static final String MSG_NO_EMPTY_VOTES = "Vote cannot be empty";
        public static final String MSG_CANNOT_COUNT_EMPTY_VOTES = "Empty votes cannot be counted";
        public static final String MSG_CANNOT_CHANGE_VOTES = "Votes cannot be changed through this method";

        public static final String MSG_CANNOT_CHANGE_STATUS = "Status cannot be changed through this method";
        public static final String MSG_CANNOT_CHANGE_CANDIDATES = "Candidates cannot be changed unless the election is OPEN";
        public static final String MSG_CANNOT_CHANGE_TYPE = "Candidate Type cannot be changed unless the election is OPEN";
        public static final String MSG_CANNOT_CHANGE_METHOD = "Election Method cannot be changed unless the election is OPEN";
        public static final String MSG_CANNOT_CHANGE_SEATS = "Number of Seats cannot be changed unless the election is OPEN";
        public static final String MSG_CANNOT_CHANGE_VOTERS = "Voters cannot be changed unless the election is OPEN";
    }
}
