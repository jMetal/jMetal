package org.uma.jmetal.util.checking;

import org.junit.Test;
import org.uma.jmetal.util.checking.exception.ExpressionIsNotValidException;
import org.uma.jmetal.util.checking.exception.InvalidProbabilityValueException;

import org.uma.jmetal.util.checking.exception.NullParameterException;
import org.uma.jmetal.util.checking.exception.ValueOutOfRangeException;

public class CheckerTest {

  @Test (expected = NullParameterException.class)
  public void shouldIsNotNullRaiseAnExceptionIfTheObjectIsNull() {
    Checker.isNotNull(null);
  }

  @Test (expected = InvalidProbabilityValueException.class)
  public void shouldIsValidProbabilityRaiseAnExceptionIfTheValueIsNegative() {
    Checker.isValidProbability(-1.0);
  }

  @Test (expected = InvalidProbabilityValueException.class)
  public void shouldIsValidProbabilityRaiseAnExceptionIfTheValueIsHigherThanOne() {
    Checker.isValidProbability(1.1);
  }

  @Test (expected = ValueOutOfRangeException.class)
  public void shouldIsValueInRangeRaiseAnExceptionIfTheValueIsLowerThanTheLowerBound() {
    Checker.valueIsInRange(2, 3, 5);
  }

  @Test (expected = ValueOutOfRangeException.class)
  public void shouldIsValueInRangeRaiseAnExceptionIfTheValueIsHigherThanTheUpperBound() {
    Checker.valueIsInRange(6.2, 3.1, 5.5);
  }

  @Test (expected = ExpressionIsNotValidException.class)
  public void shouldThatRaiseAnExceptionIfTheExpressionIsFalse() {
    Checker.that(false, "");
  }
}