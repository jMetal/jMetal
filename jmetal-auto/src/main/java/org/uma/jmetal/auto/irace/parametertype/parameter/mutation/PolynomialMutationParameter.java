package org.uma.jmetal.auto.irace.parametertype.parameter.mutation;

import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class PolynomialMutationParameter extends RealParameterType {
  public PolynomialMutationParameter(double lowerBound, double upperBound) {
    super("polynomialMutationDistributionIndex", lowerBound, upperBound) ;

    setParentTag("polynomial");
  }
}
