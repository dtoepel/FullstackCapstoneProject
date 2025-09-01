package org.example.backend.controller;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Vote;
import org.example.backend.repository.CandidateRepo;
import org.junit.jupiter.api.Test;
import org.example.backend.model.db.Election;
import org.example.backend.repository.ElectionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ElectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ElectionRepo electionRepo;
    @Autowired
    private CandidateRepo candidateRepo;

    Candidate defaultCandidate = new Candidate(
            "1",
            "John Doe",
            "Independent",
            "#444",
            "some details",
            "Person",
            false);

    Election defaultElection = new Election(
            "1",
            "MyElection",
            "some details",
            new ArrayList<>(),
            new ArrayList<>(),
            Election.ElectionState.OPEN,
            Election.ElectionType.STV,
            "Person",
            3);
    @Test
    void getAllProducts() throws Exception {

        //GIVEN
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
        electionRepo.save(election);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election"))
            //THEN
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(
                """ 
                            [
                              {
                                "id": "1",
                                "name": "MyElection",
                                "description": "some details",
                                "candidateIDs": [],
                                "votes": [],
                                "electionState": "OPEN",
                                "electionMethod": "STV",
                                "candidateType": "Person",
                                "seats": 3
                              }
                            ]
                            """));
    }


    @Test
    void createElectionSuccess() throws Exception {
        //GIVEN
        Election existingElection = defaultElection;
        electionRepo.deleteAll();
        electionRepo.save(existingElection);
        //WHEN

        mockMvc.perform(MockMvcRequestBuilders.post("/api/election")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "2",
                                    "name": "MyElection",
                                    "description": "some details",
                                    "candidateIDs": [],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 3
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(
                        """ 
                                {
                                    "id": "2",
                                    "name": "MyElection",
                                    "description": "some details",
                                    "candidateIDs": [],
                                    "electionState": "OPEN",
                                    "votes": [],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 3
                                }
                                """));
    }

    @Test
    void createElectionDuplicate() throws Exception {
        //GIVEN
        Election existingElection = defaultElection;
        electionRepo.deleteAll();
        electionRepo.save(existingElection);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "1",
                                    "name": "MyElection",
                                    "description": "some details",
                                    "candidateIDs": [],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 3
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.DuplicateIdException.reason));
    }


    @Test
    void updateElectionSuccess() throws Exception {
        //GIVEN
        Election existingElection = defaultElection;
        electionRepo.deleteAll();
        electionRepo.save(existingElection);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "1",
                                    "name": "MyElection",
                                    "description": "some other details",
                                    "candidateIDs": [],
                                    "electionState": "OPEN",
                                    "votes": [],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 4
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().json(
                        """ 
                                {
                                    "id": "1",
                                    "name": "MyElection",
                                    "description": "some other details",
                                    "candidateIDs": [],
                                    "electionState": "OPEN",
                                    "votes": [],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 4
                                }
                                """));
    }

    @Test
    void updateElectionNotFound() throws Exception {
        //GIVEN
        Election existingElection = defaultElection;
        electionRepo.deleteAll();
        electionRepo.save(existingElection);

        //WHE
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "404",
                                    "name": "MyElection",
                                    "description": "some other details",
                                    "candidateIDs": [],
                                    "electionState": "OPEN",
                                    "votes": [],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 4
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IdNotFoundException.reason));
    }

    @Test
    void getAllCandidates() throws Exception {

        //GIVEN
        Candidate candidate = defaultCandidate;
        candidateRepo.deleteAll();
        candidateRepo.save(candidate);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election/candidates"))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                    """ 
                                [
                                  {
                                    "name": "John Doe",
                                    "description": "some details",
                                    "party": "Independent",
                                    "color": "#444",
                                    "type": "Person",
                                    "archived": false
                                  }
                                ]
                                """))
                .andExpect(MockMvcResultMatchers.jsonPath("$[:1].id").isNotEmpty());

    }

    @Test
    void createCandidateSuccess() throws Exception {
        //GIVEN

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "John Doe",
                                    "description": "some details",
                                    "party": "Independent",
                                    "color": "#444",
                                    "type": "Person",
                                    "archived": false
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(
                        """ 
                                {
                                    "name": "John Doe",
                                    "description": "some details",
                                    "party": "Independent",
                                    "color": "#444",
                                    "type": "Person",
                                    "archived": false
                                }
                                """))
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty());
    }

    @Test
    void updateCandidateSuccess() throws Exception {
        //GIVEN
        Candidate candidate = defaultCandidate;
        candidateRepo.deleteAll();
        candidateRepo.save(candidate);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "1",
                                    "name": "Don Joe",
                                    "description": "some details",
                                    "party": "Independent",
                                    "color": "#555",
                                    "type": "Person",
                                    "archived": false
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().json(
                        """ 
                                {
                                    "id": "1",
                                    "name": "Don Joe",
                                    "description": "some details",
                                    "party": "Independent",
                                    "color": "#555",
                                    "type": "Person",
                                    "archived": false
                                }
                                """));
    }

    @Test
    void updateCandidateNotFound() throws Exception {
        //GIVEN
        Candidate candidate = defaultCandidate;
        candidateRepo.deleteAll();
        candidateRepo.save(candidate);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "2",
                                    "name": "Don Joe",
                                    "description": "some details",
                                    "party": "Independent",
                                    "color": "#555",
                                    "type": "Person",
                                    "archived": false
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason(Candidate.IdNotFoundException.reason));
    }

    @Test
    void getElectionResults() throws Exception {
        //GIVEN
        Election existingElection = new Election(
                "myID",
                "any",
                "any",
                Arrays.asList("A", "B"),
                List.of(new Vote(List.of("A"))),
                Election.ElectionState.OPEN,
                Election.ElectionType.STV,
                "any",
                1);
        electionRepo.save(existingElection);
        candidateRepo.save(new Candidate("A", "Alice", "", "", "", "", false));
        candidateRepo.save(new Candidate("B", "Bob", "", "", "", "", false));
        candidateRepo.save(new Candidate("C", "Charlie", "", "", "", "", false));

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election/results/myID"))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[:1].candidateID").value("A"));


    }

    @Test
    void getElectionResults404() throws Exception {
        //GIVEN
        electionRepo.deleteAll();

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election/results/myID"))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IdNotFoundException.reason));

    }

    @Test
    void deleteElectionSuccess() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(defaultElection);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/election/"+defaultElection.id()))

                //THEN
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test

    void deleteElectionFail() throws Exception {
        //GIVEN
        electionRepo.deleteAll();

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/election/"+defaultElection.id()))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}