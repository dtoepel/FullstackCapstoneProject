package org.example.backend.repository;

import org.example.backend.model.db.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CandidateRepo extends MongoRepository<Candidate,String> {}
