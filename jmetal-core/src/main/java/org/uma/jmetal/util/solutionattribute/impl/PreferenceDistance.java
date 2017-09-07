package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.*;

public class PreferenceDistance <S extends Solution<?>>
        extends GenericSolutionAttribute<S, Double> implements DensityEstimator<S> {
    private List<Double> referencePoint;
    private List<Double> upperBounds = null;
    private List<Double> lowerBounds = null;

    public PreferenceDistance(List<Double> referencePoint, List<Double> upperBounds, List<Double> lowerBounds ){
        this.referencePoint = referencePoint;
        this.upperBounds = upperBounds;
        this.lowerBounds = lowerBounds;
    }
    @Override
    public void computeDensityEstimator(List<S> solutionList) {

        int size = solutionList.size();

        if (size == 0) {
            return;
        }

        if (size == 1) {
            solutionList.get(0).setAttribute(getAttributeIdentifier(), Double.POSITIVE_INFINITY);
            return;
        }

        if (size == 2) {
            solutionList.get(0).setAttribute(getAttributeIdentifier(), Double.POSITIVE_INFINITY);
            solutionList.get(1).setAttribute(getAttributeIdentifier(), Double.POSITIVE_INFINITY);

            return;
        }
        Vector<Double> weights = new Vector<Double>();
        int numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;
        for (int indexOfWeight = 0; indexOfWeight < numberOfObjectives; indexOfWeight++) {
            weights.add(new Double(1.0 / numberOfObjectives));
        }

        //we get preference distances for each solution and reference point
        //LinkedList<Node> distancesToReferencePoint = new LinkedList<RNSGAII.Node>();
        List<Double> distancesToReferencePoint = new ArrayList<>();

        // Use a new SolutionSet to avoid altering the original solutionSet
        List<S> front = new ArrayList<>(size);
        for (S solution : solutionList) {
            front.add(solution);
        }

        for (int i = 0; i < size; i++) {
            front.get(i).setAttribute(getAttributeIdentifier(), 0.0);
        }
        double objetiveMaxn;
        double objetiveMinn;
        double distance;

        // Sort the population by Obj n
        for (int i = 0; i < numberOfObjectives; i++) {
            // Sort the population by Obj n
            Collections.sort(front, new ObjectiveComparator<S>(i)) ;
            objetiveMinn = front.get(0).getObjective(i);
            objetiveMaxn = front.get(front.size() - 1).getObjective(i);

            // Set de crowding distance
            front.get(0).setAttribute(getAttributeIdentifier(), Double.POSITIVE_INFINITY);
            front.get(size - 1).setAttribute(getAttributeIdentifier(), Double.POSITIVE_INFINITY);

            for (int j = 1; j < size - 1; j++) {
                if (upperBounds!=null && lowerBounds!=null) {
                    distance = normalizedWeightedDistanceFromSolution(solutionList.get(j), lowerBounds, upperBounds, weights);
                } else {
                    distance = weightedDistanceFromSolution(solutionList.get(j), weights);
                }

                front.get(j).setAttribute(getAttributeIdentifier(), distance);
            }
        }
    }



    /**
     * Returns the distance between a solution and the reference point in
     * objective space.
     *
     *
     *            The first <code>Solution</code>.
     * @return the distance between a solution and the reference point in
     *         objective space.
     */
    public double normalizedWeightedDistanceFromSolution(Solution solution, List<Double> lowerBounds, List<Double> upperBounds,
                                                         Vector<Double> weights) {
        double normalizedDiff; // Auxiliar var
        double distance = 0.0;
        // -> Calculate the euclidean distance
        for (int nObj = 0; nObj < solution.getNumberOfObjectives(); nObj++) {
            normalizedDiff = (solution.getObjective(nObj) - (this.referencePoint.get(nObj)))
                    / (upperBounds.get(nObj) - lowerBounds.get(nObj));
            distance += weights.get(nObj) * Math.pow(normalizedDiff, 2.0);
        } // for

        // Return the euclidean distance
        return Math.sqrt(distance);
    } // distanceBetweenObjectives.

    /**
     * Returns the distance between a solution and the reference point in
     * objective space.
     *
     *            The first <code>Solution</code>.
     * @return the distance between a solution and the reference point in
     *         objective space.
     */
    public double weightedDistanceFromSolution(Solution solution, Vector<Double> weights) {
        double diff; // Auxiliar var
        double distance = 0.0;
        // -> Calculate the euclidean distance
        for (int nObj = 0; nObj < solution.getNumberOfObjectives(); nObj++) {
            diff = solution.getObjective(nObj) - this.referencePoint.get(nObj);
            distance += weights.get(nObj) * Math.pow(diff, 2.0);
        } // for

        // Return the euclidean distance
        return Math.sqrt(distance);
    } // distanceBetweenObjectives.
}
