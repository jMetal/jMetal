package org.uma.jmetal.operator.crossover.impl;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Order-based Crossover with Duplicate Elimination (OXD) operator for permutation-based genetic
 * algorithms. This operator combines elements of the standard Order Crossover (OX) and handles
 * duplicates to ensure valid permutations. It selects a subsequence from one parent and fills the
 * remaining positions from the other parent, maintaining the order and avoiding duplicate genes to
 * produce valid offspring permutations.
 *
 * @author Nicolás R. Uribe
 */
public class OXDCrossover implements CrossoverOperator<PermutationSolution<Integer>> {
  private final double probability;
  private final JMetalRandom randomNumberGenerator = JMetalRandom.getInstance();

  // Constructor to initialize crossover probability
  public OXDCrossover(double probability) {
    Check.probabilityIsValid(probability);
    this.probability = probability;
  }

  @Override
  public List<PermutationSolution<Integer>> execute(List<PermutationSolution<Integer>> solutions) {
    // Validate input - solutions must not be null or empty
    Check.notNull(solutions);
    Check.collectionIsNotEmpty(solutions);

    if (solutions.size() != 2) {
      throw new JMetalException("OXDCrossover requires exactly two parents.");
    }

    PermutationSolution<Integer> parent1 = solutions.get(0);
    PermutationSolution<Integer> parent2 = solutions.get(1);

    // Validate parents - must not be null and must have valid permutation length
    Check.notNull(parent1);
    Check.notNull(parent2);

    int permutationLength = parent1.variables().size();

    // Create offspring by copying the parents
    List<PermutationSolution<Integer>> offspring = new ArrayList<>(2);
    offspring.add((PermutationSolution<Integer>) parent1.copy());
    offspring.add((PermutationSolution<Integer>) parent2.copy());

    // Perform crossover if within probability
    if (randomNumberGenerator.nextDouble() < probability) {
      // Select two cutting points
      int cuttingPoint1 = randomNumberGenerator.nextInt(0, permutationLength - 2);
      int cuttingPoint2 = randomNumberGenerator.nextInt(cuttingPoint1 + 1, permutationLength - 1);

      // Swap segments between the two parents to create the initial offspring
      for (int i = cuttingPoint1; i <= cuttingPoint2; i++) {
        offspring.get(0).variables().set(i, parent2.variables().get(i));
        offspring.get(1).variables().set(i, parent1.variables().get(i));
      }

      // Repair offspring to ensure valid permutations are produced
      repairOffspring(offspring.get(0), parent1, parent2, cuttingPoint1, cuttingPoint2);
      repairOffspring(offspring.get(1), parent2, parent1, cuttingPoint1, cuttingPoint2);
    }

    return offspring;
  }

  // Repair offspring to ensure that it contains a valid permutation without duplicates
  private void repairOffspring(
      PermutationSolution<Integer> offspring,
      PermutationSolution<Integer> parent,
      PermutationSolution<Integer> otherParent,
      int cuttingPoint1,
      int cuttingPoint2) {
    int permutationLength = offspring.variables().size();

    // Create a set of genes that are already in the offspring segment (between cuttingPoint1 and
    // cuttingPoint2)
    Set<Integer> genesInSegment =
        new HashSet<>(offspring.variables().subList(cuttingPoint1, cuttingPoint2 + 1));

    // Start from the position after cuttingPoint2 and continue wrapping around until cuttingPoint1
    // is reached
    int currentIndex = (cuttingPoint2 + 1) % permutationLength;
    int otherParentIndex = (cuttingPoint2 + 1) % permutationLength;

    // Fill in the remaining elements from the other parent, skipping duplicates
    while (currentIndex != cuttingPoint1) {
      int gene = otherParent.variables().get(otherParentIndex);

      // Only add the gene if it's not already in the offspring segment
      if (!genesInSegment.contains(gene)) {
        offspring.variables().set(currentIndex, gene);
        currentIndex = (currentIndex + 1) % permutationLength;
      }

      // Move to the next element in the other parent, wrapping around if necessary
      otherParentIndex = (otherParentIndex + 1) % permutationLength;
    }
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

  // Helper method to check if a list represents a valid permutation (no duplicate elements)
  private boolean isValidPermutation(List<Integer> permutation) {
    Set<Integer> uniqueElements = new HashSet<>(permutation);
    return uniqueElements.size() == permutation.size();
  }
}
