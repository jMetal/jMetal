package org.uma.jmetal.auto.util.checking.exception;

public class InvalidProbabilityValueException extends RuntimeException {
  public InvalidProbabilityValueException(double value) {
    super("The parameter " + value + " is not a valid probability value") ;
  }
}
