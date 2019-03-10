package org.uma.jmetal.auto.irace.crossover;

import org.uma.jmetal.auto.irace.Parameter;
import org.uma.jmetal.auto.irace.ParameterType;

import java.util.Arrays;

public class CrossoverParameter extends Parameter {

  public CrossoverParameter() {
    super("crossover",
        "--crossover",
        ParameterType.c,
        "(SBX, BLX-alpha)",
        "",
        Arrays.asList(
            new CrossoverProbabilityParameter(),
            new BlxAlphaCrossoverAlphaParameter(),
            new SBXCrossoverDistributionIndexParameter())
    ) ;
  }
}
