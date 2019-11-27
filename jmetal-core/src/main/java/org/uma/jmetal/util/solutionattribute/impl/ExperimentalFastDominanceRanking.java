package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import ru.ifmo.nds.JensenFortinBuzdalov;
import ru.ifmo.nds.NonDominatedSorting;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an implementation of the {@link Ranking} interface using non-dominated sorting algorithms
 * from the <a href="https://github.com/mbuzdalov/non-dominated-sorting">non-dominated sorting repository</a>.
 *
 * It uses the solution's objectives directly, and not the dominance comparators,
 * as the structure of the efficient algorithms requires it to be this way.
 *
 * Additionally, if {@link ConstraintHandling#overallConstraintViolationDegree(Solution)} is less than
 * zero, it is used as a preliminary comparison key: all solutions are first sorted in the decreasing order of this
 * property, then non-dominated sorting is executed for each block of solutions with equal value of this property.
 *
 * @param <S> the exact type of a solution.
 * @author Maxim Buzdalov
 */
public class ExperimentalFastDominanceRanking<S extends Solution<?>>
        extends GenericSolutionAttribute<S, Integer> implements Ranking<S> {
    private final List<List<S>> subFronts = new ArrayList<>();
    private final NonDominatedFrontAssigner<S> assigner = new NonDominatedFrontAssigner<>();

    /**
     * Creates an instance of the experimental fast dominance ranking.
     *
     * Note that this class has the same identifier as the {@link DominanceRanking} class,
     * as this class is intended to be a drop-in replacement.
     */
    @SuppressWarnings("WeakerAccess")
    public ExperimentalFastDominanceRanking() {
        super(DominanceRanking.class);
    }

    @Override
    public Ranking<S> computeRanking(List<S> solutionList) {
        assigner.assignFronts(solutionList, subFronts);
        for (int rank = 0; rank < subFronts.size(); ++rank) {
            Integer theRank = rank;
            for (S solution : subFronts.get(rank)) {
                setAttribute(solution, theRank);
            }
        }
        return this;
    }

    @Override
    public List<S> getSubFront(int rank) {
        return subFronts.get(rank);
    }

    @Override
    public int getNumberOfSubFronts() {
        return subFronts.size();
    }

    /**
     * The main workhorse of this implementation of dominance ranking.
     * It is implemented using non-dominated sorting algorithms
     * from the <a href="https://github.com/mbuzdalov/non-dominated-sorting">non-dominated sorting repository</a>.
     *
     * @param <S> the type of solutions.
     * @author Maxim Buzdalov
     */
    public static class NonDominatedFrontAssigner<S extends Solution<?>> {
        // Constraint violation checking support.
        private final OverallConstraintViolationComparator<S> overallConstraintViolationComparator
                = new OverallConstraintViolationComparator<>();

        // Delegation.
        private NonDominatedSorting sortingInstance = null;

        /**
         * Runs non-dominated sorting on the given solutions and put them into fronts accordingly.
         *
         * This implementation also accounts for overall constraint violation,
         * see {@link ConstraintHandling#overallConstraintViolationDegree(Solution)}.
         * Solutions are first compared by constraint violation (the ones which violate more are considered worse),
         * and only then by dominance on objectives.
         *
         * @param solutions the solutions to sort.
         * @param fronts the fronts to put solutions into.
         */
        public void assignFronts(List<S> solutions, List<List<S>> fronts) {
            fronts.clear();
            int nSolutions = solutions.size();
            if (nSolutions == 0) {
                return;
            }

            // We have at least one individual
            S first = solutions.get(0);
            int nObjectives = first.getNumberOfObjectives();
            boolean hasConstraintViolation = getConstraint(first) < 0;

            // Iterate over all individuals to check if all have the same number of objectives,
            // and to get whether we have meaningful constraints
            for (int i = 1; i < nSolutions; ++i) {
                S current = solutions.get(i);
                if (nObjectives != current.getNumberOfObjectives()) {
                    throw new IllegalArgumentException("Solutions have different numbers of objectives");
                }
                hasConstraintViolation |= getConstraint(current) < 0;
            }

            if (!hasConstraintViolation) {
                // Running directly on the input, no further work is necessary
                runSorting(solutions, 0, nSolutions, nObjectives, 0, fronts);
            } else {
                // Need to apply the constraint comparator first
                List<S> defensiveCopy = new ArrayList<>(solutions);
                defensiveCopy.sort(overallConstraintViolationComparator);
                int rankOffset = 0;
                int lastSpanStart = 0;
                double lastConstraint = getConstraint(defensiveCopy.get(0));
                for (int i = 1; i < nSolutions; ++i) {
                    double currConstraint = getConstraint(defensiveCopy.get(i));
                    if (lastConstraint != currConstraint) {
                        lastConstraint = currConstraint;
                        rankOffset = 1 + runSorting(defensiveCopy, lastSpanStart, i, nObjectives, rankOffset, fronts);
                        lastSpanStart = i;
                    }
                }
                runSorting(defensiveCopy, lastSpanStart, nSolutions, nObjectives, rankOffset, fronts);
            }
        }

        private int runSorting(List<S> solutions, int from, int until, int dimension, int rankOffset,
                               List<List<S>> fronts) {
            ensureEnoughSpace(until - from, dimension);
            double[][] points = new double[until - from][];
            int[] ranks = new int[until - from];
            for (int i = from; i < until; ++i) {
                points[i - from] = solutions.get(i).getObjectives();
            }
            sortingInstance.sort(points, ranks, until - from);
            int maxRank = 0;
            for (int i = from; i < until; ++i) {
                S current = solutions.get(i);
                int rank = ranks[i - from] + rankOffset;
                maxRank = Math.max(maxRank, rank);
                while (fronts.size() <= rank) {
                    fronts.add(new ArrayList<>());
                }
                fronts.get(rank).add(current);
            }
            return maxRank;
        }

        private void ensureEnoughSpace(int nPoints, int dimension) {
            if (sortingInstance == null
                    || sortingInstance.getMaximumPoints() < nPoints
                    || sortingInstance.getMaximumDimension() < dimension) {

                // This might be more intellectual.
                // For instance, for nPoints <= 10000 and dimension >= 7 one can instead use SetIntersectionSort aka MNDS.
                sortingInstance = JensenFortinBuzdalov
                        .getRedBlackTreeSweepHybridENSImplementation(1)
                        .getInstance(nPoints, dimension);
            }
        }

        private double getConstraint(S solution) {
            return ConstraintHandling.overallConstraintViolationDegree(solution);
        }
    }
}
