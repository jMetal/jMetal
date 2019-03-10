package org.uma.jmetal.auto.irace.mutation;

import org.uma.jmetal.auto.irace.Parameter;
import org.uma.jmetal.auto.irace.ParameterType;

import java.util.Collections;

public class MutationProbabilityParameter extends Parameter {
  public MutationProbabilityParameter() {
    super("mutationProbability",
        "--mutationProbability",
        ParameterType.r,
        "(0.0, 1.0)",
        "| crossover %in% c(\"Polynomial\", \"Uniform\")",
        Collections.emptyList()
        ) ;
  }
}
