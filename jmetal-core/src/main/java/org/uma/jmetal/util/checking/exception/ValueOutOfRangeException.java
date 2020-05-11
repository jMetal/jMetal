package org.uma.jmetal.util.checking.exception;

@SuppressWarnings("serial")
public class ValueOutOfRangeException extends RuntimeException {
  public ValueOutOfRangeException(int value, int lowestValue, int highestValue) {
    super("The parameter " + value + " is not in the range (" + lowestValue + ", " + highestValue + ")") ;
  }

  public ValueOutOfRangeException(double value, double lowestValue, double highestValue) {
    super("The parameter " + value + " is not in the range (" + lowestValue + ", " + highestValue + ")") ;
  }
}
