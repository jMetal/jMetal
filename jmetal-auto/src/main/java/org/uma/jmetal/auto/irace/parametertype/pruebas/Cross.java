package org.uma.jmetal.auto.irace.parametertype.pruebas;

import org.uma.jmetal.auto.irace.parametertype.ParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.CategoricalParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class Cross {
  CategoricalParameterType crossoverParameter ;

  public Cross() {
    crossoverParameter = new CategoricalParameterType("crossover");

    CategoricalParameterType crossoverRepairStrategy = new CategoricalParameterType("crossoverRepairStrategy") ;
    crossoverRepairStrategy.addValue("random");
    crossoverRepairStrategy.addValue("bounds");
    crossoverRepairStrategy.addValue("round");

    RealParameterType crossoverProbability =
            new RealParameterType("crossoverProbability", 0, 1);

    crossoverParameter.addGlobalParameter(crossoverProbability);
    crossoverParameter.addGlobalParameter(crossoverRepairStrategy);
  }

  public CategoricalParameterType getParameter() {
    return crossoverParameter ;
  }

  public void add(ParameterType param) {
    crossoverParameter.addAssociatedParameter(param);
  }
}

