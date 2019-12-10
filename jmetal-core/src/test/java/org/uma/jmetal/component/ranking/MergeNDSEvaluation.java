package org.uma.jmetal.component.ranking;

import org.uma.jmetal.component.ranking.impl.MergeNonDominatedSortRanking;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl.IntegerValueAttributeComparator;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;
import org.uma.jmetal.util.point.PointSolution;

import ru.ifmo.nds.NonDominatedSorting;
import ru.ifmo.nds.SetIntersectionSort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * In this class I try to evaluate two implementations of the Merge Non-Dominated Sorting,
 * the one by Moreno ({@link org.uma.jmetal.component.ranking.impl.MergeNonDominatedSortRanking})
 * and the one by Buzdalov ({@link ru.ifmo.nds.SetIntersectionSort}).
 *
 * This shall be done using JMH, but as it is too expensive now to allocate a whole new module for it,
 * this is done the cheap way.
 *
 * @author Maxim Buzdalov
 */
public class MergeNDSEvaluation {
    public static void main(String[] args) {
        int n = Integer.parseInt(args[1]);
        int d = Integer.parseInt(args[2]);
        int count = Integer.parseInt(args[3]);
        List<List<PointSolution>> tests = generateRandomTests(n, d, count);

        switch (args[0]) {
            case "Moreno":
                runRanking(tests, new MergeNonDominatedSortRanking<>());
                break;
            case "Buzdalov":
                runRanking(tests, new BuzdalovSetIntersectionSortWrapper<>());
                break;
        }
    }

    private static List<List<PointSolution>> generateRandomTests(int n, int d, int count) {
        List<List<PointSolution>> rv = new ArrayList<>(count);
        Random r = new Random(n * 31 + d * 2342 + count);
        for (int i = 0; i < count; ++i) {
            List<PointSolution> instance = new ArrayList<>(n);
            for (int j = 0; j < n; ++j) {
                PointSolution s = new PointSolution(d);
                for (int k = 0; k < d; ++k) {
                    s.setObjective(k, r.nextDouble());
                }
                instance.add(s);
            }
            rv.add(instance);
        }
        return rv;
    }

    private static void runRanking(List<List<PointSolution>> solutions, Ranking<PointSolution> ranking) {
        int nMeasurements = 0;
        long t0 = System.nanoTime();
        long tenSeconds = 10000000000L;
        while (nMeasurements < 5 || System.nanoTime() - t0 < tenSeconds) {
            long start = System.nanoTime();
            for (List<PointSolution> instance : solutions) {
                ranking.computeRanking(instance);
            }
            long time = System.nanoTime() - start;
            ++nMeasurements;
            System.out.println("Iteration " + nMeasurements + ": " + (time / 1e9 / solutions.size()) + " seconds per instance");
        }
    }

    private static class BuzdalovSetIntersectionSortWrapper<S extends Solution<?>> implements Ranking<S> {
        private String attributeId = getClass().getName() ;
        private Comparator<S> solutionComparator ;

        public BuzdalovSetIntersectionSortWrapper() {
            this.solutionComparator =
                    new IntegerValueAttributeComparator<>(attributeId, AttributeComparator.Ordering.ASCENDING);
        }

        // Interface support: the place to store the fronts.
        private final List<List<S>> subFronts = new ArrayList<>();

        // Constraint violation checking support.
        private final OverallConstraintViolationComparator<S> overallConstraintViolationComparator
                = new OverallConstraintViolationComparator<>();

        // Delegation.
        private NonDominatedSorting sortingInstance = null;

        @Override
        public Ranking<S> computeRanking(List<S> solutionList) {
            subFronts.clear();
            int nSolutions = solutionList.size();
            if (nSolutions == 0) {
                return this;
            }

            // We have at least one individual
            S first = solutionList.get(0);
            int nObjectives = first.getNumberOfObjectives();
            boolean hasConstraintViolation = getConstraint(first) < 0;

            // Iterate over all individuals to check if all have the same number of objectives,
            // and to get whether we have meaningful constraints
            for (int i = 1; i < nSolutions; ++i) {
                S current = solutionList.get(i);
                if (nObjectives != current.getNumberOfObjectives()) {
                    throw new IllegalArgumentException("Solutions have different numbers of objectives");
                }
                hasConstraintViolation |= getConstraint(current) < 0;
            }

            if (!hasConstraintViolation) {
                // Running directly on the input, no further work is necessary
                runSorting(solutionList, 0, nSolutions, nObjectives, 0);
            } else {
                // Need to apply the constraint comparator first
                List<S> defensiveCopy = new ArrayList<>(solutionList);
                defensiveCopy.sort(overallConstraintViolationComparator);
                int rankOffset = 0;
                int lastSpanStart = 0;
                double lastConstraint = getConstraint(defensiveCopy.get(0));
                for (int i = 1; i < nSolutions; ++i) {
                    double currConstraint = getConstraint(defensiveCopy.get(i));
                    if (lastConstraint != currConstraint) {
                        lastConstraint = currConstraint;
                        rankOffset = 1 + runSorting(defensiveCopy, lastSpanStart, i, nObjectives, rankOffset);
                        lastSpanStart = i;
                    }
                }
                runSorting(defensiveCopy, lastSpanStart, nSolutions, nObjectives, rankOffset);
            }

            return this;
        }

        private int runSorting(List<S> solutions, int from, int until, int dimension, int rankOffset) {
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
                current.setAttribute(attributeId, rank);
                while (subFronts.size() <= rank) {
                    subFronts.add(new ArrayList<>());
                }
                subFronts.get(rank).add(current);
            }
            return maxRank;
        }

        private void ensureEnoughSpace(int nPoints, int dimension) {
            if (sortingInstance == null
                    || sortingInstance.getMaximumPoints() < nPoints
                    || sortingInstance.getMaximumDimension() < dimension) {

                sortingInstance = SetIntersectionSort.getBitSetInstance().getInstance(nPoints, dimension);
            }
        }

        private double getConstraint(S solution) {
            return ConstraintHandling.overallConstraintViolationDegree(solution);
        }

        @Override
        public List<S> getSubFront(int rank) {
            return subFronts.get(rank);
        }

        @Override
        public int getNumberOfSubFronts() {
            return subFronts.size();
        }

        @Override
        public Comparator<S> getSolutionComparator() {
            return solutionComparator;
        }

        @Override
        public String getAttributeId() {
            return attributeId ;
        }
    }
}
