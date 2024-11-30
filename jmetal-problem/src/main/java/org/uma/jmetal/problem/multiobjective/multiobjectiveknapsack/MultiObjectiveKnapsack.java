package org.uma.jmetal.problem.multiobjective.multiobjectiveknapsack;

import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Multi-objective Knapsack problem
 *
 * @author Antonio J. Nebro
 */
public class MultiObjectiveKnapsack extends AbstractBinaryProblem {
  private final List<int[]> profits;
  private final int[] weights;
  private final int capacity;

  public MultiObjectiveKnapsack(List<int[]> profits, int[] weights, int capacity) {
    Check.that(
        profits.stream().mapToInt(arr -> arr.length).distinct().count() == 1,
        "The profit vectors must have the same size");
    Check.that(
        profits.get(0).length == weights.length,
        "The number of profits ("
            + profits.get(0).length
            + ") must be equal to the number of weights ("
            + weights.length
            + ")");
    Check.that(capacity > 0, "The capacity (" + capacity + ") must be greater than zero");

    this.profits = profits;
    this.weights = weights;
    this.capacity = capacity;
  }

  @Override
  public int numberOfVariables() {
    return 1;
  }

  @Override
  public int numberOfObjectives() {
    return profits.size();
  }

  @Override
  public int numberOfConstraints() {
    return 1;
  }

  @Override
  public List<Integer> numberOfBitsPerVariable() {
    return List.of(weights.length);
  }

  @Override
  public String name() {
    return "Knapsack";
  }

  public int capacity() {
    return capacity;
  }

  public int[] weights() {
    return weights;
  }

  public List<int[]> profits() {
    return profits;
  }

  @Override
  public BinarySolution evaluate(BinarySolution solution) {
    int[] totalProfits = new int[profits.size()];
    int totalWeight = 0;

    BitSet bitSet = solution.variables().get(0);

    for (int i = 0; i < bitSet.length(); i++) {
      if (bitSet.get(i)) {
        totalWeight += weights[i];
        for (int j = 0; j < profits.size(); j++) {
          totalProfits[j] += profits.get(j)[i];
        }
      }
    }

    if (totalWeight > capacity) {
      solution.constraints()[0] = 1.0 * capacity - totalWeight;
    }

    IntStream.range(0, numberOfObjectives()).forEach(i -> solution.objectives()[i] = -totalProfits[i]);
    return solution;
  }
}
