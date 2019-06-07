package org.uma.jmetal.auto.irace.parametertype.impl;

import org.uma.jmetal.auto.irace.parametertype.ParameterType;
import org.uma.jmetal.util.checking.Check;

/**
 * Irace parameter types
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class RealParameterType extends ParameterType {
  private String range;

  public RealParameterType(String name, double lowerBound, double upperBound) {
    this(name, "--" + name, lowerBound, upperBound);
  }

  public RealParameterType(String name, String label, double lowerBound, double upperBound) {
    super(name, label);

    Check.isTrue(lowerBound < upperBound, "The range is invalid");
    this.range = "(" + lowerBound + ", " + upperBound + ")";
  }

  @Override
  public String getParameterType() {
    return "r";
  }

  @Override
  public String getRange() {
    return range;
  }

  @Override
  public void addSpecificParameter(ParameterType parameter) {
    specificParameters.add(parameter);
  }
}
