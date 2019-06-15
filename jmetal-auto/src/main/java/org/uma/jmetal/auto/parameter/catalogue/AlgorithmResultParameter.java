package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.parameter.Parameter;

import java.util.List;
import java.util.function.Function;

public class AlgorithmResultParameter extends CategoricalParameter<String> {

  public AlgorithmResultParameter(String args[], List<String> validValues) {
    super("algorithmResult", args, validValues) ;
  }

  @Override
  public CategoricalParameter<String> parse() {
    setValue(on("--algorithmResult", getArgs(), Function.identity()));

    for (Parameter<?> parameter : getGlobalParameters()) {
      parameter.parse().check();
    }

    getSpecificParameters()
        .forEach(
            (key, parameter) -> {
              if (key.equals(this.getValue())) {
                parameter.parse().check();
              }
            });

    return this ;
  }
}
