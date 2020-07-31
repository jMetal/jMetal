package org.uma.jmetal.experimental.auto.parameter.catalogue;

import org.uma.jmetal.experimental.auto.parameter.OrdinalParameter;

import java.util.List;

public class PopulationSizeWithArchive extends OrdinalParameter<Integer> {
  private String[] args ;

  public PopulationSizeWithArchive(String args[], List<Integer> validValues) {
    super("populationSizeWithArchive", args, validValues) ;
    this.args = args ;
    //check(value) ;
  }

  @Override
  public OrdinalParameter<Integer> parse() {
    setValue(on("--populationSizeWithArchive", args, Integer::parseInt));
    return this ;
  }
}
