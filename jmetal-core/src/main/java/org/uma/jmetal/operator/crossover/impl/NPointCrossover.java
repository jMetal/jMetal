package org.uma.jmetal.operator.crossover.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/** Created by FlapKap on 23-03-2017. */
@SuppressWarnings("serial")
public class NPointCrossover<T extends Solution<S>, S extends Number> implements CrossoverOperator<T> {
  private final JMetalRandom randomNumberGenerator = JMetalRandom.getInstance();
  private final double probability;
  private final int numberOfCuttingPoints;

  public NPointCrossover(double probability, int numberOfCuttingPoints) {
    Check.probabilityIsValid(probability);
    Check.that(numberOfCuttingPoints >=  1, "Number of crossovers is less than one");
    this.probability = probability;
    this.numberOfCuttingPoints = numberOfCuttingPoints;
  }

  public NPointCrossover(int crossovers) {
    this.numberOfCuttingPoints = crossovers;
    this.probability = 1.0;
  }

  @Override
  public double crossoverProbability() {
    return probability;
  }

  @Override
  public List<T> execute(List<T> solutions) {
    Check.that(
        numberOfRequiredParents() == solutions.size(),
        "Point Crossover requires + "
            + numberOfRequiredParents()
            + " parents, but got "
            + solutions.size());

    if (randomNumberGenerator.nextDouble() < probability) {
      return doCrossover(solutions);
    } else {
      return solutions;
    }
  }

  private List<T> doCrossover(List<T> s) {
    T mom = s.get(0);
    T dad = s.get(1);

    Check.that(
        mom.variables().size() == dad.variables().size(),
        "The 2 parents doesn't have the same number of variables");
    Check.that(
        mom.variables().size() >= numberOfCuttingPoints,
        "The number of crossovers is higher than the number of variables");

    int[] crossoverPoints = new int[numberOfCuttingPoints];
    for (int i = 0; i < crossoverPoints.length; i++) {
      crossoverPoints[i] = randomNumberGenerator.nextInt(0, mom.variables().size() - 1);
    }
    T girl = (T) mom.copy();
    T boy = (T) dad.copy();
    boolean swap = false;

    for (int i = 0; i < mom.variables().size(); i++) {
      if (swap) {
        boy.variables().set(i, mom.variables().get(i));
        girl.variables().set(i, dad.variables().get(i));
      }

      if (ArrayUtils.contains(crossoverPoints, i)) {
        swap = !swap;
      }
    }
    List<T> result = new ArrayList<>();
    result.add(girl);
    result.add(boy);
    return result;
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
