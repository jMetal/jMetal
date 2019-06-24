package org.uma.jmetal.auto.old.irace.parameter.variation;

import org.uma.jmetal.auto.old.irace.parametertype.impl.RealParameterType;

public class DifferentialEvolutionFValueParameter extends RealParameterType {
  public DifferentialEvolutionFValueParameter() {
    super("differentialEvolutionFValue", 0.0, 1.0) ;
    setParentTag(VariationType.differentialEvolutionVariation.toString());
  }
}
