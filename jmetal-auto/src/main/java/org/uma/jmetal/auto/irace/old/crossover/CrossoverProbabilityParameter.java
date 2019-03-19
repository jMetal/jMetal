package org.uma.jmetal.auto.irace.old.crossover;

import org.uma.jmetal.auto.irace.old.Parameter;
import org.uma.jmetal.auto.irace.old.ParameterTypes;

import java.util.Collections;

public class CrossoverProbabilityParameter extends Parameter {
  public CrossoverProbabilityParameter() {
    super("crossoverProbability",
        "--crossoverProbability",
        ParameterTypes.r,
        "(0.0, 1.0)",
        "| crossover %in% c(\"SBXCrossoverParameter\", \"BLXCrossoverParameter-alpha\")",
        Collections.emptyList()
        ) ;
  }
}
