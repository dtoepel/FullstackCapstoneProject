package org.example.backend.model.db;

public record Voter(String id, String email, String electionID, String validationCode) {}
