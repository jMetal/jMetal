package org.uma.jmetal.util.checking;

import org.junit.Test;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.checking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.checking.exception.NullParameterException;
import org.uma.jmetal.util.checking.exception.ValueOutOfRangeException;

public class CheckerTest {

  @Test (expected = NullParameterException.class)
  public void shouldIsNotNullRaiseAnExceptionIfTheObjectIsNull() {
    Check.isNotNull(null);
  }

  @Test (expected = InvalidProbabilityValueException.class)
  public void shouldIsValidProbabilityRaiseAnExceptionIfTheValueIsNegative() {
    Check.probabilityIsValid(-1.0);
  }

  @Test (expected = InvalidProbabilityValueException.class)
  public void shouldIsValidProbabilityRaiseAnExceptionIfTheValueIsHigherThanOne() {
    Check.probabilityIsValid(1.1);
  }

  @Test (expected = ValueOutOfRangeException.class)
  public void shouldIsValueInRangeRaiseAnExceptionIfTheValueIsLowerThanTheLowerBound() {
    Check.valueIsInRange(2, 3, 5);
  }

  @Test (expected = ValueOutOfRangeException.class)
  public void shouldIsValueInRangeRaiseAnExceptionIfTheValueIsHigherThanTheUpperBound() {
    Check.valueIsInRange(6.2, 3.1, 5.5);
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldThatRaiseAnExceptionIfTheExpressionIsFalse() {
    Check.that(false, "");
  }
}