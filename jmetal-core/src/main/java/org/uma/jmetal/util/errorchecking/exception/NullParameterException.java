package org.uma.jmetal.util.errorchecking.exception;

@SuppressWarnings("serial")
public class NullParameterException extends RuntimeException {
  public NullParameterException() {
    super("The parameter is null");
  }

  public NullParameterException(String parameterName) {
    super("The parameter '" + parameterName + "' is null");
  }
}
