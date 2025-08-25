package org.example.backend.service;

import org.example.backend.model.db.Election;
import org.example.backend.repository.ElectionRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ElectionServiceTest {

    @Test
    void getAllElections() {
        // given

        ElectionRepo electionRepo = Mockito.mock(ElectionRepo.class);
        ElectionService electionService = new ElectionService(electionRepo);
        Election election = new Election(
                "1",
                "MyElection",
                "some details",
                new Vector<>(),
                new Vector<>(),
                Election.ElectionState.OPEN,
                Election.ElectionType.STV,
                "Person",
                3);

        // when
        when(electionRepo.findAll()).thenReturn(java.util.List.of(election));
        java.util.List<Election> result = electionService.getAllElections();

        // then
        assertThat(result)
                .hasSize(1)
                .containsExactly(election);
        verify(electionRepo, times(1)).findAll();
        verifyNoMoreInteractions(electionRepo);
    }
}