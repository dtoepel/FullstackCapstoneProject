package org.example.backend.model.count;

import java.util.ArrayList;
import java.util.List;

class Vote {

    private final double amount;
    private final ArrayList<Candidate> ranking;

    public Vote(org.example.backend.model.db.Vote vote, List<Candidate> candidates, double amount) {
        ranking = new ArrayList<>();
        this.amount = amount;
        for(String cId : vote.rankingIDs()) {
            for(Candidate m : candidates) {
                if(m.getDbCandidate().id().equals(cId)) {
                    ranking.add(m);
                }
            }
        }
    }

    public double getAmount() {
        return amount;
    }

    public List<Candidate> getRanking() {
        return ranking;
    }
}