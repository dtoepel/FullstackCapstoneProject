package org.example.backend.repository;

import org.example.backend.model.db.Election;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionRepo extends MongoRepository<Election,String> {}
