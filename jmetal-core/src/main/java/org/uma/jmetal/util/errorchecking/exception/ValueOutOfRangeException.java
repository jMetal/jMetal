package org.uma.jmetal.util.errorchecking.exception;

@SuppressWarnings("serial")
public class ValueOutOfRangeException extends RuntimeException {
  public ValueOutOfRangeException(int value, int lowestValue, int highestValue) {
    super("The parameter " + value + " is not in the inclusive range [" + lowestValue + ", " + highestValue + "]");
  }

  public ValueOutOfRangeException(double value, double lowestValue, double highestValue) {
    super("The parameter " + value + " is not in the inclusive range [" + lowestValue + ", " + highestValue + "]");
  }

  public ValueOutOfRangeException(int value, int lowestValue, int highestValue, String parameterName) {
    super("The parameter '" + parameterName + "' (" + value + ") is not in the inclusive range [" + lowestValue + ", " + highestValue + "]");
  }

  public ValueOutOfRangeException(double value, double lowestValue, double highestValue, String parameterName) {
    super("The parameter '" + parameterName + "' (" + value + ") is not in the inclusive range [" + lowestValue + ", " + highestValue + "]");
  }
}
