package org.example.backend.model.db;

public record Candidate(
        String id,
        String name,
        String party,
        String color,
        String description,
        String type) {
}
