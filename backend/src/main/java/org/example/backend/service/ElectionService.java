package org.example.backend.service;

import org.example.backend.model.db.*;
import org.example.backend.repository.CandidateRepo;
import org.example.backend.repository.ElectionRepo;
import org.example.backend.repository.VoterRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.example.backend.model.db.Election.IllegalManipulationException.*;

@Service
public class ElectionService {
    private final ElectionRepo electionRepo;
    private final CandidateRepo candidateRepo;
    private final VoterRepo voterRepo;

    public ElectionService(ElectionRepo electionRepo, CandidateRepo candidateRepo, VoterRepo voterRepo) {
        this.electionRepo = electionRepo;
        this.candidateRepo = candidateRepo;
        this.voterRepo = voterRepo;
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

    public Election advanceElection(String electionId) {
        Optional<Election> electionO = getElectionById(electionId);
        if(electionO.isPresent()) {
            Election electionDB = electionO.get();
            if(electionDB.electionState() == Election.ElectionState.ARCHIVED) {
                throw new Election.IllegalManipulationException(MSG_ALREADY_ARCHIVED);}
            if(electionDB.electionState() == Election.ElectionState.OPEN &&
                    electionDB.candidateIDs().size() <= electionDB.seats()) {
                throw new Election.IllegalManipulationException(MSG_TOO_FEW_CANDIDATES);}
            if(electionDB.electionState() == Election.ElectionState.VOTING &&
                    electionDB.votes().isEmpty()) {
                throw new Election.IllegalManipulationException(MSG_TOO_FEW_VOTES);}
            if(electionDB.electionState() == Election.ElectionState.OPEN) {
                voterRepo.saveAll(electionDB.createVoterCodes());
            }
            return updateElection(electionDB.advance());
        } else {
            throw new Election.IdNotFoundException();
        }
    }

    public void vote(String electionId, VoteDTO vote) {
        Optional<Election> electionO = getElectionById(electionId);
        if(electionO.isPresent()) {
            Election electionDB = electionO.get();
            if(electionDB.electionState() != Election.ElectionState.VOTING) {
                throw new Election.IllegalManipulationException(MSG_VOTES_CANNOT_BE_CAST);}
            if(vote.rankingIDs().isEmpty()) {
                throw new Election.IllegalManipulationException(MSG_NO_EMPTY_VOTES);}
            List<Voter> allVoters = voterRepo.findAll();
            List<Voter> validVoters = allVoters.stream().filter(voter -> {return
                    electionDB.id().equals(voter.electionID()) &&
                    voter.validationCode().equals(vote.validationCode());}).toList();
            if(validVoters.isEmpty()) {
                throw new Election.VoteNotAuthorizedException();
            } else {
                voterRepo.deleteById(validVoters.getFirst().id());
                electionRepo.save(electionDB.vote(new Vote(vote.rankingIDs())));
            }
        } else {
            throw new Election.IdNotFoundException();
        }
    }
}
