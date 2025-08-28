package org.example.backend.controller;

import org.example.backend.model.count.CountService;
import org.example.backend.model.count.DetailedResult;
import org.example.backend.model.count.MeekAlgorithm;
import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Election;
import org.example.backend.service.ElectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/election")
public class ElectionController {
    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping
    public List<Election> getAllElections() { return electionService.getAllElections(); }

    @GetMapping("/results/{electionId}")
    public ResponseEntity<List<DetailedResult.ResultItem>> getElectionResults(@PathVariable("electionId") String electionId) {
        Optional<Election> electionO = electionService.getElectionById(electionId);
        List<Candidate> candidatesDB = electionService.getAllCandidates();
        if(electionO.isPresent()) {
            List<DetailedResult.ResultItem> result = CountService.getElectionResult(electionO.get(), candidatesDB);
            return new ResponseEntity<>(
                    result,
                    HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/candidates")
    public List<Candidate> getAllCandidates() { return electionService.getAllCandidates(); }
}
