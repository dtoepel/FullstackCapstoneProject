package org.example.backend.controller;

import org.example.backend.model.db.Candidate;
import org.example.backend.repository.CandidateRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}