package org.uma.jmetal.util.errorchecking.exception;

@SuppressWarnings("serial")
public class NonPositiveValueException extends RuntimeException {
  public NonPositiveValueException(Number value) {
    super("The parameter " + value + " is not positive");
  }

  public NonPositiveValueException(Number value, String parameterName) {
    super("The parameter '" + parameterName + "' (" + value + ") is not positive");
  }
}
