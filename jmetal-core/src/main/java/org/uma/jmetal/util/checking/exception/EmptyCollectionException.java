package org.uma.jmetal.util.checking.exception;

@SuppressWarnings("serial")
public class EmptyCollectionException extends RuntimeException {
  public EmptyCollectionException() {
    super("The collection is empty") ;
  }
}
