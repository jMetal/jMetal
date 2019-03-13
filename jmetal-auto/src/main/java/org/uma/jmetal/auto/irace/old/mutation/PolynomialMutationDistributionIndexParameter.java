package org.uma.jmetal.auto.irace.old.mutation;

import org.uma.jmetal.auto.irace.old.Parameter;
import org.uma.jmetal.auto.irace.old.ParameterTypes;

import java.util.Collections;

public class PolynomialMutationDistributionIndexParameter extends Parameter {
  public PolynomialMutationDistributionIndexParameter() {
    super(
     "polynomialMutationDistributionIndex",
     "--polynomialMultationDistributionIndex",
        ParameterTypes.r,
        "(5.0, 400.0)",
        "| mutation %in% c(\"Polynomial\"\")",
        Collections.emptyList()
    ) ;
  }
}
