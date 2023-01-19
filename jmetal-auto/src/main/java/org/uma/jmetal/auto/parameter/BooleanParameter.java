package org.uma.jmetal.auto.parameter;

import org.uma.jmetal.util.errorchecking.Check;

public class BooleanParameter extends Parameter<Boolean> {

  public BooleanParameter(String name, String[] args) {
    super(name, args);
  }

  @Override
  public void check() {
    Check.that(!getName().equals(""), "The parameter name cannot be the empty string");
    Check.notNull(getName());
  }

  @Override
  public Parameter<Boolean> parse() {
    return super.parse(Boolean::parseBoolean);
  }
}
