package org.uma.jmetal.util.errorchecking.exception;

/**
 * Exception thrown when a numeric parameter is not a finite number (NaN or
 * infinite), and a finite value is required.
 */
@SuppressWarnings("serial")
public class InvalidNumberException extends RuntimeException {
  public InvalidNumberException(double value) {
    super("The numeric value " + value + " is invalid (NaN or infinite)");
  }

  /**
   * @param parameterName the name of the numeric parameter that is invalid
   */
  public InvalidNumberException(double value, String parameterName) {
    super("The numeric value '" + parameterName + "' (" + value + ") is invalid (NaN or infinite)");
  }
}
