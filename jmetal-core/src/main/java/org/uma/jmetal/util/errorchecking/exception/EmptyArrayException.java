package org.uma.jmetal.util.errorchecking.exception;

/**
 * Exception thrown when an array parameter is {@code null} or has zero
 * length where at least one element is required.
 */
@SuppressWarnings("serial")
public class EmptyArrayException extends RuntimeException {
  public EmptyArrayException() {
    super("The array is empty");
  }

  /**
   * @param parameterName the name of the parameter whose array is empty
   */
  public EmptyArrayException(String parameterName) {
    super("The array '" + parameterName + "' is empty");
  }
}
