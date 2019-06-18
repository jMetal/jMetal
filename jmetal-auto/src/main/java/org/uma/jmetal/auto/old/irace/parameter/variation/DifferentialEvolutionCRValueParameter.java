package org.uma.jmetal.auto.old.irace.parameter.variation;

import org.uma.jmetal.auto.old.irace.parametertype.impl.RealParameterType;

public class DifferentialEvolutionCRValueParameter extends RealParameterType {
  public DifferentialEvolutionCRValueParameter() {
    super("differentialEvolutionCRValue", 0.0, 1.0) ;
    setParentTag(VariationType.differentialEvolutionVariation.toString());
  }
}
