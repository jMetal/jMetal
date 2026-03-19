package org.uma.jmetal.util.errorchecking.exception;

@SuppressWarnings("serial")
public class NegativeValueException extends RuntimeException {
  public NegativeValueException(double value) {
    super("The parameter " + value + " is negative");
  }

  public NegativeValueException(double value, String parameterName) {
    super("The parameter '" + parameterName + "' (" + value + ") is negative");
  }
}
