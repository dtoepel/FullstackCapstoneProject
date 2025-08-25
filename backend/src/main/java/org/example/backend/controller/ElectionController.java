package org.example.backend.controller;

import org.example.backend.model.db.Election;
import org.example.backend.service.ElectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/election")
public class ElectionController {
    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping
    public List<Election> getAllElections() {

        System.out.println("controller");
        return electionService.getAllElections(); }
}
