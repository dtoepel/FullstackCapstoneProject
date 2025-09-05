package org.example.backend.service;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Election;
import org.example.backend.repository.CandidateRepo;
import org.example.backend.repository.ElectionRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ElectionService {
    private final ElectionRepo electionRepo;
    private final CandidateRepo candidateRepo;

    public ElectionService(ElectionRepo electionRepo, CandidateRepo candidateRepo) {
        this.electionRepo = electionRepo;
        this.candidateRepo = candidateRepo;
    }

    public List<Election> getAllElections() { return electionRepo.findAll(); }
    public Election createElection(Election election) { return electionRepo.save(election); }
    public Election updateElection(Election election) { return electionRepo.save(election); }

    public Optional<Election> getElectionById(String id) { return electionRepo.findById(id); }

    public List<Candidate> getAllCandidates() { return candidateRepo.findAll(); }
    public Candidate createCandidate(Candidate candidate) { return candidateRepo.save(candidate); }
    public Candidate updateCandidate(Candidate candidate) { return candidateRepo.save(candidate); }

    public Optional<Candidate> getCandidateById(String id) { return candidateRepo.findById(id); }

    public boolean deleteElection(String electionId) {
        Optional<Election> response = electionRepo.findById(electionId);
        if(response.isPresent()) {
            electionRepo.deleteById(electionId);
            return true;
        }
        return false;
    }

    public boolean deleteCandidate(String candidateId) {
        Optional<Candidate> response = candidateRepo.findById(candidateId);
        if(response.isPresent()) {
            candidateRepo.deleteById(candidateId);
            return true;
        }
        return false;
    }

}
