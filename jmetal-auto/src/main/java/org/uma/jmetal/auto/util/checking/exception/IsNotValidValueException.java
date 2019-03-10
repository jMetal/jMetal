package org.uma.jmetal.auto.util.checking.exception;

public class IsNotValidValueException extends RuntimeException {
  public IsNotValidValueException(Object value, Object validValue, String message) {
    super(message + ": the value " + value + " is not equalt to " + validValue) ;
  }
}
