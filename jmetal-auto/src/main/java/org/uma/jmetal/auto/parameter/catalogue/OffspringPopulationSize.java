package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.parameter.OrdinalParameter;

import java.util.List;

public class OffspringPopulationSize extends OrdinalParameter<Integer> {
  private String[] args ;

  public OffspringPopulationSize(String args[], List<Integer> validValues) {
    super("offspringPopulationSize", args, validValues) ;
    this.args = args ;
  }

  @Override
  public OrdinalParameter<Integer>  parse() {
    setValue(on("--offspringPopulationSize", args, Integer::parseInt));

    return this ;
  }
}
