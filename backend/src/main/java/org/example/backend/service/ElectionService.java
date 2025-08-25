package org.example.backend.service;

import org.example.backend.model.db.Election;
import org.example.backend.repository.ElectionRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElectionService {
    private final ElectionRepo electionRepo;

    public ElectionService(ElectionRepo electionRepo) {
        this.electionRepo = electionRepo;
    }

    public List<Election> getAllElections() {
        System.out.println("service");
    return electionRepo.findAll(); }

}
