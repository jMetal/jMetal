package org.uma.jmetal.util.solutionattribute.impl;

import java.util.List;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;

/**
 * created at 11:09 pm, 2019/1/28
 * Estimate DIR scores for solutions, used in D-NSGA-II
 * @author sunhaoran <nuaa_sunhr@yeah.net>
 */
@SuppressWarnings("serial")
public class DirScore<S extends Solution<?>>  extends GenericSolutionAttribute<S, Double> implements DensityEstimator<S> {

    private double[][] referenceVectors ;

    public DirScore(double[][] referenceVectors){
        this.referenceVectors = referenceVectors ;
    }

    @Override
    public void computeDensityEstimator(List<S> solutionSet) {
        var dirVector = computeDirVector(solutionSet) ;
        for(var i = 0; i < dirVector.length; i++){
            var solution = solutionSet.get(i) ;
            solution.attributes().put("dir-score", 1.0 / (double) dirVector[i]);
        }
    }

    private int[] computeDirVector(@NotNull List<S> solutionSet) {
        var dirVector = new int[solutionSet.size()] ;

        for(var vector : referenceVectors){
            var minIndex = 0;
            var minDistance = Double.MAX_VALUE;
            for(var i = 0; i < solutionSet.size(); i++){
                var solution = solutionSet.get(i) ;
                var distance = computeAngleDistance(vector, solution.objectives()) ;
                if(distance < minDistance){
                    minDistance = distance ;
                    minIndex = i ;
                }
            }
            dirVector[minIndex] = dirVector[minIndex] + 1 ;
        }

        return dirVector ;
    }

    private double computeAngleDistance(double[] vector, double[] objectives) {
        RealVector realVector = new ArrayRealVector(vector) ;
        RealVector objectVector = new ArrayRealVector(objectives) ;
        return 1.0 / realVector.cosine(objectVector);
    }
}
