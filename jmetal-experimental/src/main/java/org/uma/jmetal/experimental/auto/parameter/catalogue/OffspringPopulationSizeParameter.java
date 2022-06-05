package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.experimental.auto.parameter.OrdinalParameter;

public class OffspringPopulationSizeParameter extends OrdinalParameter<Integer> {
  private final String[] args ;

  public OffspringPopulationSizeParameter(String[] args, List<Integer> validValues) {
    super("offspringPopulationSize", args, validValues) ;
    this.args = args ;
  }

  @Override
  public OrdinalParameter<Integer>  parse() {
    setValue(on("--offspringPopulationSize", args, Integer::parseInt));

    return this ;
  }
}
