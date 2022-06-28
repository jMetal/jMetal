package org.uma.jmetal.operator.crossover.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements a uniform crosoover operator for binary solutions.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class UniformCrossover extends GenericUniformCrossover<BinarySolution> {

  public UniformCrossover(double crossoverProbability) {
    super(crossoverProbability);
  }

  public UniformCrossover(double crossoverProbability,
      RandomGenerator<Double> crossoverRandomGenerator) {
    super(crossoverProbability, crossoverRandomGenerator);
  }
}
