package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.OrdinalParameter;

import java.util.List;

public class OffspringPopulationSize extends OrdinalParameter<Integer> {
  private String[] args ;

  public OffspringPopulationSize(String args[], List<Integer> validValues) {
    super(validValues) ;
    this.args = args ;
  }

  @Override
  public OrdinalParameter<Integer>  parse() {
    value = on("--offspringPopulationSize", args, Integer::parseInt);
    return this ;
  }

  @Override
  public String getName() {
    return "offspringPopulationSize";
  }
}
