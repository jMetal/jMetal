package org.uma.jmetal.util.errorchecking.exception;

@SuppressWarnings("serial")
public class InvalidProbabilityValueException extends RuntimeException {
  public InvalidProbabilityValueException(double value) {
    super("The parameter " + value + " is not a valid probability value");
  }

  public InvalidProbabilityValueException(double value, String parameterName) {
    super("The parameter '" + parameterName + "' (" + value + ") is not a valid probability value");
  }
}
