package org.example.backend.model.count;

import java.util.Comparator;

class Candidate {
    public org.example.backend.model.db.Candidate candidate;
    public transient double weight = 1.0;
    public transient CandidateStatus status = CandidateStatus.HOPEFUL;
    public double random;

    public Candidate(org.example.backend.model.db.Candidate candidate) {
        this.candidate = candidate;
        this.random = Math.random();
    }

    public String getNameAndParty() {
        return candidate.name() + " (" + (candidate.party()==null?"ind.":candidate.party()) + ")";
    }

    public static enum CandidateStatus {
        ELECTED, HOPEFUL, EXCLUDED
    }

    public static class VoteSorter implements Comparator<Candidate> {
        private final Count count;

        public VoteSorter(Count count) {
            this.count = count;
        }

        @Override
        public int compare(Candidate c1, Candidate c2) {
            if(count.voteCount().get(c1) > count.voteCount().get(c2)) return 1;
            if(count.voteCount().get(c1) < count.voteCount().get(c2)) return -1;
            return Double.compare(c1.random, c2.random);
        }
    }
}
