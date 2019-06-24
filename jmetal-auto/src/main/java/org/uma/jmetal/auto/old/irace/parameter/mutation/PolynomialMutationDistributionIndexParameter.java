package org.uma.jmetal.auto.old.irace.parameter.mutation;

import org.uma.jmetal.auto.old.irace.parametertype.impl.RealParameterType;

public class PolynomialMutationDistributionIndexParameter extends RealParameterType {
  public PolynomialMutationDistributionIndexParameter(double lowerBound, double upperBound) {
    super("polynomialMutationDistributionIndex", lowerBound, upperBound) ;

    setParentTag(MutationType.polynomial.name());
  }
}
