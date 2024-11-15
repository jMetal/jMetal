package org.uma.jmetal.operator.mutation.impl;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * Displacement Mutation operator for permutation-based genetic algorithms. This operator selects a
 * subsequence of the permutation and inserts it into a new random position.
 *
 * @author Nicolás R. Uribe
 */
public class DisplacementMutation<T> implements MutationOperator<PermutationSolution<T>> {
  private final double mutationProbability;
  private final JMetalRandom randomNumberGenerator = JMetalRandom.getInstance();

  public DisplacementMutation(double mutationProbability) {
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
    int permutationLength = solution.variables().size();

    // Proceed only if the permutation length is greater than 1 and mutation should be applied
    if (permutationLength > 1 && randomNumberGenerator.nextDouble() < this.mutationProbability) {

      // Select two distinct positions in the permutation
      int pos1 = randomNumberGenerator.nextInt(0, permutationLength - 1);
      int pos2 = randomNumberGenerator.nextInt(0, permutationLength - 1);

      // Ensure that pos1 and pos2 are not the same
      while (pos1 == pos2) {
        pos2 = randomNumberGenerator.nextInt(0, permutationLength - 1);
      }

      // Swap positions if pos1 is greater than pos2 to ensure pos1 < pos2
      if (pos1 > pos2) {
        int temp = pos1;
        pos1 = pos2;
        pos2 = temp;
      }

      // Extract the subsequence to be displaced (from pos1 to pos2 inclusive)
      List<T> sublist = new ArrayList<>(solution.variables().subList(pos1, pos2 + 1));

      // Remove the subsequence from its original position
      solution.variables().subList(pos1, pos2 + 1).clear();

      // Select a new position to insert the subsequence
      int newPos = randomNumberGenerator.nextInt(0, permutationLength - sublist.size());

      // Insert the subsequence at the new position
      solution.variables().addAll(newPos, sublist);
    }
  }
}
