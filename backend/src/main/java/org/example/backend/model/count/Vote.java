package org.example.backend.model.count;

import java.util.Vector;

class Vote {

    public double amount = 1.;
    public Vector<Candidate> ranking;

    public Vote(org.example.backend.model.db.Vote vote, Vector<Candidate> candidates) {
        ranking = new Vector<>();
        for(String cId : vote.rankingIDs()) {
            for(Candidate m : candidates) {
                if(m.candidate.id().equals(cId)) {
                    ranking.add(m);
                }
            }
        }
    }
}