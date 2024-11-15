package org.uma.jmetal.operator.crossover.impl;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * Cycle Crossover (CX) operator for permutation-based genetic algorithms. This operator identifies
 * cycles between two parent permutations and exchanges them to produce offspring. It ensures that
 * each gene occupies the same position in the offspring as it did in one of the parents, preserving
 * positional information and maintaining valid permutations without duplicates or omissions of
 * genes.
 *
 * @author Nicolás R. Uribe
 */
public class CycleCrossover implements CrossoverOperator<PermutationSolution<Integer>> {
  private final double probability;
  private final JMetalRandom randomNumberGenerator = JMetalRandom.getInstance();

  public CycleCrossover(double probability) {
    Check.probabilityIsValid(probability);
    this.probability = probability;
  }

  @Override
  public List<PermutationSolution<Integer>> execute(List<PermutationSolution<Integer>> solutions) {
    // Ensure that exactly two parents are provided
    if (solutions.size() != 2) {
      throw new JMetalException("CycleCrossover requires exactly two parents.");
    }

    // Retrieve parents
    PermutationSolution<Integer> parent1 = solutions.get(0);
    PermutationSolution<Integer> parent2 = solutions.get(1);

    // Create offspring as copies of the parents
    List<PermutationSolution<Integer>> offspring = new ArrayList<>(2);
    offspring.add((PermutationSolution<Integer>) parent1.copy());
    offspring.add((PermutationSolution<Integer>) parent2.copy());

    // Perform crossover based on the given probability
    if (randomNumberGenerator.nextDouble() < probability) {
      int permutationLength = parent1.variables().size();

      // Initialize cycle lists with -1 indicating unassigned positions
      List<Integer> cycle1 = new ArrayList<>(permutationLength);
      List<Integer> cycle2 = new ArrayList<>(permutationLength);
      for (int i = 0; i < permutationLength; i++) {
        cycle1.add(-1);
        cycle2.add(-1);
      }

      // Choose a random starting index for the cycle
      int index = randomNumberGenerator.nextInt(0, permutationLength - 1);
      int start = index;

      // Find the first cycle
      do {
        cycle1.set(index, parent1.variables().get(index));
        // Find the next index based on the value from the other parent
        index = parent1.variables().indexOf(parent2.variables().get(index));
      } while (index != start);

      // Fill the rest of the elements from the other parent
      for (int i = 0; i < permutationLength; i++) {
        if (cycle1.get(i) == -1) {
          // If position is not in the cycle, copy from the other parent
          cycle1.set(i, parent2.variables().get(i));
          cycle2.set(i, parent1.variables().get(i));
        } else {
          // If position is in the cycle, complete with the value from the current parent
          cycle2.set(i, parent2.variables().get(i));
        }
      }

      // Set the offspring values
      for (int i = 0; i < permutationLength; i++) {
        offspring.get(0).variables().set(i, cycle1.get(i));
        offspring.get(1).variables().set(i, cycle2.get(i));
      }
    }

    return offspring;
  }

  @Override
  public double crossoverProbability() {
    return this.probability;
  }

  @Override
  public int numberOfRequiredParents() {
    return 2;
  }

  @Override
  public int numberOfGeneratedChildren() {
    return 2;
  }
}
