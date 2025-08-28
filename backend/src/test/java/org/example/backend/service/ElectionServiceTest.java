package org.example.backend.service;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Election;
import org.example.backend.repository.CandidateRepo;
import org.example.backend.repository.ElectionRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ElectionServiceTest {

    @Test
    void getAllElections() {
        // GIVEN
        ElectionRepo electionRepo = Mockito.mock(ElectionRepo.class);
        CandidateRepo candidateRepo = Mockito.mock(CandidateRepo.class);
        ElectionService electionService = new ElectionService(electionRepo, candidateRepo);
        Election election = new Election(
                "1",
                "MyElection",
                "some details",
                new ArrayList<>(),
                new ArrayList<>(),
                Election.ElectionState.OPEN,
                Election.ElectionType.STV,
                "Person",
                3);

        // WHEN
        when(electionRepo.findAll()).thenReturn(java.util.List.of(election));
        java.util.List<Election> result = electionService.getAllElections();

        // THEN
        assertThat(result)
                .hasSize(1)
                .containsExactly(election);
        verify(electionRepo, times(1)).findAll();
        verifyNoMoreInteractions(electionRepo);
    }

    @Test
    void createElection() {
        //GIVEN
        ElectionRepo electionRepo = Mockito.mock(ElectionRepo.class);
        CandidateRepo candidateRepo = Mockito.mock(CandidateRepo.class);
        ElectionService electionService = new ElectionService(electionRepo, candidateRepo);
        Election election = new Election(
                "1",
                "MyElection",
                "some details",
                new ArrayList<>(),
                new ArrayList<>(),
                Election.ElectionState.OPEN,
                Election.ElectionType.STV,
                "Person",
                3);
        //WHEN
        when(electionRepo.save(election)).thenReturn(election);
        Election result = electionService.createElection(election);

        //THEN
        assertThat(result)
                .isEqualTo(election);
        verify(electionRepo, times(1)).save(election);
        verifyNoMoreInteractions(electionRepo);
    }

    @Test
    void getAllCandidates() {
        // given

        ElectionRepo electionRepo = Mockito.mock(ElectionRepo.class);
        CandidateRepo candidateRepo = Mockito.mock(CandidateRepo.class);
        ElectionService electionService = new ElectionService(electionRepo, candidateRepo);
        Candidate candidate = new Candidate(
                "-1",
                "John Doe",
                "Independent",
                "#444",
                "some details",
                "Person");

        // when
        when(candidateRepo.findAll()).thenReturn(java.util.List.of(candidate));
        java.util.List<Candidate> result = electionService.getAllCandidates();

        // then
        assertThat(result)
                .hasSize(1)
                .containsExactly(candidate);
        verify(candidateRepo, times(1)).findAll();
        verifyNoMoreInteractions(candidateRepo);
    }
}