package org.example.backend.model.count;

class Candidate {
    private final org.example.backend.model.db.Candidate dbCandidate;
    private double weight = 1.0;
    private CandidateStatus status = CandidateStatus.HOPEFUL;
    public enum CandidateStatus {ELECTED, HOPEFUL, EXCLUDED}
    private final double random;


    public Candidate(org.example.backend.model.db.Candidate dbCandidate) {
        this.dbCandidate = dbCandidate;
        this.random = 1. * dbCandidate.name().hashCode() / Integer.MAX_VALUE;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
        if(this.status == CandidateStatus.HOPEFUL) {weight = 1.;}
        else if(this.status == CandidateStatus.EXCLUDED) {weight = 0.;}
    }

    CandidateStatus getStatus() { return status; }
    org.example.backend.model.db.Candidate getDbCandidate() { return dbCandidate; }
    double getWeight() { return weight; }
    void setWeight(double weight) { this.weight = weight; }
    double getRandom() { return random; }

}
