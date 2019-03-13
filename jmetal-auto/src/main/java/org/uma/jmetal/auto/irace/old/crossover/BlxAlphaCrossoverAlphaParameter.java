package org.uma.jmetal.auto.irace.old.crossover;

import org.uma.jmetal.auto.irace.old.Parameter;
import org.uma.jmetal.auto.irace.old.ParameterTypes;

import java.util.Collections;

public class BlxAlphaCrossoverAlphaParameter extends Parameter {
  public BlxAlphaCrossoverAlphaParameter() {
    super("blxAlphaCrossoverAlpha",
        "--crossoverBlxAlpha",
        ParameterTypes.r,
        "(0.1, 1.0)",
        "| crossover %in% c(\"BLX-alpha\"\")",
        Collections.emptyList()
    );
  }
}
