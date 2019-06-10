package org.uma.jmetal.auto.parameterv2.param;

import org.uma.jmetal.auto.parameterv2.param.Parameter;

import java.util.Arrays;
import java.util.List;

public class PopulationSizeWithArchive extends Parameter<Integer> {
  private List<Integer> validValues ;

  public PopulationSizeWithArchive(String args[]) {
    this(args, Arrays.asList(100)) ;
  }

  public PopulationSizeWithArchive(String args[], List<Integer> validValues) {
    type = ParameterType.ordinal ;
    this.validValues = validValues ;
    value = on("--populationSizeWithArchive", args, Integer::parseInt);
    check(value) ;
  }

  private void check(Integer value) {
    if (!validValues.contains(value)) {
      throw new RuntimeException("Invalid value: " + value ) ;
    }
  }

  @Override
  public String getName() {
    return "populationSizeWithArchive";
  }

  public List<Integer> getValidValues() { return validValues ;}
}
