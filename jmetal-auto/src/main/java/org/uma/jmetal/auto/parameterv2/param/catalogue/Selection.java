package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Selection extends CategoricalParameter<String> {
  public Selection(String args[]) {
    this(args, Arrays.asList("tournament", "random")) ;
  }

  public Selection(String args[], List<String> selectionStrategies) {
    super(selectionStrategies) ;
    value = on("--selection", args, Function.identity());
    check(value) ;

    /*
    if (value.equals("tournament")) {
      getSpecificParameters().add(new NumericalParameter<Integer>() {
        @Override
        public String getName() {
          return null;
        }
      }) ;
    }

     */
  }

  @Override
  public String getName() {
    return "selection";
  }
}
