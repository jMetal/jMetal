package org.uma.jmetal.auto.old.irace.parameter.mutation;

import org.uma.jmetal.auto.old.irace.parametertype.impl.CategoricalParameterType;
import org.uma.jmetal.auto.old.irace.parametertype.impl.RealParameterType;

public class MutationParameter extends CategoricalParameterType {

  public MutationParameter() {
    super("mutation") ;

    CategoricalParameterType mutationRepairStrategy = new CategoricalParameterType("mutationRepairStrategy") ;
    mutationRepairStrategy.addValue("random");
    mutationRepairStrategy.addValue("bounds");
    mutationRepairStrategy.addValue("round");

    RealParameterType mutationProbability =
            new RealParameterType("mutationProbability", 0, 1);

    addGlobalParameter(mutationProbability);
    addGlobalParameter(mutationRepairStrategy);
  }
}

