package org.uma.jmetal.auto.irace.old.crossover;

import org.uma.jmetal.auto.irace.old.Parameter;
import org.uma.jmetal.auto.irace.old.ParameterTypes;

import java.util.Arrays;

public class CrossoverParameter extends Parameter {

  public CrossoverParameter() {
    super("crossover",
        "--crossover",
        ParameterTypes.c,
        "(SBXCrossoverDistributionIndexParameter, BLXAlphaCrossoverAlphaValueParameter-alpha)",
        "",
        Arrays.asList(
            new CrossoverProbabilityParameter(),
            new BlxAlphaCrossoverAlphaParameter(),
            new SBXCrossoverDistributionIndexParameter())
    ) ;
  }
}
