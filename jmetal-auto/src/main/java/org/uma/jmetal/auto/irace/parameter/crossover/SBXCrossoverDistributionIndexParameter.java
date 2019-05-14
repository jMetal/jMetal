package org.uma.jmetal.auto.irace.parameter.crossover;

import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class SBXCrossoverDistributionIndexParameter extends RealParameterType {
  public SBXCrossoverDistributionIndexParameter(double lowerBound, double upperBound) {
    super("sbxCrossoverDistributionIndex", lowerBound, upperBound) ;

    setParentTag(CrossoverType.SBX.name());
  }
}
