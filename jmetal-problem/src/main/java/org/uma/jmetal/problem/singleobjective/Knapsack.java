package org.uma.jmetal.problem.singleobjective;

import java.util.BitSet;
import java.util.List;
import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.errorchecking.Check;

public class Knapsack extends AbstractBinaryProblem {
  private final int[] profits;
  private final int[] weights;
  private final int capacity;

  public Knapsack(int[] profits, int[] weights, int capacity) {
    Check.that(
        profits.length == weights.length,
        "The number of profits ("
            + profits.length
            + ") must be equal to the number of weights ("
            + weights.length
            + ")");
    Check.that(capacity > 0, "The capacity ("+ capacity+") must be greater than zero") ;

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
    return 1;
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

  public int[] profits() {
    return profits;
  }

  @Override
  public BinarySolution evaluate(BinarySolution solution) {
    int totalProfit = 0;
    int totalWeight = 0;

    BitSet bitSet = solution.variables().get(0);

    for (int i = 0; i < bitSet.length(); i++) {
      if (bitSet.get(i)) {
        totalProfit += profits[i];
        totalWeight += weights[i];
      }
    }

    if (totalWeight > capacity) {
      // totalProfit = 0; // Penalize if the solution is not feasible
      solution.constraints()[0] = capacity - totalWeight;
    }

    solution.objectives()[0] = -totalProfit;

    return solution;
  }
}
