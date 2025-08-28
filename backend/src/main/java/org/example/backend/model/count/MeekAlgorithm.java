package org.example.backend.model.count;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import static org.example.backend.model.count.Candidate.CandidateStatus.*;

public class MeekAlgorithm {

    private final Vector<org.example.backend.model.count.Candidate> candidates;
    private final Vector<org.example.backend.model.count.Vote> votes;
    private final int seats;

    private final HashMap<org.example.backend.model.db.Candidate, Integer> firstVotes;

    /**
     * Inits the Algorithm with a list of candidates and a list of votes cast.
     * This does not start the distribution, so that other parameters can be set before.
     * Initializes all candidates as if reset had been invoked.
     * The distribution is performed using perform().
     * @param candidates The candidates standing in the election
     * @param votes The votes cast
     * @param seats The number of seats to be awarded
     */
    public MeekAlgorithm(Vector<org.example.backend.model.db.Candidate> candidates,
                         Vector<org.example.backend.model.db.Vote> votes, int seats)  {
        this.candidates = new Vector<>();
        this.firstVotes = new HashMap<>();
        for(org.example.backend.model.db.Candidate c : candidates) {
            this.candidates.add(new Candidate(c));
            this.firstVotes.put(c, 0);
        }
        this.votes = new Vector<>();
        for(org.example.backend.model.db.Vote v : votes) {
            this.votes.add(new Vote(v, this.candidates));
            if(!v.rankingIDs().isEmpty()) {
                for(org.example.backend.model.db.Candidate c : candidates) {
                    if(c.id().equals(v.rankingIDs().firstElement())) {
                        this.firstVotes.put(c, this.firstVotes.get(c) + 1);
                    }
                }
            }
        }
        this.seats = seats;
        reset();
    }

    /**
     * Distributes the available seats to the standing candidates.
     * The status of all candidates should be HOPEFUL,
     * which is the case right after using the constructor.
     * Other configurations may yield unexpected results.
     * Candidates which have a different status will keep that status.
     *
     */
    public DetailedResult perform() {
        DetailedResult result = new DetailedResult();

        // next iteration:
        // what to do next?
        // first count remaining (!ELIMINATED) Candidate
        while(getRemainingCandidatesCount()>seats) {
            //int remainingCandidates = getRemainingCandidatesCount();

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
            if (c.status == HOPEFUL) {
                c.status = ELECTED;

                result.recordElectedCandidate(c);
            }
        }
        result.recordVoteCount(countVotes());
        return result;
    }

    private void excludeLast(DetailedResult result) {
        Count count = countVotes();
        Vector<Candidate> hopefulCandidates = new Vector<>();
        for(Candidate c : candidates) if (c.status == HOPEFUL) hopefulCandidates.add(c);
        hopefulCandidates.sort(new Candidate.VoteSorter(count));

        hopefulCandidates.getFirst().status = EXCLUDED;
        hopefulCandidates.getFirst().weight = 0.0;

        result.recordExcludedCandidate(hopefulCandidates.getFirst());
    }

    private int electCandidates(DetailedResult result) {
        Count count = countVotes();
        int newlyElected = 0;
        Vector<Candidate> hopefulCandidates = new Vector<>();
        for(Candidate c : candidates) if (c.status == HOPEFUL) hopefulCandidates.add(c);
        hopefulCandidates.sort(new Candidate.VoteSorter(count));

        for(Candidate c : hopefulCandidates) {
            if(count.voteCount().get(c) >= count.quota()) {
                c.status = ELECTED;
                newlyElected++;
                result.recordElectedCandidate(c);
            }
        }
        return newlyElected;
    }

    private void findWeights() {
        Count count = countVotes();
        boolean ok = true;
        for(Candidate c : candidates) {
            if(c.status == ELECTED) {
                if(count.voteCount().get(c)/count.quota() < 0.9999) ok = false;
                if(count.voteCount().get(c)/count.quota() > 1.0001) ok = false;
            }
        }
        if(!ok) {
            for(Candidate c : candidates) {
                if(c.status == ELECTED) {
                    c.weight = c.weight * count.quota() / count.voteCount().get(c);
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
        for(Candidate c : candidates) {
            voteCount.put(c, 0.0);
        }
        // then process ballot by ballot
        for(Vote v : votes) {
            double vote = v.amount;
            total+=vote;
            for(Candidate c : v.ranking) {
                // for each rank, add the remaining vote * weight onto the candidate's count
                // and deduct that from the remaining vote
                voteCount.put(c, voteCount.get(c) + vote * c.weight);
                vote -= vote * c.weight;
            }
        }

        // to calculate the excess, count the number of votes and deduct non-exhausted, i.e. counted votes
        excess = total;
        for(Candidate c : candidates) excess-=voteCount.get(c);

        return new Count(voteCount, total, excess, (total-excess)/(seats+.999));
    }

    private int getRemainingCandidatesCount() {
        int i = 0;
        for(Candidate c : candidates) {
            if(c.status != EXCLUDED) i++;
        }
        return i;
    }

    /**
     * Resets the algorithm, setting the status of all candidates to HOPEFUL.
     */
    public void reset() {
        for(Candidate c : candidates) {
            c.status = HOPEFUL;
            c.weight = 1.;
        }
    }

    public Vector<ElectionResultItem> getResult() {
        Vector<ElectionResultItem> result = new Vector<>();

        for(Candidate c : candidates) {
            org.example.backend.model.db.Candidate candidate = c.candidate;
            result.add(new ElectionResultItem(
                    candidate,
                    FORMAT.format(100.*firstVotes.get(candidate)/votes.size())+"%",
                    c.status!=EXCLUDED,
                    c.status!=EXCLUDED?"default":"void"));
        }

        Collections.sort(result, (o1, o2) ->
                firstVotes.get(o1.candidate).compareTo(firstVotes.get(o2.candidate)));
        Collections.reverse(result);
        return result;
    }

    public record ElectionResultItem(org.example.backend.model.db.Candidate candidate, String firstVotes, boolean elected, String electedAs) {}
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
}
