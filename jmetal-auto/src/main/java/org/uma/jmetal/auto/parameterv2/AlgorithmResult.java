package org.uma.jmetal.auto.parameterv2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

class AlgorithmResult extends Parameter<String> {
  private List<String> validValues = Arrays.asList("externalArchive", "population") ;

  public AlgorithmResult(String args[]) {
    type = ParameterType.categorical ;
    value = on("--algorithmResult", args, Function.identity());
    check(value) ;
  }

  private void check(String value) {
    if (!validValues.contains(value)) {
      throw new RuntimeException("Invalid value: " + value) ;
    }
  }

  @Override
  public String getName() {
    return "algorithmResult";
  }

  public List<String> getValidValues() { return validValues ;}
}
