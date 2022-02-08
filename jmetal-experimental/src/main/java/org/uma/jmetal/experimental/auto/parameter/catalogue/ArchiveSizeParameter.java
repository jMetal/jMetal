package org.uma.jmetal.experimental.auto.parameter.catalogue;

import org.uma.jmetal.experimental.auto.parameter.Parameter;
import org.uma.jmetal.util.errorchecking.Check;

public class ArchiveSizeParameter extends Parameter<Integer> {

  public ArchiveSizeParameter(String[] args) {
    super("populationSize", args) ;
  }

  @Override
  public Parameter<Integer> parse() {
    return super.parse(Integer::parseInt) ;
  }

  @Override
  public void check() {
    Check.that(getValue() > 0, "The population size cannot not be <= 0");
  }
}
