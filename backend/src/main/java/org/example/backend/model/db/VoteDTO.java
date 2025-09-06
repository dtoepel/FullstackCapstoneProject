package org.example.backend.model.db;

import java.util.List;

public record VoteDTO(List<String> rankingIDs, String validationCode) {}
