package org.example.backend.model.db;

import java.util.Vector;

public record ElectionDTO(
        String id, String name, String description,
          Vector<String> candidateIDs,
          Election.ElectionType electionType,
          String candidateType,
          int seats) {
}
