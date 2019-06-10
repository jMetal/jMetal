package org.uma.jmetal.auto.parameterv2.param;

import org.uma.jmetal.auto.parameterv2.param.Parameter;

public class PopulationSize extends Parameter<Integer> {
  public PopulationSize(String args[]) {
    value = on("--populationSize", args, Integer::parseInt);
    type = ParameterType.singleValue ;
  }

  @Override
  public String getName() {
    return "populationSize";
  }
}
