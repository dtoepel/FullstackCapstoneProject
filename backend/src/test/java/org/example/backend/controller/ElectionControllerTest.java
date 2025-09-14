package org.example.backend.controller;

import org.example.backend.model.db.Candidate;
import org.example.backend.model.db.Vote;
import org.example.backend.model.db.Voter;
import org.example.backend.repository.CandidateRepo;
import org.example.backend.repository.VoterRepo;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
class ElectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ElectionRepo electionRepo;
    @Autowired
    private CandidateRepo candidateRepo;
    @Autowired
    private VoterRepo voterRepo;

    final Election OPEN_ELECTION = new Election(
            "id1",
            "MyElection",
            "some details",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            Election.ElectionState.OPEN,
            Election.ElectionType.STV,
            "Person",
            3);

    final Election VOTING_ELECTION = new Election(
            "id2",
            "MyElection",
            "some details",
            Arrays.asList("candidate1", "candidate2"),
            new ArrayList<>(),
            Arrays.asList("voter1@example.com", "voter2@example.com"),
            Election.ElectionState.VOTING,
            Election.ElectionType.STV,
            "Person",
            1);

    final Election CLOSED_ELECTION = new Election(
            "id3",
            "MyElection",
            "some details",
            Arrays.asList("A", "B", "C", "D", "E"),
            Arrays.asList(
                    new Vote(Arrays.asList("C", "B", "E", "A", "D")),
                    new Vote(Arrays.asList("E", "B", "A", "D", "C")),
                    new Vote(Arrays.asList("B", "A", "D", "C")),
                    new Vote(Arrays.asList("D", "A", "C")),
                    new Vote(Arrays.asList("A", "C"))),
            Arrays.asList("voter1@example.com", "voter2@example.com"),
            Election.ElectionState.VOTING,
            Election.ElectionType.STV,
            "Person",
            1);

    @Test
    void getAllElections() throws Exception {

        //GIVEN
        electionRepo.save(OPEN_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election"))
            //THEN
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(
                """ 
                            [
                              {
                                "id": "id1",
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
        electionRepo.deleteAll();
        electionRepo.save(OPEN_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election")
                        .with(user("any-authenticated-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "id2",
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
                            "id": "id2",
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
    void createElectionFail401() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(OPEN_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "id2",
                                    "name": "MyElection",
                                    "description": "some details",
                                    "candidateIDs": [],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 3
                                }
                                """))

        //THEN
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void createElectionDuplicate() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(OPEN_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("any-authenticated-user"))
                        .content("""
                                {
                                    "id": "id1",
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
                .andExpect(MockMvcResultMatchers.status().reason(Election.DuplicateIdException.REASON));
    }

    @Test
    void updateOpenElectionSuccess() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(OPEN_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .with(user("any-authenticated-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "id1",
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
                                "id": "id1",
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
    void updateOpenElectionFail401() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(OPEN_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "id1",
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
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void updateNonOpenElectionSuccess() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("any-authenticated-user"))
                        .content("""
                                {
                                    "id": "id2",
                                    "name": "My Election",
                                    "description": "some other details",
                                    "candidateIDs": ["candidate1", "candidate2"],
                                    "electionState": "VOTING",
                                    "votes": [],
                                    "voterEmails": ["voter1@example.com", "voter2@example.com"],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 1
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().json(
                        """ 
                                {
                                    "id": "id2",
                                    "name": "My Election",
                                    "description": "some other details",
                                    "candidateIDs": ["candidate1", "candidate2"],
                                    "electionState": "VOTING",
                                    "votes": [],
                                    "voterEmails": ["voter1@example.com", "voter2@example.com"],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 1
                                }
                                """));
    }

    @Test
    void updateElectionNotFound() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(OPEN_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .with(user("any-authenticated-user"))
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
                .andExpect(MockMvcResultMatchers.status().reason(Election.IdNotFoundException.REASON));
    }

    @Test
    void updateElectionFailVotes() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .with(user("any-authenticated-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "id2",
                                    "name": "MyElection",
                                    "description": "some details",
                                    "candidateIDs": ["candidate1", "candidate2"],
                                    "electionState": "VOTING",
                                    "votes": [{"rankingIDs": ["A","B"]}],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 1
                                }
                                """))
        //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_CANNOT_CHANGE_VOTES));
    }

    @Test
    void updateElectionFailSeats() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .with(user("any-authenticated-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "id2",
                                    "name": "MyElection",
                                    "description": "some details",
                                    "candidateIDs": ["candidate1", "candidate2"],
                                    "electionState": "VOTING",
                                    "votes": [],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 3
                                }
                                """))
        //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_CANNOT_CHANGE_SEATS));
    }

    @Test
    void updateElectionFailCandidates() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .with(user("any-authenticated-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "id2",
                                    "name": "MyElection",
                                    "description": "some details",
                                    "candidateIDs": ["candidate2", "candidate1"],
                                    "electionState": "VOTING",
                                    "votes": [],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 1
                                }
                                """))
        //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_CANNOT_CHANGE_CANDIDATES));
    }

    @Test
    void updateElectionFailType() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .with(user("any-authenticated-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "id2",
                                    "name": "MyElection",
                                    "description": "some details",
                                    "candidateIDs": ["candidate1", "candidate2"],
                                    "electionState": "VOTING",
                                    "votes": [],
                                    "candidateType": "Foo",
                                    "electionMethod": "STV",
                                    "seats": 1
                                }
                                """))
        //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_CANNOT_CHANGE_TYPE));
    }

    @Test
    void updateElectionFailMethod() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .with(user("any-authenticated-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "id2",
                                    "name": "MyElection",
                                    "description": "some details",
                                    "candidateIDs": ["candidate1", "candidate2"],
                                    "electionState": "VOTING",
                                    "votes": [],
                                    "candidateType": "Person",
                                    "electionMethod": "VICE",
                                    "seats": 1
                                }
                                """))
        //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_CANNOT_CHANGE_METHOD));
    }

    @Test
    void updateElectionFailStatus() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .with(user("any-authenticated-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "id2",
                                    "name": "MyElection",
                                    "description": "some details",
                                    "candidateIDs": ["candidate1", "candidate2"],
                                    "electionState": "OPEN",
                                    "votes": [],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 1
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_CANNOT_CHANGE_STATUS));
    }

    @Test
    void updateElectionFailVoters() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/election")
                        .with(user("any-authenticated-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "id2",
                                    "name": "MyElection",
                                    "description": "some details",
                                    "candidateIDs": ["candidate1", "candidate2"],
                                    "electionState": "VOTING",
                                    "votes": [],
                                    "voterEmails": ["voter3@example.com"],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 1
                                }
                                """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_CANNOT_CHANGE_VOTERS));
    }

    @Test
    void advanceOpenElectionSuccess() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        voterRepo.deleteAll();
        electionRepo.save(new Election(
                OPEN_ELECTION.id(), OPEN_ELECTION.name(), OPEN_ELECTION.description(),
                Arrays.asList("cId1", "cId2", "cId3"), OPEN_ELECTION.votes(),
                Arrays.asList("voter1@example.com", "voter2@example.com"),
                OPEN_ELECTION.electionState(), OPEN_ELECTION.electionMethod(),
                OPEN_ELECTION.candidateType(), 2));

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/advance/id1")
                        .with(user("any-authenticated-user")))

        //THEN
        .andExpect(MockMvcResultMatchers.status().isAccepted());

        assertEquals(2, voterRepo.findAll().size());
        assertEquals(Election.ElectionState.VOTING, electionRepo.findById("id1").orElseThrow().electionState());
    }

    @Test
    void advanceOpenElectionFail401() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        voterRepo.deleteAll();
        electionRepo.save(new Election(
                OPEN_ELECTION.id(), OPEN_ELECTION.name(), OPEN_ELECTION.description(),
                Arrays.asList("cId1", "cId2", "cId3"), OPEN_ELECTION.votes(),
                Arrays.asList("voter1@example.com", "voter2@example.com"),
                OPEN_ELECTION.electionState(), OPEN_ELECTION.electionMethod(),
                OPEN_ELECTION.candidateType(), 2));

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/advance/id1"))

        //THEN
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void advanceElectionFailNotFound() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(OPEN_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/advance/id2")
                        .with(user("any-authenticated-user")))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IdNotFoundException.REASON));
    }

    @Test
    void advanceElectionFailTooFewCandidates() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(OPEN_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/advance/id1")
                        .with(user("any-authenticated-user")))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_TOO_FEW_CANDIDATES));
    }

    @Test
    void advanceElectionFailTooFewVotes() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/advance/id2")
                        .with(user("any-authenticated-user")))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_TOO_FEW_VOTES));
    }

    @Test
    void advanceElectionSuccessAdvanceToArchived() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION.vote(new Vote(Arrays.asList("A","B"))).advance());

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/advance/id2")
                        .with(user("any-authenticated-user")))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().json(
                        """ 
                                {
                                    "id": "id2",
                                    "name": "MyElection",
                                    "electionState": "ARCHIVED",
                                    "votes": [{"rankingIDs": ["A","B"]}],
                                    "candidateType": "Person",
                                    "electionMethod": "STV",
                                    "seats": 1
                                }
                                """));
    }

    @Test
    void advanceElectionFailAlreadyArchived() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION.vote(new Vote(Arrays.asList("A","B"))).advance().advance());

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/advance/id2")
                        .with(user("any-authenticated-user")))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_ALREADY_ARCHIVED));
    }

    @Test
    void voteElectionFailWrongState() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(OPEN_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/vote/id1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                               "rankingIDs": ["A","B"],
                               "validationCode" : "IRRELEVANT"
                            }
                        """))
                //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_VOTES_CANNOT_BE_CAST));
    }

    @Test
    void voteElectionFailNotFound() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);
        voterRepo.save(new Voter(null, "any@example.com", "id2", "ABCDEF"));

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/vote/id3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                           "rankingIDs": ["A","B"],
                           "validationCode" : "ABCDEF"
                        }
                    """))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IdNotFoundException.REASON));
    }

    @Test
    void voteElectionFailEmpty() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/vote/id2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                               "rankingIDs": [],
                               "validationCode" : "IRRELEVANT"
                            }
                        """))
        //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_NO_EMPTY_VOTES));
    }

    @Test
    void voteElectionFailUnauthorized() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/vote/id2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                               "rankingIDs": ["A","B"],
                               "validationCode" : "IRRELEVANT"
                            }
                        """))
        //THEN
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.status().reason(Election.VoteNotAuthorizedException.REASON));
    }

    @Test
    void voteElectionSuccess() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(VOTING_ELECTION);
        voterRepo.save(new Voter(null, "any@example.com", "id2", "ABCDEF"));

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/election/vote/id2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                               "rankingIDs": ["A","B"],
                               "validationCode" : "ABCDEF"
                            }
                        """))
        //THEN
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertTrue(voterRepo.findAll().isEmpty());
        assertEquals(1, electionRepo.findById("id2").orElseThrow().votes().size());
    }

    @Test
    void getVotes() throws Exception {
        //GIVEN
        voterRepo.deleteAll();
        voterRepo.save(new Voter("vId1", "voter9@example.co.nz", "eId1", "QWERTZ"));
        voterRepo.save(new Voter("vId2", "voter9@example.co.nz", "eId2", "ASDFGH"));
        voterRepo.save(new Voter("vId3", "voter10@example.com.au", "eId2", "YXCVBN"));

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election/email/voter10@example.com.au"))

                //THEN
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                """                     
                    [
                      {
                        "id": "vId3",
                        "email": "voter10@example.com.au",
                        "electionID": "eId2",
                        "validationCode": "YXCVBN"
                      }
                    ]
                    """));

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election/email/voter10@example.co.nz"))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election/email/voter9@example.co.nz"))

        //THEN
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().
                json("""
            [{"id":"vId1","email":"voter9@example.co.nz","electionID":
                "eId1","validationCode":"QWERTZ"},
             {"id":"vId2","email":
                "voter9@example.co.nz","electionID":"eId2","validationCode":"ASDFGH"}]
            """));
    }

    @Test
    void failGetElectionResultsEmpty() throws Exception {
        //GIVEN
        Election existingElection = new Election(
                "myID",
                "any",
                "any",
                Arrays.asList("A", "B"),
                new ArrayList<>(),
                new ArrayList<>(),
                Election.ElectionState.CLOSED,
                Election.ElectionType.STV,
                "any",
                1);
        electionRepo.save(existingElection);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election/results/myID"))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_CANNOT_COUNT_EMPTY_VOTES));

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election/result-analysis/myID"))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.status().reason(Election.IllegalManipulationException.MSG_CANNOT_COUNT_EMPTY_VOTES));
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
                new ArrayList<>(),
                Election.ElectionState.CLOSED,
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
                .andExpect(MockMvcResultMatchers.status().reason(Election.IdNotFoundException.REASON));

    }

    @Test
    void deleteElectionSuccess() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(OPEN_ELECTION);
        voterRepo.deleteAll();
        voterRepo.save(new Voter("1", "1", OPEN_ELECTION.id(), "1"));
        voterRepo.save(new Voter("2", "2", VOTING_ELECTION.id(), "2"));

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/election/"+OPEN_ELECTION.id())
                        .with(user("any-authenticated-user")))

        //THEN
        .andExpect(MockMvcResultMatchers.status().isOk());

        assertEquals(1, voterRepo.findAll().size());
        assertEquals(VOTING_ELECTION.id(), voterRepo.findAll().getFirst().electionID());
    }

    @Test
    void deleteElectionFail401() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(OPEN_ELECTION);
        voterRepo.deleteAll();
        voterRepo.save(new Voter("1", "1", OPEN_ELECTION.id(), "1"));
        voterRepo.save(new Voter("2", "2", VOTING_ELECTION.id(), "2"));

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/election/"+OPEN_ELECTION.id()))

        //THEN
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        assertEquals(2, voterRepo.findAll().size());
    }

    @Test
    void deleteElectionFail() throws Exception {
        //GIVEN
        electionRepo.deleteAll();

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/election/"+OPEN_ELECTION.id())
                        .with(user("any-authenticated-user")))

        //THEN
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void analyseElectionSuccess() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(CLOSED_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election/result-analysis/"+CLOSED_ELECTION.id()))

        //THEN
        .andExpect(MockMvcResultMatchers.status().isOk());

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election/result-condorcet/"+CLOSED_ELECTION.id()))

        //THEN
        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void analyseElectionFail404() throws Exception {
        //GIVEN
        electionRepo.deleteAll();
        electionRepo.save(CLOSED_ELECTION);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election/result-analysis/"+OPEN_ELECTION.id()))

        //THEN
        .andExpect(MockMvcResultMatchers.status().isNotFound());

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/election/result-condorcet/"+OPEN_ELECTION.id()))

        //THEN
        .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}