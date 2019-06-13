package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;
import org.uma.jmetal.auto.parameterv2.param.Parameter;

import java.util.List;
import java.util.function.Function;

public class AlgorithmResult extends CategoricalParameter<String> {
  private String args[] ;

  public AlgorithmResult(String args[], List<String> validValues) {
    super(validValues) ;
    this.args = args ;
  }

  @Override
  public CategoricalParameter<String> parse() {
    value = on("--algorithmResult", args, Function.identity());

    for (Parameter<?> parameter : getGlobalParameters()) {
      parameter.parse().check();
    }

    getSpecificParameters()
        .forEach(
            (key, parameter) -> {
              if (key.equals(this.value)) {
                parameter.parse().check();
              }
            });

    return this ;
  }

  @Override
  public String getName() {
    return "algorithmResult";
  }
}
