package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.OrdinalParameter;

import java.util.List;

public class OffspringPopulationSize extends OrdinalParameter<Integer> {
  public OffspringPopulationSize(String args[], List<Integer> validValues) {
    super(validValues) ;
    value = on("--offspringPopulationSize", args, Integer::parseInt);
  }

  @Override
  public String getName() {
    return "offspringPopulationSize";
  }
}
