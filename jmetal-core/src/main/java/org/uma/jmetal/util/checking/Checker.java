package org.uma.jmetal.util.checking;

import org.uma.jmetal.util.checking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.checking.exception.NullParameterException;
import org.uma.jmetal.util.checking.exception.ValueOutOfRangeException;

public class Checker {
  public Checker isNotNull(Object object) {
    if (null == object) {
      throw new NullParameterException() ;
    }

    return this ;
  }

  public Checker isValidProbability(double value) {
    if ((value < 0.0) && (value > 1.0)) {
      throw new InvalidProbabilityValueException(value) ;
    }

    return this ;
  }

  public Checker isValueInRange(double value, double lowestValue, double highestValue) {
    if ((value < lowestValue) && (value > highestValue)) {
      throw new ValueOutOfRangeException(value, lowestValue, highestValue) ;
    }

    return this ;
  }

  public Checker isValueInRange(int value, int lowestValue, int highestValue) {
    if ((value < lowestValue) && (value > highestValue)) {
      throw new ValueOutOfRangeException(value, lowestValue, highestValue) ;
    }

    return this ;
  }

  public Checker isTrue(boolean expression, String message) {
    if (!expression) {
      throw new RuntimeException(message) ;
    }

    return this ;
  }
}
