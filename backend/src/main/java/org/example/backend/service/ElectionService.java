package org.example.backend.service;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Election;
import org.example.backend.repository.CandidateRepo;
import org.example.backend.repository.ElectionRepo;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Candidate> getAllCandidates() { return candidateRepo.findAll(); }
    public Candidate createCandidate(Candidate candidate) { return candidateRepo.save(candidate); }
    public Candidate updateCandidate(Candidate candidate) { return candidateRepo.save(candidate); }

}
