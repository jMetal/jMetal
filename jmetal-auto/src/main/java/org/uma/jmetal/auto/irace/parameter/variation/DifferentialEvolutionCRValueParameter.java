package org.uma.jmetal.auto.irace.parameter.variation;

import org.uma.jmetal.auto.irace.parameter.crossover.CrossoverType;
import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class DifferentialEvolutionCRValueParameter extends RealParameterType {
  public DifferentialEvolutionCRValueParameter() {
    super("differentialEvolutionCRValue", 0.0, 1.0) ;
    setParentTag(VariationType.DE.toString());
  }
}
