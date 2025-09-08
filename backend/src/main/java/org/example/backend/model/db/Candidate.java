package org.example.backend.model.db;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public record Candidate(
        String id,
        String name,
        String party,
        String color,
        String description,
        String type,
        boolean archived) {

    public Candidate(CandidateDTO init, String id) {
        this(id, init.name(), init.party(), init.color(), init.description(), init.type(), false);
    }

    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason=Election.IdNotFoundException.REASON)
    public static class IdNotFoundException extends RuntimeException {
        public static final String reason = "ID not found";
    }
}
