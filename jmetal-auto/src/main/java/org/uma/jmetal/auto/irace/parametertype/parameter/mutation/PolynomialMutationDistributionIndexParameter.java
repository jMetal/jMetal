package org.uma.jmetal.auto.irace.parametertype.parameter.mutation;

import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class PolynomialMutationDistributionIndexParameter extends RealParameterType {
  public PolynomialMutationDistributionIndexParameter(double lowerBound, double upperBound) {
    super("polynomialMutationDistributionIndex", lowerBound, upperBound) ;

    setParentTag("polynomial");
  }
}
