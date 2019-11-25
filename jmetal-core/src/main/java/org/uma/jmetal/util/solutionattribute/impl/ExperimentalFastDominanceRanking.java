package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import ru.ifmo.nds.JensenFortinBuzdalov;
import ru.ifmo.nds.NonDominatedSorting;

import java.util.ArrayList;
import java.util.List;

public class ExperimentalFastDominanceRanking<S extends Solution<?>>
        extends GenericSolutionAttribute<S, Integer> implements Ranking<S> {
    private final List<List<S>> subFronts = new ArrayList<>();
    private final OverallConstraintViolation<S> overallConstraintViolation = new OverallConstraintViolation<>();

    private NonDominatedSorting sortingInstance = null;
    private double[][] points = null;
    private int[] ranks = null;

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
        Double firstConstraint = overallConstraintViolation.getAttribute(first);
        boolean hasConstraintViolation = firstConstraint != null && firstConstraint < 0;

        // Iterate over all individuals to check if all have the same number of objectives,
        // and to get whether we have meaningful constraints
        for (int i = 1; i < nSolutions; ++i) {
            S current = solutionList.get(i);
            if (nObjectives != current.getNumberOfObjectives()) {
                throw new IllegalArgumentException("Solutions have different numbers of objectives");
            }
            Double currentConstraint = overallConstraintViolation.getAttribute(current);
            hasConstraintViolation |= currentConstraint != null && currentConstraint < 0;
        }

        // Make sure our underlying sorting can handle this many points and objectives
        int nEffectiveObjectives = nObjectives + (hasConstraintViolation ? 1 : 0);
        ensureEnoughSpace(nSolutions, nEffectiveObjectives);

        // Populate our points with the contents of solutions
        for (int i = 0; i < nSolutions; ++i) {
            S currentIndividual = solutionList.get(i);
            double[] point = points[i];
            for (int j = 0; j < nObjectives; ++j) {
                point[j] = currentIndividual.getObjective(j);
            }
            if (hasConstraintViolation) {
                point[nObjectives] = -overallConstraintViolation.getAttribute(currentIndividual);
            }
        }

        // Run sorting
        sortingInstance.sort(points, ranks, nSolutions);

        // Assign ranks
        for (int i = 0; i < nSolutions; ++i) {
            S current = solutionList.get(i);
            int rank = ranks[i];
            setAttribute(current, rank);
            while (subFronts.size() <= rank) {
                subFronts.add(new ArrayList<>());
            }
            subFronts.get(rank).add(current);
        }

        return this;
    }

    private void ensureEnoughSpace(int nPoints, int dimension) {
        if (sortingInstance == null
                || sortingInstance.getMaximumPoints() < nPoints
                || sortingInstance.getMaximumDimension() != dimension) { // "!=" is intentional though unfortunate

            // This might be more intellectual.
            // For instance, for nPoints <= 10000 and dimension >= 7 one can instead use SetIntersectionSort aka MNDS.
            sortingInstance = JensenFortinBuzdalov
                    .getRedBlackTreeSweepHybridENSImplementation(1)
                    .getInstance(nPoints, dimension);
            points = new double[nPoints][dimension];
            ranks = new int[nPoints];
        }
    }

    @Override
    public List<S> getSubfront(int rank) {
        return subFronts.get(rank);
    }

    @Override
    public int getNumberOfSubfronts() {
        return subFronts.size();
    }
}
