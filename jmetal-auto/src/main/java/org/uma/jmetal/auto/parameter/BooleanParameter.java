package org.uma.jmetal.auto.parameter;

import java.util.List;
import org.uma.jmetal.util.errorchecking.Check;

public class BooleanParameter extends Parameter<Boolean> {

  public BooleanParameter(String name) {
    super(name);
  }

  @Override
  public void check() {
    Check.that(!name().equals(""), "The parameter name cannot be the empty string");
    Check.notNull(name());
  }

  public List<String> getValidValues() {
    return List.of("TRUE", "FALSE");
  }

  @Override
  public Parameter<Boolean> parse(String[] args) {
    return super.parse(Boolean::parseBoolean, args);
  }
}
