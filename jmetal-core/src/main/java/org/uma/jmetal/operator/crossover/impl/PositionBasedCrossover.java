package org.uma.jmetal.operator.crossover.impl;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.*;

/**
 * Position-Based Crossover operator for permutation-based genetic algorithms. This operator selects
 * random positions to inherit genes from one parent. The remaining genes are filled from the other
 * parent in the order they appear, ensuring a valid permutation without duplicates.
 *
 * @author Nicolás R. Uribe
 */
public class PositionBasedCrossover implements CrossoverOperator<PermutationSolution<Integer>> {
  private final double probability;
  private final JMetalRandom randomNumberGenerator = JMetalRandom.getInstance();

  public PositionBasedCrossover(double probability) {
    Check.probabilityIsValid(probability);
    this.probability = probability;
  }

  @Override
  public List<PermutationSolution<Integer>> execute(List<PermutationSolution<Integer>> solutions) {
    if (solutions.size() != 2) {
      throw new JMetalException("PositionBasedCrossover requires exactly two parents.");
    }

    PermutationSolution<Integer> parent1 = solutions.get(0);
    PermutationSolution<Integer> parent2 = solutions.get(1);

    List<PermutationSolution<Integer>> offspring = new ArrayList<>(2);
    offspring.add((PermutationSolution<Integer>) parent1.copy());
    offspring.add((PermutationSolution<Integer>) parent2.copy());

    if (randomNumberGenerator.nextDouble() < probability) {
      List<Integer> child1 = positionBasedCrossover(parent1.variables(), parent2.variables());
      List<Integer> child2 = positionBasedCrossover(parent2.variables(), parent1.variables());

      for (int i = 0; i < child1.size(); i++) {
        offspring.get(0).variables().set(i, child1.get(i));
        offspring.get(1).variables().set(i, child2.get(i));
      }
    }

    return offspring;
  }

  private List<Integer> positionBasedCrossover(List<Integer> parent1, List<Integer> parent2) {
    int size = parent1.size();
    List<Integer> child = new ArrayList<>(Collections.nCopies(size, null));

    // We select positions at random to keep elements in parent1
    Set<Integer> selectedPositions = new HashSet<>();
    int numberOfPositions = randomNumberGenerator.nextInt(1, size / 2);

    while (selectedPositions.size() < numberOfPositions) {
      selectedPositions.add(randomNumberGenerator.nextInt(0, size - 1));
    }

    // We place the elements of parent1 in the selected positions in the child.
    for (Integer pos : selectedPositions) {
      child.set(pos, parent1.get(pos));
    }

    // Fill the rest of the child with the elements of parent2 in the order they appear.
    int childIndex = 0;
    for (Integer element : parent2) {
      if (!child.contains(element)) {
        // Find the first null position and place the element of parent2 there.
        while (child.get(childIndex) != null) {
          childIndex++;
        }
        child.set(childIndex, element);
      }
    }

    return child;
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
