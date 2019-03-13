package org.uma.jmetal.auto.irace.old.mutation;

import org.uma.jmetal.auto.irace.old.Parameter;
import org.uma.jmetal.auto.irace.old.ParameterTypes;

import java.util.Arrays;

public class MutationParameter extends Parameter {

  public MutationParameter() {
    super("mutation",
        "--mutation",
        ParameterTypes.c,
        "(Polynomial, Uniform)",
        "",
        Arrays.asList(
            new MutationProbabilityParameter(),
            new PolynomialMutationDistributionIndexParameter())
    ) ;
  }
}
