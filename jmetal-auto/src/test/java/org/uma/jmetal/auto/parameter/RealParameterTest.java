package org.uma.jmetal.auto.parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

class RealParameterTest {

  @Test
  void theConstructorMustInitializeTheFieldsCorrectly() {
    String[] parameterString = new String[]{"--realParameter", "5"};
    double lowerBound = 1.0;
    double upperBound = 10.0;
    RealParameter realParameter = new RealParameter("realParameter", parameterString, lowerBound,
        upperBound);

    assertThat(realParameter.getName()).isEqualTo("realParameter", "5");
    assertThat(realParameter.getValidValues()).containsExactly(lowerBound, upperBound);
  }

  @Test
  void theConstructorMustRaiseAnExceptionInTheLowerBoundIsNotLowerThanTheUpperBound() {
    String[] parameterString = new String[]{"--realParameter", "5"};
    double lowerBound = 10.0;
    double upperBound = 10.0;

    assertThatThrownBy(() -> new RealParameter("realParameter", parameterString, lowerBound,
        upperBound)).isInstanceOf(InvalidConditionException.class);
  }

  @Test
  void parseRaisesAnExceptionIfTheValueIsNotADouble() {
    String[] parameterString = new String[]{"--realParameter", "hello"};
    double lowerBound = 10.0;
    double upperBound = 20.0;

    RealParameter realParameter = new RealParameter("realParameter", parameterString, lowerBound,
        upperBound);

    assertThatThrownBy(realParameter::parse).isInstanceOf(NumberFormatException.class);
  }

  @Test
  void parseGetsTheRightValue() {
    String[] parameterString = new String[]{"--realParameter", "15"};
    double lowerBound = 10.0;
    double upperBound = 20.0;

    RealParameter realParameter = new RealParameter("realParameter", parameterString, lowerBound,
        upperBound);

    assertThat(realParameter.parse().getValue()).isEqualTo(15) ;
  }

  @Test
  void checkRaisesAnExceptionIfTheValueIsLowerThanTheLowerBound() {
    String[] parameterString = new String[]{"--realParameter", "5"};
    double lowerBound = 10.0;
    double upperBound = 20.0;

    RealParameter realParameter = new RealParameter("realParameter", parameterString, lowerBound,
        upperBound);

    realParameter.parse();
    assertThatThrownBy(realParameter::check).isInstanceOf(JMetalException.class);
  }
}