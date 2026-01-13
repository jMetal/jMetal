package org.uma.jmetal.operator.mutation.impl;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements a random mutation operator for integer solutions
 *
 * @author Jose Alejandro Cornejo-Acosta
 */
@SuppressWarnings("serial")
public class IntegerSimpleRandomMutation implements MutationOperator<IntegerSolution> {
    private double mutationProbability;
    private RandomGenerator<Double> randomGenerator;

    /**
     * Constructor
     */
    public IntegerSimpleRandomMutation(double probability) {
        this(probability, () -> JMetalRandom.getInstance().nextDouble());
    }

    /**
     * Constructor
     */
    public IntegerSimpleRandomMutation(double probability, RandomGenerator<Double> randomGenerator) {
        if (probability < 0) {
            throw new JMetalException("Mutation probability is negative: " + mutationProbability);
        }

        this.mutationProbability = probability;
        this.randomGenerator = randomGenerator;
    }

    /* Getters */
    @Override
    public double mutationProbability() {
        return mutationProbability;
    }

    /* Setters */
    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    /**
     * Execute() method
     */
    @Override
    public IntegerSolution execute(IntegerSolution solution) throws JMetalException {
        if (null == solution) {
            throw new JMetalException("Null parameter");
        }

        doMutation(mutationProbability, solution);

        return solution;
    }

    /**
     * Implements the mutation operation
     */
    private void doMutation(double probability, IntegerSolution solution) {
        for (int i = 0; i < solution.variables().size(); i++) {
            if (randomGenerator.getRandomValue() <= probability) {
                Bounds<Integer> bounds = solution.getBounds(i);
                Integer lowerBound = bounds.getLowerBound();
                Integer upperBound = bounds.getUpperBound();
                Double randomValue = randomGenerator.getRandomValue();
                Integer value = (int) (lowerBound + ((upperBound - lowerBound) * randomValue));

                solution.variables().set(i, value);
            }
        }
    }
}
