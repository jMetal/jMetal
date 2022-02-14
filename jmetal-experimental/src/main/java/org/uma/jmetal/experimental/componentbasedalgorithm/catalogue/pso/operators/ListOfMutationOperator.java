package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.operators;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.UniformMutation;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;


import java.util.ArrayList;

public class ListOfMutationOperator<S extends Solution<?>> implements MutationOperator<S>{
    private static final
    double DEFAULT_PROBABILITY = 0.01;
    private static final double DEFAULT_DISTRIBUTION_INDEX = 20.0;
    private double distributionIndex;
    private double mutationProbability;
    private RepairDoubleSolution solutionRepair;
    private ArrayList<MutationOperator> operators;
    protected JMetalRandom randomGenerator;

    @Override
    public S execute(S solution) throws JMetalException {
        Check.notNull(solution);

        doMutation(solution);
        return solution;
    }


    /** Constructor */
    public ListOfMutationOperator(ArrayList<MutationOperator> operators){
        this.operators = operators;
    }



    public double getMutationProbability() {
        return mutationProbability;
    }

    private S doMutation(S solution){
        double randomNumber = randomGenerator.nextDouble(0,1);
        MutationOperator op;
        if(0 <= randomNumber && randomNumber <= 0.3){
            op = operators.get(0);
            solution = (S) op.execute(solution);
        }else if(randomNumber < 0.3  && randomNumber <= 0.6){
            op = operators.get(1);
            solution = (S) op.execute(solution);
        }

        return solution;
    }


}
