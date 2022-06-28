package org.uma.jmetal.auto.parameter;

import java.util.function.Function;
import org.uma.jmetal.util.errorchecking.Check;

public class StringParameter extends Parameter<String> {

  public StringParameter(String name, String[] args) {
    super(name, args);
  }

  @Override
  public void check() {
    Check.that(!getName().equals(""), "The parameter name cannot be the empty string");
    Check.notNull(getName());
  }

  @Override
  public Parameter<String> parse() {
    return super.parse(Function.identity());
  }
}
