package org.uma.jmetal.util.checking;

import org.uma.jmetal.util.checking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.checking.exception.NullParameterException;
import org.uma.jmetal.util.checking.exception.ValueOutOfRangeException;

public class Checker {
  public static void isNotNull(Object object) {
    if (null == object) {
      throw new NullParameterException() ;
    }
  }

  public static void isValidProbability(double value) {
    if ((value < 0.0) || (value > 1.0)) {
      throw new InvalidProbabilityValueException(value) ;
    }
  }

  public static void isValueInRange(double value, double lowestValue, double highestValue) {
    if ((value < lowestValue) || (value > highestValue)) {
      throw new ValueOutOfRangeException(value, lowestValue, highestValue) ;
    }
  }

  public static void  isValueInRange(int value, int lowestValue, int highestValue) {
    if ((value < lowestValue) || (value > highestValue)) {
      throw new ValueOutOfRangeException(value, lowestValue, highestValue) ;
    }
  }

  public static void that(boolean expression, String message) {
    if (!expression) {
      throw new RuntimeException(message) ;
    }
  }
}
