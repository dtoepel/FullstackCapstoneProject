package org.example.backend.controller;

import org.example.backend.model.db.Candidate;
import org.example.backend.repository.CandidateRepo;
import org.junit.jupiter.api.Test;
import org.example.backend.model.db.Election;
import org.example.backend.repository.ElectionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Vector;

@SpringBootTest
@AutoConfigureMockMvc
public class ElectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ElectionRepo electionRepo;
    @Autowired
    private CandidateRepo candidateRepo;

    @Test
    void getAllProducts() throws Exception {

        //GIVEN
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
                                "electionType": "STV",
                                "candidateType": "Person",
                                "seats": 3
                              }
                            ]
                            """));
    }

    @Test
    void getAllCandidates() throws Exception {

        //GIVEN
        Candidate candidate = new Candidate(
                "-1",
                "John Doe",
                "Independent",
                "#444",
                "some details",
                "Person");
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
                                    "type": "Person"
                                  }
                                ]
                                """))
                .andExpect(MockMvcResultMatchers.jsonPath("$[:1].id").isNotEmpty());

    }

}