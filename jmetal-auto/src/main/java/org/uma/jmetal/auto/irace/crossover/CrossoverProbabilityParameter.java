package org.uma.jmetal.auto.irace.crossover;

import org.uma.jmetal.auto.irace.Parameter;
import org.uma.jmetal.auto.irace.ParameterType;

import java.util.Collections;

public class CrossoverProbabilityParameter extends Parameter {
  public CrossoverProbabilityParameter() {
    super("crossoverProbability",
        "--crossoverProbability",
        ParameterType.r,
        "(0.0, 1.0)",
        "| crossover %in% c(\"SBX\", \"BLX-alpha\")",
        Collections.emptyList()
        ) ;
  }
}
