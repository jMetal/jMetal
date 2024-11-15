package org.uma.jmetal.operator.mutation.impl;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.Collections;

/**
 * Simple Inversion Mutation operator for permutation-based genetic algorithms.
 * This operator reverses the entire permutation with a given probability.
 * @author Nicolás R. Uribe
 */
public class SimpleInversionMutation<T> implements MutationOperator<PermutationSolution<T>> {
    private final double mutationProbability;
    private final JMetalRandom randomNumberGenerator = JMetalRandom.getInstance();

    public SimpleInversionMutation(double mutationProbability) {
        Check.probabilityIsValid(mutationProbability);
        this.mutationProbability = mutationProbability;
    }

    public double mutationProbability() {
        return this.mutationProbability;
    }

    @Override
    public PermutationSolution<T> execute(PermutationSolution<T> solution) {
        Check.notNull(solution);
        this.doMutation(solution);
        return solution;
    }

    public void doMutation(PermutationSolution<T> solution) {
        if (randomNumberGenerator.nextDouble() < this.mutationProbability) {
            // Reverse the entire permutation
            Collections.reverse(solution.variables());
        }
    }
}
