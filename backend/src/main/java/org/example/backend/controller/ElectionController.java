package org.example.backend.controller;

import org.example.backend.model.count.CountService;
import org.example.backend.model.count.DetailedResult;
import org.example.backend.model.db.*;
import org.example.backend.service.ElectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import static org.example.backend.model.db.Election.IllegalManipulationException.*;

@RestController
@RequestMapping("/api/election")
public class ElectionController {
    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping
    public List<Election> getAllElections() { return electionService.getAllElections(); }

    @PostMapping
    public ResponseEntity<Election> createElection(@RequestBody ElectionDTO init) {
        List<Election> allElections = electionService.getAllElections();
        if(!allElections.stream().filter(electionDB -> electionDB.id().equals(init.id())).toList().isEmpty()) {
            throw new Election.DuplicateIdException();
        } else {
            return new ResponseEntity<>(
                electionService.createElection(new Election(
                        init.id(),
                        init.name(),
                        init.description(),
                        init.candidateIDs(),
                        new ArrayList<>(),
                        init.voterEmails(),
                        Election.ElectionState.OPEN,
                        init.electionMethod(),
                        init.candidateType(),
                        init.seats())),
                HttpStatus.CREATED);
        }
    }

    @PutMapping
    public ResponseEntity<Election> updateElection(@RequestBody Election election) {
        Optional<Election> electionO = electionService.getElectionById(election.id());
        if(electionO.isEmpty()) throw new Election.IdNotFoundException();
        Election electionDB = electionO.get();

        // Check for updates prohibited in any case:
        if(!electionDB.votes().equals(election.votes())) {
            throw new Election.IllegalManipulationException(MSG_CANNOT_CHANGE_VOTES);}
        if(!electionDB.electionState().equals(election.electionState())) {
            throw new Election.IllegalManipulationException(MSG_CANNOT_CHANGE_STATUS);}

        // If status is OPEN, then any other updates are allowed
        if(election.electionState() == Election.ElectionState.OPEN)
            return new ResponseEntity<>(
                electionService.updateElection(election),
                HttpStatus.ACCEPTED);

        // Otherwise more updates are prohibited
        if (!electionDB.candidateIDs().equals(election.candidateIDs()))
            throw new Election.IllegalManipulationException(MSG_CANNOT_CHANGE_CANDIDATES);
        if (!electionDB.candidateType().equals(election.candidateType()))
            throw new Election.IllegalManipulationException(MSG_CANNOT_CHANGE_TYPE);
        if (!electionDB.electionMethod().equals(election.electionMethod()))
            throw new Election.IllegalManipulationException(MSG_CANNOT_CHANGE_METHOD);
        if (electionDB.seats() != (election.seats()))
            throw new Election.IllegalManipulationException(MSG_CANNOT_CHANGE_SEATS);
        if (!election.voterEmails().equals(electionDB.voterEmails()))
            throw new Election.IllegalManipulationException(MSG_CANNOT_CHANGE_VOTERS);

        // No checks have failed, now the remaining cases (!= OPEN) can be updated
        return new ResponseEntity<>(
                electionService.updateElection(election),
                HttpStatus.ACCEPTED);

    }

    @PostMapping("/advance/{electionId}")
    public ResponseEntity<Election> advanceElection(@PathVariable("electionId") String electionId) {
        return new ResponseEntity<>(
            electionService.advanceElection(electionId),
            HttpStatus.ACCEPTED);
    }

    @PostMapping("/vote/{electionId}")
    public void voteElection(
            @PathVariable("electionId") String electionId,
            @RequestBody VoteDTO vote) {
        electionService.vote(electionId, vote);
    }

    @GetMapping("/results/{electionId}")
    public ResponseEntity<List<DetailedResult.ResultItem>> getElectionResults(@PathVariable("electionId") String electionId) {
        Optional<Election> electionO = electionService.getElectionById(electionId);
        List<Candidate> allCandidates = electionService.getAllCandidates();
        if(electionO.isPresent()) {
            if (electionO.get().votes().isEmpty())
                throw new Election.IllegalManipulationException(MSG_CANNOT_COUNT_EMPTY_VOTES);
            List<DetailedResult.ResultItem> result = CountService.getElectionResult(electionO.get(), allCandidates);
            return new ResponseEntity<>(
                    result,
                    HttpStatus.OK);
        } else {
            throw new Election.IdNotFoundException();
        }
    }

    @DeleteMapping("/{electionId}")
    public void deleteElection(@PathVariable("electionId") String electionId) {
        if(!electionService.deleteElection(electionId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object does not exist");
    }

    @GetMapping("/candidates")
    public List<Candidate> getAllCandidates() { return electionService.getAllCandidates(); }


    @PostMapping("/candidates")
    public ResponseEntity<Candidate> createCandidate(@RequestBody CandidateDTO init) {
        UUID uuid = UUID.randomUUID();

        return new ResponseEntity<>(
                electionService.createCandidate(new Candidate(
                        init, uuid.toString())),
                HttpStatus.CREATED);
    }

    @PutMapping("/candidates")
    public ResponseEntity<Candidate> updateCandidate(@RequestBody Candidate candidate) {
        Optional<Candidate> candidateDB =  electionService.getCandidateById(candidate.id());
        if(candidateDB.isEmpty()) {
            throw new Candidate.IdNotFoundException();
        } else {
            return new ResponseEntity<>(
                    electionService.updateCandidate(candidate),
                    HttpStatus.ACCEPTED);
        }
    }

    @DeleteMapping("/candidates/{candidateID}")
    public void deleteCandidate(@PathVariable String candidateID) {
        if(!electionService.deleteCandidate(candidateID))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object does not exist");
    }

    @GetMapping("/email/{email}")
    public List<Voter> getCodes(@PathVariable String email) {
        return electionService.getCodes(email);
    }
}
