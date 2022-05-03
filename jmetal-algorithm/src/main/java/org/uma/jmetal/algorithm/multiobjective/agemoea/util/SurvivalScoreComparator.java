package org.uma.jmetal.algorithm.multiobjective.agemoea.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

import java.util.Comparator;

/**
 * Comparator based on the survival score of AGE-MOEA
 *
 * @author Annibale Panichella
 */
public class SurvivalScoreComparator<S extends Solution<?>> implements Comparator<S> {
    private FastNonDominatedSortRanking ranking;

    public SurvivalScoreComparator() {
        ranking = new FastNonDominatedSortRanking();
    }

    /**
     * Compares two solutions.
     *
     * @param solution1 Object representing the first solution
     * @param solution2 Object representing the second solution.
     * @return -1, or 0, or 1 if solution1 is less than, equal, or greater than solution2,
     * respectively.
     */
    @Override
    public int compare(S solution1, S solution2) {
        if (ranking.getRank(solution1) < ranking.getRank(solution2))
            return -1;

        if (ranking.getRank(solution1) > ranking.getRank(solution2))
            return +1;

        double distance1 = (double) solution1.attributes().get(AGEMOEA2EnvironmentalSelection.getAttributeId());
        double distance2 = (double) solution2.attributes().get(AGEMOEA2EnvironmentalSelection.getAttributeId());

        if (distance1 > distance2) {
           return -1;
        }
        if (distance1 < distance2) {
            return +1;
        }

        return 0;
    }
}
