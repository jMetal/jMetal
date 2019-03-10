package org.uma.jmetal.auto.irace.crossover;

import org.uma.jmetal.auto.irace.Parameter;
import org.uma.jmetal.auto.irace.ParameterType;

import java.util.Collections;

public class BlxAlphaCrossoverAlphaParameter extends Parameter {
  public BlxAlphaCrossoverAlphaParameter() {
    super("blxAlphaCrossoverAlpha",
        "--crossoverBlxAlpha",
        ParameterType.r,
        "(0.1, 1.0)",
        "| crossover %in% c(\"BLX-alpha\"\")",
        Collections.emptyList()
    );
  }
}
