package org.uma.jmetal.auto.irace.old.mutation;

import org.uma.jmetal.auto.irace.old.Parameter;
import org.uma.jmetal.auto.irace.old.ParameterTypes;

import java.util.Collections;

public class MutationProbabilityParameter extends Parameter {
  public MutationProbabilityParameter() {
    super("mutationProbability",
        "--mutationProbability",
        ParameterTypes.r,
        "(0.0, 1.0)",
        "| crossover %in% c(\"Polynomial\", \"Uniform\")",
        Collections.emptyList()
        ) ;
  }
}
