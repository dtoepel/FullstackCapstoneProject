package org.example.backend.model.db;

import java.util.ArrayList;

public record ElectionDTO(
        String id, String name, String description,
        ArrayList<String> candidateIDs,
        Election.ElectionType electionMethod,
        String candidateType,
        int seats) {
}
