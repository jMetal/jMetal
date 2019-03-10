package org.uma.jmetal.auto.irace.crossover;

import org.uma.jmetal.auto.irace.Parameter;
import org.uma.jmetal.auto.irace.ParameterType;

import java.util.Collections;

public class SBXCrossoverDistributionIndexParameter extends Parameter {
  public SBXCrossoverDistributionIndexParameter() {
    super(
     "sbxCrossoverDistributionIndex",
     "--crossoverSBXDistributionIndex",
        ParameterType.r,
        "(5.0, 400.0)",
        "| crossover %in% c(\"SBX\"\")",
        Collections.emptyList()
    ) ;
  }
}
