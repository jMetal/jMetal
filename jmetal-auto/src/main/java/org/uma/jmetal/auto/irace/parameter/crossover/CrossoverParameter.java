package org.uma.jmetal.auto.irace.parameter.crossover;

import org.uma.jmetal.auto.irace.parametertype.impl.CategoricalParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class CrossoverParameter extends CategoricalParameterType {

  public CrossoverParameter() {
    super("crossover") ;

    CategoricalParameterType crossoverRepairStrategy = new CategoricalParameterType("crossoverRepairStrategy") ;
    crossoverRepairStrategy.addValue("random");
    crossoverRepairStrategy.addValue("bounds");
    crossoverRepairStrategy.addValue("round");

    RealParameterType crossoverProbability =
            new RealParameterType("crossoverProbability", 0, 1);

    addGlobalParameter(crossoverProbability);
    addGlobalParameter(crossoverRepairStrategy);
  }
}

