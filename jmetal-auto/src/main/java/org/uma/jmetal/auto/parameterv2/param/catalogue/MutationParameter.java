package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;
import org.uma.jmetal.auto.parameterv2.param.Parameter;

import java.util.List;
import java.util.function.Function;

public class MutationParameter extends CategoricalParameter<String> {
  private String[] args;

  public MutationParameter(String args[], List<String> crossoverOperators) {
    super(crossoverOperators);
    this.args = args;
  }

  public CategoricalParameter<String> parse() {
    value = on("--mutation", args, Function.identity());

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

    return this;
  }

  @Override
  public String getName() {
    return "mutation";
  }
}
