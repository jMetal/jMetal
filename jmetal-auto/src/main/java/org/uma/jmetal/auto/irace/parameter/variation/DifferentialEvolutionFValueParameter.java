package org.uma.jmetal.auto.irace.parameter.variation;

import org.uma.jmetal.auto.irace.parameter.crossover.CrossoverType;
import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class DifferentialEvolutionFValueParameter extends RealParameterType {
  public DifferentialEvolutionFValueParameter() {
    super("differentialEvolutionFValue", 0.0, 1.0) ;
    setParentTag(VariationType.DE.toString());
  }
}
