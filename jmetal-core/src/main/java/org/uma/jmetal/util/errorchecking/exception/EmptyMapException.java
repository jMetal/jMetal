package org.uma.jmetal.util.errorchecking.exception;

/**
 * Exception thrown when a {@link java.util.Map} parameter is {@code null} or
 * empty where at least one entry is required.
 */
@SuppressWarnings("serial")
public class EmptyMapException extends RuntimeException {
  public EmptyMapException() {
    super("The map is empty");
  }

  /**
   * @param parameterName the name of the parameter whose map is empty
   */
  public EmptyMapException(String parameterName) {
    super("The map '" + parameterName + "' is empty");
  }
}
