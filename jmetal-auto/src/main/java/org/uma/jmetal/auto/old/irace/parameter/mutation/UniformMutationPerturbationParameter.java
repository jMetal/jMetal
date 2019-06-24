package org.uma.jmetal.auto.old.irace.parameter.mutation;

import org.uma.jmetal.auto.old.irace.parametertype.impl.RealParameterType;

public class UniformMutationPerturbationParameter extends RealParameterType {
  public UniformMutationPerturbationParameter(double lowerBound, double upperBound) {
    super("uniformMutationPerturbation", lowerBound, upperBound) ;

    setParentTag(MutationType.uniform.name());
  }
}
