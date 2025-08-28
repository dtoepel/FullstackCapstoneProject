package org.example.backend.model.count;

import java.util.ArrayList;
class Vote {

    private double amount = 1.;
    private ArrayList<Candidate> ranking;

    public Vote(org.example.backend.model.db.Vote vote, ArrayList<Candidate> candidates) {
        ranking = new ArrayList<>();
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

    public ArrayList<Candidate> getRanking() {
        return ranking;
    }
}