package org.uma.jmetal.operator.crossover.impl;

import org.uma.jmetal.solution.binarysolution.BinarySolution;

/**
 * This class implements a single point crossover operator.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class SinglePointCrossover extends GenericSinglePointCrossover<BinarySolution> {
  public SinglePointCrossover(double crossoverProbability) {
    super(crossoverProbability);
  }
}

