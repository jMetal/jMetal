package org.uma.jmetal.auto.irace.mutation;

import org.uma.jmetal.auto.irace.Parameter;
import org.uma.jmetal.auto.irace.ParameterType;

import java.util.Collections;

public class PolynomialMutationDistributionIndexParameter extends Parameter {
  public PolynomialMutationDistributionIndexParameter() {
    super(
     "polynomialMutationDistributionIndex",
     "--polynomialMultationDistributionIndex",
        ParameterType.r,
        "(5.0, 400.0)",
        "| mutation %in% c(\"Polynomial\"\")",
        Collections.emptyList()
    ) ;
  }
}
