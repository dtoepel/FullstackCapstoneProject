package org.example.backend.model.count;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.example.backend.model.count.Candidate.CandidateStatus.*;

public class MeekAlgorithm {

    private final ArrayList<Candidate> candidates;
    private final ArrayList<org.example.backend.model.count.Vote> votes;
    private final int seats;

    /**
     * Inits the Algorithm with a list of candidates and a list of votes cast.
     * This does not start the distribution, so that other parameters can be set before.
     * Initializes all candidates as if reset had been invoked.
     * The distribution is performed using perform().
     *
     * @param candidates The candidates standing in the election
     * @param votes      The votes cast
     * @param seats      The number of seats to be awarded
     */
    public MeekAlgorithm(ArrayList<org.example.backend.model.db.Candidate> candidates,
                         List<org.example.backend.model.db.Vote> votes, int seats) {
        this.candidates = new ArrayList<>();
        for (org.example.backend.model.db.Candidate c : candidates) {
            this.candidates.add(new Candidate(c));
        }
        this.votes = new ArrayList<>();
        for (org.example.backend.model.db.Vote v : votes) {
            this.votes.add(new Vote(v, this.candidates, 1.));
        }
        this.seats = seats;
    }

    /**
     * Distributes the available seats to the standing candidates.
     * The status of all candidates should be HOPEFUL,
     * which is the case right after using the constructor.
     * Other configurations may yield unexpected results.
     * Candidates which have a different status will keep that status.
     */
    public DetailedResult perform() {
        DetailedResult result = new DetailedResult();

        // next iteration:
        // what to do next?
        // first count remaining (!ELIMINATED) Candidate
        while (getRemainingCandidatesCount() > seats) {

            // while there are too many candidates left.
            Count count = countVotes();
            result.recordVoteCount(count);

            // with the new weights (from last iteration), some candidates may be over the quota and become elected.
            // This function changes the status to ELECTED,
            // registers the candidate in the detailed results as elected,
            // and returns the number of newly elected candidates
            int electedCandidates = electCandidates(result);

            // if no candidate was newly elected
            // one candidate is excluded,
            // setting their status to EXCLUDED, and registering the exclusion in the detailed result.
            if (electedCandidates == 0) excludeLast(result);

            // find the new weights.
            // this will reduce the vote share kept by elected candidates,
            // and set the weight for excluded candidates to zero
            findWeights();
        }
        // if candidates have been reduced to available seats, then finalize
        // i.e. set all remaining HOPEFUL to ELECTED
        for (Candidate c : candidates) {
            if (c.getStatus() == HOPEFUL) {
                c.setStatus(ELECTED);

                result.recordElectedCandidate(c);
            }
        }
        result.recordVoteCount(countVotes());
        return result;
    }

    private void excludeLast(DetailedResult result) {
        Count count = countVotes();
        ArrayList<Candidate> hopefulCandidates = new ArrayList<>();
        for (Candidate c : candidates) if (c.getStatus() == HOPEFUL) hopefulCandidates.add(c);
        hopefulCandidates.sort(new VoteSorter(count));

        hopefulCandidates.getFirst().setStatus(EXCLUDED);

        result.recordExcludedCandidate(hopefulCandidates.getFirst());
    }

    private int electCandidates(DetailedResult result) {
        Count count = countVotes();
        int newlyElected = 0;
        ArrayList<Candidate> hopefulCandidates = new ArrayList<>();
        for (Candidate c : candidates) if (c.getStatus() == HOPEFUL) hopefulCandidates.add(c);
        hopefulCandidates.sort(new VoteSorter(count));

        for (Candidate c : hopefulCandidates) {
            if (count.voteCount().get(c) >= count.quota()) {
                c.setStatus(ELECTED);
                newlyElected++;
                result.recordElectedCandidate(c);
            }
        }
        return newlyElected;
    }

    private void findWeights() {
        Count count = countVotes();
        boolean ok = true;
        for (Candidate c : candidates) {
            if (c.getStatus() == ELECTED) {
                if (count.voteCount().get(c) / count.quota() < 0.9999) ok = false;
                if (count.voteCount().get(c) / count.quota() > 1.0001) ok = false;
            }
        }
        if (!ok) {
            for (Candidate c : candidates) {
                if (c.getStatus() == ELECTED) {
                    c.setWeight(c.getWeight() * count.quota() / count.voteCount().get(c));
                }
            }
            findWeights();
        }
    }

    private Count countVotes() {
        double total = 0.;
        double excess;

        HashMap<Candidate, Double> voteCount = new HashMap<>();
        // initialize all candidates with zero votes
        for (Candidate c : candidates) {
            voteCount.put(c, 0.0);
        }
        // then process ballot by ballot
        for (Vote v : votes) {
            double vote = v.getAmount();
            total += vote;
            for (Candidate c : v.getRanking()) {
                // for each rank, add the remaining vote * weight onto the candidate's count
                // and deduct that from the remaining vote
                voteCount.put(c, voteCount.get(c) + vote * c.getWeight());
                vote -= vote * c.getWeight();
            }
        }

        // to calculate the excess, count the number of votes and deduct non-exhausted, i.e. counted votes
        excess = total;
        for (Candidate c : candidates) excess -= voteCount.get(c);

        return new Count(voteCount, total, excess, (total - excess) / (seats + .999));
    }

    private int getRemainingCandidatesCount() {
        int i = 0;
        for (Candidate c : candidates) {
            if (c.getStatus() != EXCLUDED) i++;
        }
        return i;
    }
}
