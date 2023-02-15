package org.uma.jmetal.auto.parameter;

import org.uma.jmetal.util.errorchecking.Check;

public class PositiveDoubleValue extends Parameter<Double> {

  public PositiveDoubleValue(String name) {
    super(name);
  }

  @Override
  public void check() {
    Check.that(value() > 0, "The " + name() + " value " + value() + " " +
        "cannot not be <= 0");
  }

  @Override
  public Parameter<Double> parse(String[] arguments) {
    return super.parse(Double::parseDouble, arguments);
  }
}
