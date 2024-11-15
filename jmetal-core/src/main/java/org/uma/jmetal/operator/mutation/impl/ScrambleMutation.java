package org.uma.jmetal.operator.mutation.impl;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Scramble Mutation operator for permutation-based genetic algorithms. This operator selects a
 * subsequence of the permutation and shuffles it.
 *
 * @author Nicolás R. Uribe
 */
public class ScrambleMutation<T> implements MutationOperator<PermutationSolution<T>> {
  private double mutationProbability;
  private final JMetalRandom randomNumberGenerator = JMetalRandom.getInstance();

  public ScrambleMutation(double mutationProbability) {
    Check.probabilityIsValid(mutationProbability);
    this.mutationProbability = mutationProbability;
  }

  public double mutationProbability() {
    return this.mutationProbability;
  }

  public void setMutationProbability(double mutationProbability) {
    Check.probabilityIsValid(mutationProbability);
    this.mutationProbability = mutationProbability;
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

      // Ensure that pos1 and pos2 are different
      while (pos1 == pos2) {
        pos2 = randomNumberGenerator.nextInt(0, permutationLength - 1);
      }

      // Swap positions if pos1 is greater than pos2 to ensure pos1 < pos2
      if (pos1 > pos2) {
        int temp = pos1;
        pos1 = pos2;
        pos2 = temp;
      }

      // Extract the subsequence to scramble
      List<T> sublist = new ArrayList<>(solution.variables().subList(pos1, pos2 + 1));

      // Shuffle the subsequence
      Collections.shuffle(sublist);

      // Replace the original subsequence with the scrambled one
      for (int i = pos1; i <= pos2; i++) {
        solution.variables().set(i, sublist.get(i - pos1));
      }
    }
  }
}
