package org.example.backend.controller;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Election;
import org.example.backend.model.db.ElectionDTO;
import org.example.backend.service.ElectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Vector;

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
                        new Vector<>(),
                        Election.ElectionState.OPEN,
                        init.electionMethod(),
                        init.candidateType(),
                        init.seats())),
                HttpStatus.CREATED);
        }
    }

    @PutMapping
    public ResponseEntity<Election> updateElection(@RequestBody Election election) {
        List<Election> allElections = electionService.getAllElections();
        if(allElections.stream().filter(electionDB -> electionDB.id().equals(election.id())).toList().isEmpty()) {
            throw new Election.IdNotFoundException();
        } else {
            return new ResponseEntity<>(
                electionService.updateElection(election),
                HttpStatus.ACCEPTED);
        }
    }

    @GetMapping("/candidates")
    public List<Candidate> getAllCandidates() { return electionService.getAllCandidates(); }
}
