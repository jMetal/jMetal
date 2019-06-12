package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;
import org.uma.jmetal.auto.parameterv2.param.Parameter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Selection extends CategoricalParameter<String> {
  private String[] args ;

  public Selection(String args[]) {
    this(args, Arrays.asList("tournament", "random")) ;
  }

  public Selection(String args[], List<String> selectionStrategies) {
    super(selectionStrategies) ;
    this.args = args ;
  }

  public CategoricalParameter<String> parse() {
    value = on("--selection", args, Function.identity());

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
    return "selection";
  }
}
