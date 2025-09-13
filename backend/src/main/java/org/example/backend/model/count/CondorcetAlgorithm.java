package org.example.backend.model.count;

import org.example.backend.model.db.Vote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CondorcetAlgorithm {
    private CondorcetAlgorithm() {}

    public static CondorcetResult performCondorcetAlgorithm(List<String> candidatesArg,
                         List<org.example.backend.model.db.Vote> votesArgs) {

        /*
        For this algorithm the candidates and votes are stateless, so the .db. version of the votes can be used,
        and the candidates remain ids.
        The frontend can do the rest
        */

        final ArrayList<String> candidates = new ArrayList<>(candidatesArg);
        final ArrayList<org.example.backend.model.db.Vote> votes = new ArrayList<>(votesArgs);
        final int[][] duels;

        /* initialize the result matrix:
           the first index is a candidate, which is compared to the candidate in the second index
           then there is a counter, counting victories (+1) vs defeats (-1)
        */

        duels = new int[candidates.size()][candidates.size()];
        for(int i = 0; i < candidates.size(); i++) {
            for(int j = 0; j < candidates.size(); j++) {
                duels[i][j] = 0;
            }
        }

        countDuels(duels, candidates, votes);

        /* now the candidates have been paired and compared.
           the candidates are now ranked, how often they won each duel.
           2 points for a victory, 1 for a draw, to keep using ints
           Each candidate draws against themself, so that cancels out automatically.
         */

        int[] points = new int[candidates.size()];
        for(int i = 0; i < candidates.size(); i++) {
            points[i] = 0;
            for(int j = 0; j < candidates.size(); j++) {
                if(duels[i][j] > 0) points[i]+=2;
                if(duels[i][j] == 0) points[i]++;
            }
        }

        ArrayList<String> candidatesSorted = new ArrayList<>(candidates);

        candidatesSorted.sort((o1, o2) -> {
            int index1 = candidates.indexOf(o1);
            int index2 = candidates.indexOf(o2);
            Integer points1 = points[index1];
            Integer points2 = points[index2];
            return -points1.compareTo(points2);
        });

        /*
        Now the list of candidates has been sorted, with the candidates with the most points first,
        Where candidates have the same number of points, the order remains as by the original order
         */

        /*
        the results, in particular the duels, are usually in an inconvenient order
        But, as the order of the candidates is now known, the algorithm is invoked again,
        doing the exact ame task, just the order of the duel matrix is in a convenient order.

        To break the recursion, the algorithm is only invoked another time, if the order changed
         */

        if(candidates.equals(candidatesSorted)) {
            return new CondorcetResult(candidates, duels);
        } else {
            return performCondorcetAlgorithm(candidatesSorted, votes);
        }
    }

    private static void countDuels(int[][] duels, ArrayList<String> candidates, ArrayList<Vote> votes) {
        /*
           the diagonal is pointless as is it compares candidates with themselves,
           also the matrix is negated and mirrored at the diagonal, therefore only the upper right half is counted
           and immediately copied to the bottom left for convenience
         */

        for(int i = 0; i < candidates.size(); i++) {
            for(int j = i+1; j < candidates.size(); j++) { // only upper right
                String c1 =  candidates.get(i);
                String c2 =  candidates.get(j);
                for(org.example.backend.model.db.Vote v : votes) {
                    int index1 = v.rankingIDs().indexOf(c1);
                    int index2 = v.rankingIDs().indexOf(c2);

                    if(index1 != index2) evalDuel(duels, i, j, index1, index2);
                }
            }
        }
    }

    private static void evalDuel(int[][] duels, int i, int j, int index1, int index2) {
        // the case that both are unranked has already been covered. Else:
        if(index1 == -1)
            inc(duels, j, i); // 2 ranked, 1 unranked, 2 wins
        else if(index2 == -1)
            inc(duels, i, j); // 1 ranked, 2 unranked, 1 wins
        else if(index1 < index2)
            inc(duels, i, j); // 1 wins
        else
            inc(duels, j, i); // 2 wins
    }

    private static void inc(int[][] duels, int a, int b) {
        duels[a][b]++;
        duels[b][a]--;
    }

    public record CondorcetResult(
            List<String> candidateIDs,
            int[][] duels
    ) {
        @Override
        public String toString() {
            return "CondorcetResult{" +
                    "candidateIDs=" + candidateIDs +
                    ", duels=" + Arrays.toString(duels) +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            CondorcetResult that = (CondorcetResult) o;
            return Objects.deepEquals(duels, that.duels) && Objects.equals(candidateIDs, that.candidateIDs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(candidateIDs, Arrays.deepHashCode(duels));
        }
    }
}
