package org.uma.jmetal.auto.irace.mutation;

import org.uma.jmetal.auto.irace.Parameter;
import org.uma.jmetal.auto.irace.ParameterType;
import picocli.CommandLine;

import java.util.Arrays;

public class MutationParameter extends Parameter {

  public MutationParameter() {
    super("mutation",
        "--mutation",
        ParameterType.c,
        "(Polynomial, Uniform)",
        "",
        Arrays.asList(
            new MutationProbabilityParameter(),
            new PolynomialMutationDistributionIndexParameter())
    ) ;
  }
}
