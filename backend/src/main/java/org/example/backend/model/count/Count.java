package org.example.backend.model.count;

import java.util.HashMap;

record Count(
    HashMap<Candidate, Double> voteCount,
    double total,
    double excess,
    double quota) {}
