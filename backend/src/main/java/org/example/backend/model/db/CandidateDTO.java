package org.example.backend.model.db;

public record CandidateDTO(
        String name,
        String party,
        String color,
        String description,
        String type) {
}
