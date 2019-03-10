package org.uma.jmetal.auto.util.checking.exception;

public class NullParameterException extends RuntimeException {
  public NullParameterException() {
    super("The parameter is null") ;
  }
}
