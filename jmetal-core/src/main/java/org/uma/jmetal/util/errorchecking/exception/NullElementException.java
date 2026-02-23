package org.uma.jmetal.util.errorchecking.exception;

/**
 * Exception thrown when a collection contains a {@code null} element where
 * non-null elements are required.
 */
@SuppressWarnings("serial")
public class NullElementException extends RuntimeException {
  public NullElementException() {
    super("A collection contains a null element");
  }

  /**
   * @param parameterName the name of the parameter whose collection contains
   *     a null element
   */
  public NullElementException(String parameterName) {
    super("The collection '" + parameterName + "' contains a null element");
  }
}
