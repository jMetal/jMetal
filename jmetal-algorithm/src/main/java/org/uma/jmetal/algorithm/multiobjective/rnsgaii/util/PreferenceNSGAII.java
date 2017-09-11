package org.uma.jmetal.algorithm.multiobjective.rnsgaii.util;

import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

public class PreferenceNSGAII<S extends Solution<?>>  {
    private final List<Double> interestPoint;
    private List<Double> upperBounds = null;
    private List<Double> lowerBounds = null;
    private List<Double> weights = null;

    public PreferenceNSGAII(List<Double> weights, List<Double> interestPoint) {
        this.weights = weights;
        this.interestPoint = interestPoint;

    }


    public void updatePointOfInterest(List<Double> newInterestPoint) {
        if (this.interestPoint.size() != newInterestPoint.size()) {
            throw new JMetalException("Wrong dimension of the interest point vector");
        } else {
            for(int i = 0; i < newInterestPoint.size(); ++i) {
                this.interestPoint.set(i, newInterestPoint.get(i));
            }

        }
    }

    public Double evaluate(S solution) {


        List<Double> objectiveValues = new ArrayList(solution.getNumberOfObjectives());

            for(int i = 0; i < solution.getNumberOfObjectives(); ++i) {
                objectiveValues.add(solution.getObjective(i));
            }

            double distance  = 0.0D;
            double normalizeDiff = 0.0D;

            for (int i =0;i < solution.getNumberOfObjectives();i++){
                if(this.upperBounds!=null && this.lowerBounds!=null){
                    normalizeDiff = (solution.getObjective(i)-this.interestPoint.get(i))/
                            (this.upperBounds.get(i)-this.lowerBounds.get(i));
                }else{
                    normalizeDiff = solution.getObjective(i) - this.interestPoint.get(i);
                }
                distance += weights.get(i) * Math.pow(normalizeDiff,2.0D);
            }

            return Math.sqrt(distance);

    }

   public int getSize(){
        return this.weights.size();
   }

    public void setUpperBounds(List<Double> upperBounds) {
        this.upperBounds = upperBounds;
    }


    public void setLowerBounds(List<Double> lowerBounds) {
        this.lowerBounds = lowerBounds;
    }
}
