package org.uma.jmetal.qualityindicator.calculator;

import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.qualityindicator.util.MetricsUtil;

/**
 * This is an abstract class for the quality indicators calculators.
 * The idea is to add all available Pareto fronts into an <b>internal population</b>,
 * in order to perform an exact normalization. This internal population is used for
 * assessing the max and min values for the normalization. It is useful when you don't know
 * what are these values and must use the max known and min known values.
 * <br/>
 * <br/>
 * If the intention is to compare the fronts A, B and C, then the addParetoFront() method
 * must be invoked three times giving A, B and then C as input.
 */
public abstract class Calculator {
  protected SolutionSet internalPopulation;
  protected final MetricsUtil metricsUtil;

  public Calculator() {
    internalPopulation = new SolutionSet();
    metricsUtil = new MetricsUtil();
  }

  /**
   * Adds a Pareto front in the internal population.
   *
   * @param front the solution set to be added into the internal population.
   */
  public void addParetoFront(SolutionSet front) {
    internalPopulation = internalPopulation.union(front);
  }

  /**
   * Adds a Pareto front in the internal population.
   *
   * @param frontPath the path for the solution set to be added into the internal population.
   */
  public void addParetoFront(String frontPath) {
    addParetoFront(metricsUtil.readNonDominatedSolutionSet(frontPath));
  }

  /**
   * Clears the internal population;
   */
  public void clear() {
    this.internalPopulation = new SolutionSet();
  }

  public abstract double execute(String frontPath);

  public abstract double execute(SolutionSet front);
    
}
