package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.parameter.Parameter;

import java.util.List;
import java.util.function.Function;

public class StringCategoricalParameter extends CategoricalParameter<String> {

  public StringCategoricalParameter(String name, String args[], List<String> validValues) {
    super(name, args, validValues) ;
  }

  @Override
  public CategoricalParameter<String> parse() {
    setValue(on("--" + getName(), getArgs(), Function.identity()));

    for (Parameter<?> parameter : getGlobalParameters()) {
      parameter.parse().check();
    }

    getSpecificParameters()
        .forEach(
            pair -> {
              if (pair.getKey().equals(getValue())) {
                pair.getValue().parse().check();
              }
            });

    return this ;
  }
}
