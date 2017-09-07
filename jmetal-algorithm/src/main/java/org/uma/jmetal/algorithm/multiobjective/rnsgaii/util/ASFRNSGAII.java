package org.uma.jmetal.algorithm.multiobjective.rnsgaii.util;

import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

public class ASFRNSGAII <S extends Solution<?>> extends AbstractUtilityFunctionsSet<S> {
    private final List<Double> interestPoint;
    private List<Double> upperBounds = null;
    private List<Double> lowerBounds = null;


    public ASFRNSGAII(double[][] weights, List<Double> interestPoint) {
        super(weights);
        this.interestPoint = interestPoint;

    }

    public ASFRNSGAII(double[][] weights) {
        super(weights);
        this.interestPoint = new ArrayList(this.getVectorSize());

        for(int i = 0; i < this.getVectorSize(); ++i) {
            this.interestPoint.add(0.0D);
        }

    }

    public ASFRNSGAII(String file_path, List<Double> interestPoint) {
        super(file_path);
        this.interestPoint = interestPoint;
    }

    public ASFRNSGAII(String file_path) {
        super(file_path);
        this.interestPoint = new ArrayList(this.getVectorSize());

        for(int i = 0; i < this.getVectorSize(); ++i) {
            this.interestPoint.add(0.0D);
        }

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
    @Override
    public Double evaluate(S solution, int vector) {
        if (vector >= 0 && vector < this.getSize()) {
            List<Double> weightVector = this.getWeightVector(vector);
            List<Double> objectiveValues = new ArrayList(solution.getNumberOfObjectives());

            for(int i = 0; i < solution.getNumberOfObjectives(); ++i) {
                objectiveValues.add(solution.getObjective(i));
            }

            double distance  = 0.0D;
            double normalizeDiff = 0.0D;

            for (int i =0;i < solution.getNumberOfObjectives();i++){
                if(this.lowerBounds!=null && this.lowerBounds!=null){
                    normalizeDiff = (solution.getObjective(i)-this.interestPoint.get(i))/
                            (this.upperBounds.get(i)-this.lowerBounds.get(i));
                }else{
                    normalizeDiff = solution.getObjective(i) - this.interestPoint.get(i);
                }
                distance += weightVector.get(i) * Math.pow(normalizeDiff,2.0D);
            }

            return Math.sqrt(distance);
        } else {
            throw new JMetalException("Vector value " + vector + " invalid");
        }
    }



    public void setUpperBounds(List<Double> upperBounds) {
        this.upperBounds = upperBounds;
    }


    public void setLowerBounds(List<Double> lowerBounds) {
        this.lowerBounds = lowerBounds;
    }
}
