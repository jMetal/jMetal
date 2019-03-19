package org.uma.jmetal.auto.irace.parametertype.parameter.mutation;

import org.uma.jmetal.auto.irace.parametertype.impl.CategoricalParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class MutationParameter extends CategoricalParameterType {

  public MutationParameter() {
    super("mutation") ;

    CategoricalParameterType crossoverRepairStrategy = new CategoricalParameterType("crossoverRepairStrategy") ;
    crossoverRepairStrategy.addValue("random");
    crossoverRepairStrategy.addValue("bounds");
    crossoverRepairStrategy.addValue("round");

    RealParameterType crossoverProbability =
            new RealParameterType("mutationProbability", 0, 1);

    addGlobalParameter(crossoverProbability);
    addGlobalParameter(crossoverRepairStrategy);
  }
}

