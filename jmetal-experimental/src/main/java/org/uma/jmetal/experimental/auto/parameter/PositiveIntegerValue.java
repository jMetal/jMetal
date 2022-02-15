package org.uma.jmetal.experimental.auto.parameter;

import org.uma.jmetal.util.errorchecking.Check;

public class PositiveIntegerValue extends Parameter<Integer>{
  public PositiveIntegerValue(String name, String[] args) {
    super(name, args) ;
  }

  @Override
  public void check() {
    Check.that(getValue() > 0, "The " + getName() + " value " + getValue() + " " +
            "cannot not be <= 0");
  }

  @Override
  public Parameter<Integer> parse() {
     return super.parse(Integer::parseInt) ;
  }
}
