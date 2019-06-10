package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.OrdinalParameter;

import java.util.Arrays;
import java.util.List;

public class PopulationSizeWithArchive extends OrdinalParameter<Integer> {

  public PopulationSizeWithArchive(String args[]) {
    this(args, Arrays.asList(100)) ;
  }

  public PopulationSizeWithArchive(String args[], List<Integer> validValues) {
    super(validValues) ;
    value = on("--populationSizeWithArchive", args, Integer::parseInt);
    check(value) ;
  }

  @Override
  public String getName() {
    return "populationSizeWithArchive";
  }
}
