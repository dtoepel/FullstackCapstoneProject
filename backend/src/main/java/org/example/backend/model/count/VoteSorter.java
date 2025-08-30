package org.example.backend.model.count;

import java.util.Comparator;

class VoteSorter implements Comparator<Candidate> {
    private final Count count;

    VoteSorter(Count count) {
        this.count = count;
    }

    @Override
    public int compare(Candidate c1, Candidate c2) {
        if(count.voteCount().get(c1) > count.voteCount().get(c2)) return 1;
        if(count.voteCount().get(c1) < count.voteCount().get(c2)) return -1;
        return Double.compare(c1.getRandom(), c2.getRandom());
    }
}