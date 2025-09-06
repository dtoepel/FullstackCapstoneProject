package org.example.backend.repository;

import org.example.backend.model.db.Voter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoterRepo extends MongoRepository<Voter,String> {}
