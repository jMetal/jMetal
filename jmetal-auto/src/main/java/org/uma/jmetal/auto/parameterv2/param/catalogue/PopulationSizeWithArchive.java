package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.IntegerParameter;
import org.uma.jmetal.auto.parameterv2.param.OrdinalParameter;

import java.util.Arrays;
import java.util.List;

public class PopulationSizeWithArchive extends OrdinalParameter<Integer> {
  private String[] args ;

  public PopulationSizeWithArchive(String args[], List<Integer> validValues) {
    super(validValues) ;
    this.args = args ;
    //check(value) ;
  }

  @Override
  public OrdinalParameter<Integer> parse() {
    value = on("--populationSizeWithArchive", args, Integer::parseInt);
    return this ;
  }

  @Override
  public String getName() {
    return "populationSizeWithArchive";
  }
}
