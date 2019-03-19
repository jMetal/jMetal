package org.uma.jmetal.auto.irace.old.crossover;

import org.uma.jmetal.auto.irace.old.Parameter;
import org.uma.jmetal.auto.irace.old.ParameterTypes;

import java.util.Collections;

public class SBXCrossoverDistributionIndexParameter extends Parameter {
  public SBXCrossoverDistributionIndexParameter() {
    super(
     "sbxCrossoverDistributionIndex",
     "--crossoverSBXDistributionIndex",
        ParameterTypes.r,
        "(5.0, 400.0)",
        "| crossover %in% c(\"SBXCrossoverParameter\"\")",
        Collections.emptyList()
    ) ;
  }
}
