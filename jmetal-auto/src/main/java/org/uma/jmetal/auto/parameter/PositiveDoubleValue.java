package org.uma.jmetal.auto.parameter;

import org.uma.jmetal.util.errorchecking.Check;

public class PositiveDoubleValue extends Parameter<Double> {

  public PositiveDoubleValue(String name, String[] args) {
    super(name, args);
  }

  @Override
  public void check() {
    Check.that(getValue() > 0, "The " + getName() + " value " + getValue() + " " +
        "cannot not be <= 0");
  }

  @Override
  public Parameter<Double> parse() {
    return super.parse(Double::parseDouble);
  }
}
