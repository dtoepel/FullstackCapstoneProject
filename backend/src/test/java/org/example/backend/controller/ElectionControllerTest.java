package org.example.backend.controller;

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
    private ElectionRepo repo;

    @Test
    void getAllProducts() throws Exception {

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

        repo.save(election);
        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election"))
            //THEN
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(
                """ 
                            [
                              {
                                "id": "1",
                                "name": "MyElection"
                              }
                            ]
                            """));
    }
}