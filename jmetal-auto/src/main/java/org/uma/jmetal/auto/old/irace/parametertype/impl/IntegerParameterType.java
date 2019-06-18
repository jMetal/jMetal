package org.uma.jmetal.auto.old.irace.parametertype.impl;

import org.uma.jmetal.auto.old.irace.parametertype.ParameterType;
import org.uma.jmetal.util.checking.Check;

/**
 * Irace parameter types
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class IntegerParameterType extends ParameterType {
  private String range;

  public IntegerParameterType(String name,  int lowerBound, int upperBound) {
    this(name, "--" + name, lowerBound, upperBound) ;
  }

  public IntegerParameterType(String name, String label, int lowerBound, int upperBound) {
    super(name, label) ;

    Check.isTrue(lowerBound < upperBound, "The range is invalid");
    this.range = "(" + lowerBound + ", " + upperBound +")";
  }

  @Override
  public String getParameterType() {
    return "i";
  }

  @Override
  public String getRange() {
    return range;
  }

  @Override
  public void addSpecificParameter(ParameterType parameter) {
    specificParameters.add(parameter) ;
  }
}
