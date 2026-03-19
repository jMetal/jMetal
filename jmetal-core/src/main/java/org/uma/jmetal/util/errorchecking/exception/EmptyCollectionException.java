package org.uma.jmetal.util.errorchecking.exception;

@SuppressWarnings("serial")
public class EmptyCollectionException extends RuntimeException {
  public EmptyCollectionException() {
    super("The collection is empty");
  }

  public EmptyCollectionException(String parameterName) {
    super("The collection '" + parameterName + "' is empty");
  }
}
