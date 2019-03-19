package org.uma.jmetal.auto.irace.parametertype.parameter.crossover;

import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class SBXCrossoverParameter extends RealParameterType {
  public SBXCrossoverParameter(double lowerBound, double upperBound) {
    super("sbxCrossoverDistributionIndex", lowerBound, upperBound) ;

    setParentTag("SBX");
  }
}
