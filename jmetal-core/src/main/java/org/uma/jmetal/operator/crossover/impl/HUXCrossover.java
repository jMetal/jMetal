package org.uma.jmetal.operator.crossover.impl;

import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class allows to apply a HUX crossover operator using two parent solutions. NOTE: the
 * operator is applied to the first encoding.variable of the solutions, and the type of the
 * solutions must be Binary
 *
 * @author Antonio J. Nebro
 * @author Juan J. Durillo
 * @version 1.0
 */
@SuppressWarnings("serial")
public class HUXCrossover extends GenericUniformCrossover<BinarySolution> {
  public HUXCrossover(double crossoverProbability) {
    super(crossoverProbability);
  }

  public HUXCrossover(double crossoverProbability,
      RandomGenerator<Double> crossoverRandomGenerator) {
    super(crossoverProbability, crossoverRandomGenerator);
  }
}
