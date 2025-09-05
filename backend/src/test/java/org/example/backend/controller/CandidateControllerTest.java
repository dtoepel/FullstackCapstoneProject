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

    private final Candidate DEFAULT_CANDIDATE = new Candidate(
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
        candidateRepo.deleteAll();
        candidateRepo.save(DEFAULT_CANDIDATE);

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
        candidateRepo.deleteAll();
        candidateRepo.save(DEFAULT_CANDIDATE);

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
        candidateRepo.deleteAll();
        candidateRepo.save(DEFAULT_CANDIDATE);

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
    void deleteCandidateSuccess() throws Exception {
        //GIVEN
        candidateRepo.deleteAll();
        candidateRepo.save(DEFAULT_CANDIDATE);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/election/"+ DEFAULT_CANDIDATE.id()))

                //THEN
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test

    void deleteCandidateFail() throws Exception {
        //GIVEN
        candidateRepo.deleteAll();

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/election/"+ DEFAULT_CANDIDATE.id()))

                //THEN
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}