package org.uma.jmetal.util.errorchecking.exception;

/**
 * Exception thrown when a string parameter is {@code null} or contains only
 * whitespace characters.
 */
@SuppressWarnings("serial")
public class EmptyStringException extends RuntimeException {
  public EmptyStringException() {
    super("The string is empty or blank");
  }

  /**
   * @param parameterName the name of the parameter whose value is empty/blank
   */
  public EmptyStringException(String parameterName) {
    super("The string '" + parameterName + "' is empty or blank");
  }
}
