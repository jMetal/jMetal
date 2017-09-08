package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.util.distance.EuclideanDistance;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.*;

public class PreferenceDistance <S extends Solution<?>>
        extends GenericSolutionAttribute<S, Double> implements DensityEstimator<S> {
    private Point referencePoint;
    EuclideanDistance euclideanDistance;
    public PreferenceDistance(Point referencePoint){
        this.referencePoint = referencePoint;
        euclideanDistance = new EuclideanDistance();
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

        int numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;

        // Use a new SolutionSet to avoid altering the original solutionSet
        List<S> front = new ArrayList<>(size);
        for (S solution : solutionList) {
            front.add(solution);
        }

        for (int i = 0; i < size; i++) {
            front.get(i).setAttribute(getAttributeIdentifier(), 0.0);
        }
        double distance;

        // Sort the population by Obj n
        for (int i = 0; i < numberOfObjectives; i++) {

            // Set de euclidean distance
            front.get(0).setAttribute(getAttributeIdentifier(), Double.POSITIVE_INFINITY);
            front.get(size - 1).setAttribute(getAttributeIdentifier(), Double.POSITIVE_INFINITY);

            for (int j = 1; j < size - 1; j++) {
                Point p = getPointFromSolution(solutionList.get(j));
                distance = euclideanDistance.compute(p,referencePoint);
                front.get(j).setAttribute(getAttributeIdentifier(), distance);
            }
        }
    }


 private Point getPointFromSolution(Solution solution){
        Point result = null;
        if(solution!=null){
            result = new ArrayPoint(solution.getNumberOfObjectives());
            for (int i = 0; i < solution.getNumberOfObjectives() ; i++) {
                result.setDimensionValue(i,solution.getObjective(i));
            }
        }
        return result;
 }
}
