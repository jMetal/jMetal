package org.uma.jmetal.operator.crossover.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/** Created by FlapKap on 23-03-2017. */
@SuppressWarnings("serial")
public class NPointCrossover<T> implements CrossoverOperator<Solution<T>> {
  private final JMetalRandom randomNumberGenerator = JMetalRandom.getInstance();
  private final double probability;
  private final int crossovers;

  public NPointCrossover(double probability, int crossovers) {
    if (probability < 0.0) throw new JMetalException("Probability can't be negative");
    if (crossovers < 1) throw new JMetalException("Number of crossovers is less than one");
    this.probability = probability;
    this.crossovers = crossovers;
  }

  public NPointCrossover(int crossovers) {
    this.crossovers = crossovers;
    this.probability = 1.0;
  }

  @Override
  public double getCrossoverProbability() {
    return probability;
  }

  @Override
  public List<Solution<T>> execute(List<Solution<T>> s) {
    Check.that(
        getNumberOfRequiredParents() == s.size(),
        "Point Crossover requires + "
            + getNumberOfRequiredParents()
            + " parents, but got "
            + s.size());

    if (randomNumberGenerator.nextDouble() < probability) {
      return doCrossover(s);
    } else {
      return s;
    }
  }

  private List<Solution<T>> doCrossover(List<Solution<T>> s) {
    Solution<T> mom = s.get(0);
    Solution<T> dad = s.get(1);

    Check.that(
        mom.variables().size() == dad.variables().size(),
        "The 2 parents doesn't have the same number of variables");
    Check.that(
        mom.variables().size() >= crossovers,
        "The number of crossovers is higher than the number of variables");

    int[] crossoverPoints = new int[crossovers];
    for (int i = 0; i < crossoverPoints.length; i++) {
      crossoverPoints[i] = randomNumberGenerator.nextInt(0, mom.variables().size() - 1);
    }
    Solution<T> girl = mom.copy();
    Solution<T> boy = dad.copy();
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
    List<Solution<T>> result = new ArrayList<>();
    result.add(girl);
    result.add(boy);
    return result;
  }

  @Override
  public int getNumberOfRequiredParents() {
    return 2;
  }

  @Override
  public int getNumberOfGeneratedChildren() {
    return 2;
  }
}
