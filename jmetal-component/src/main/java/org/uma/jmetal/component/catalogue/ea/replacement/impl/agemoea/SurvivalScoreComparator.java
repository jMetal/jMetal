package org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * Comparator based on the survival score of AGE-MOEA
 *
 * @author Annibale Panichella
 */
public class SurvivalScoreComparator<S extends Solution<?>> implements Comparator<S> {
    private FastNonDominatedSortRanking<S> ranking;

    public SurvivalScoreComparator() {
        ranking = new FastNonDominatedSortRanking<>(new DominanceWithConstraintsComparator<>());
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

        Object attr1 = solution1.attributes().get(AGEMOEAEnvironmentalSelection.getAttributeId());
        Object attr2 = solution2.attributes().get(AGEMOEAEnvironmentalSelection.getAttributeId());

        double distance1 = attr1 != null ? (double) attr1 : 0.0;
        double distance2 = attr2 != null ? (double) attr2 : 0.0;

        if (distance1 > distance2) {
           return -1;
        }
        if (distance1 < distance2) {
            return +1;
        }

        return 0;
    }
}
