package org.uma.jmetal.util.errorchecking;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.errorchecking.exception.NegativeValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.errorchecking.exception.ValueOutOfRangeException;

@DisplayName("Check utility tests")
class CheckTest {

  @Nested
  @DisplayName("Collection checks")
  class CollectionChecks {

    @Test
    @DisplayName("given null, when checking collectionIsNotEmpty, then throw NullParameterException")
    void givenNull_whenCollectionIsChecked_thenThrowNullParameterException() {
      assertThrows(NullParameterException.class, () -> Check.collectionIsNotEmpty(null));
    }

    @Test
    @DisplayName("given empty, when checking collectionIsNotEmpty, then throw EmptyCollectionException")
    void givenEmpty_whenCollectionIsChecked_thenThrowEmptyCollectionException() {
      List<Object> list = new ArrayList<>();
      assertThrows(EmptyCollectionException.class, () -> Check.collectionIsNotEmpty(list));
    }

    @Test
    @DisplayName("given non-empty, when checking collectionIsNotEmpty, then succeed")
    void givenNonEmpty_whenCollectionIsChecked_thenSucceed() {
      List<Integer> list = new ArrayList<>();
      list.add(1);
      assertDoesNotThrow(() -> Check.collectionIsNotEmpty(list));
    }

    @Test
    @DisplayName("given null, when checking notNullAndNotEmpty, then throw NullParameterException")
    void givenNull_whenNotNullAndNotEmpty_thenThrowNullParameterException() {
      assertThrows(NullParameterException.class, () -> Check.notNullAndNotEmpty(null));
    }

    @Test
    @DisplayName("given empty, when checking notNullAndNotEmpty, then throw EmptyCollectionException")
    void givenEmpty_whenNotNullAndNotEmpty_thenThrowEmptyCollectionException() {
      List<Object> list = new ArrayList<>();
      assertThrows(EmptyCollectionException.class, () -> Check.notNullAndNotEmpty(list));
    }

    @Test
    @DisplayName("given non-empty, when checking notNullAndNotEmpty, then succeed")
    void givenNonEmpty_whenNotNullAndNotEmpty_thenSucceed() {
      List<Integer> list = new ArrayList<>();
      list.add(1);
      assertDoesNotThrow(() -> Check.notNullAndNotEmpty(list));
    }
  }

  @Test
  @DisplayName("given null with parameter name, when checking notNullAndNotEmpty, then message contains parameter name")
  void givenNullWithParameterName_whenNotNullAndNotEmpty_thenMessageContainsParameterName() {
    NullParameterException ex = assertThrows(NullParameterException.class,
        () -> Check.notNullAndNotEmpty(null, "population"));
    org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("population"));
  }

  @Test
  @DisplayName("given invalid probability with parameter name, then message contains parameter name")
  void givenInvalidProbabilityWithParameterName_thenMessageContainsParameterName() {
    InvalidProbabilityValueException ex = assertThrows(InvalidProbabilityValueException.class,
        () -> Check.probabilityIsValid(-0.1, "prob"));
    org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("prob"));
  }

  @Test
  @DisplayName("given out-of-range value with parameter name, then message contains parameter name")
  void givenOutOfRangeWithParameterName_thenMessageContainsParameterName() {
    ValueOutOfRangeException ex = assertThrows(ValueOutOfRangeException.class,
        () -> Check.valueIsInRange(10, 0, 5, "count"));
    org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("count"));
  }

  @Test
  @DisplayName("given non-positive with parameter name, then message contains parameter name")
  void givenNonPositiveWithParameterName_thenMessageContainsParameterName() {
    org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException ex = assertThrows(
        org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException.class,
        () -> Check.valueIsPositive(0, "size"));
    org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("size"));
  }

  @Nested
  @DisplayName("Range and probability checks")
  class RangeAndProbabilityChecks {

    @Test
    @DisplayName("given negative probability, when validating, then throw InvalidProbabilityValueException")
    void givenNegativeProbability_whenValidated_thenThrowInvalidProbabilityValueException() {
      assertThrows(InvalidProbabilityValueException.class, () -> Check.probabilityIsValid(-1.0));
    }

    @Test
    @DisplayName("given probability > 1, when validated, then throw InvalidProbabilityValueException")
    void givenProbabilityGreaterThanOne_whenValidated_thenThrowInvalidProbabilityValueException() {
      assertThrows(InvalidProbabilityValueException.class, () -> Check.probabilityIsValid(1.1));
    }

    @Test
    @DisplayName("given value lower than lower bound, when checking range, then throw ValueOutOfRangeException")
    void givenValueLowerThanLowerBound_whenChecked_thenThrowValueOutOfRangeException() {
      assertThrows(ValueOutOfRangeException.class, () -> Check.valueIsInRange(2, 3, 5));
    }

    @Test
    @DisplayName("given value higher than upper bound, when checking range, then throw ValueOutOfRangeException")
    void givenValueHigherThanUpperBound_whenChecked_thenThrowValueOutOfRangeException() {
      assertThrows(ValueOutOfRangeException.class, () -> Check.valueIsInRange(6.2, 3.1, 5.5));
    }
  }

  @Nested
  @DisplayName("Condition and negative value checks")
  class ConditionAndNegativeChecks {

    @Test
    @DisplayName("given false expression, when using that(), then throw InvalidConditionException")
    void givenFalseExpression_whenUsingThat_thenThrowInvalidConditionException() {
      assertThrows(InvalidConditionException.class, () -> Check.that(false, ""));
    }

    @Test
    @DisplayName("given negative value, when checking not negative, then throw NegativeValueException")
    void givenNegativeValue_whenCheckingNotNegative_thenThrowNegativeValueException() {
      assertThrows(NegativeValueException.class, () -> Check.valueIsNotNegative(-1));
    }
  }

  @Nested
  @DisplayName("Additional utility checks")
  class AdditionalChecks {

    @Test
    @DisplayName("given blank string, when checked, then throw EmptyStringException")
    void givenBlankString_whenChecked_thenThrow() {
      assertThrows(org.uma.jmetal.util.errorchecking.exception.EmptyStringException.class,
          () -> Check.stringIsNotBlank(" \t  \n"));
    }

    @Test
    @DisplayName("given non-blank string, when checked, then succeed")
    void givenNonBlankString_whenChecked_thenSucceed() {
      assertDoesNotThrow(() -> Check.stringIsNotBlank("hello"));
    }

    @Test
    @DisplayName("given null array, when checked, then throw NullParameterException")
    void givenNullArray_whenChecked_thenThrow() {
      assertThrows(org.uma.jmetal.util.errorchecking.exception.NullParameterException.class,
          () -> Check.arrayIsNotEmpty((Object[]) null));
    }

    @Test
    @DisplayName("given empty array, when checked, then throw EmptyArrayException")
    void givenEmptyArray_whenChecked_thenThrow() {
      assertThrows(org.uma.jmetal.util.errorchecking.exception.EmptyArrayException.class,
          () -> Check.arrayIsNotEmpty(new Object[0]));
    }

    @Test
    @DisplayName("given null map, when checked, then throw NullParameterException")
    void givenNullMap_whenChecked_thenThrow() {
      assertThrows(org.uma.jmetal.util.errorchecking.exception.NullParameterException.class,
          () -> Check.mapIsNotEmpty(null));
    }

    @Test
    @DisplayName("given empty map, when checked, then throw EmptyMapException")
    void givenEmptyMap_whenChecked_thenThrow() {
      assertThrows(org.uma.jmetal.util.errorchecking.exception.EmptyMapException.class,
          () -> Check.mapIsNotEmpty(java.util.Map.of()));
    }

    @Test
    @DisplayName("given collection with null element, when checked, then throw NullElementException")
    void givenCollectionWithNullElement_whenChecked_thenThrow() {
      java.util.List<String> list = new java.util.ArrayList<>();
      list.add(null);
      assertThrows(org.uma.jmetal.util.errorchecking.exception.NullElementException.class,
          () -> Check.noNullElements(list));
    }

    @Test
    @DisplayName("given invalid index, when checked, then throw ValueOutOfRangeException")
    void givenInvalidIndex_whenChecked_thenThrow() {
      assertThrows(org.uma.jmetal.util.errorchecking.exception.ValueOutOfRangeException.class,
          () -> Check.indexInRange(3, 3));
    }

    @Test
    @DisplayName("given non-positive value, when checked, then throw NonPositiveValueException")
    void givenNonPositiveValue_whenChecked_thenThrow() {
      assertThrows(org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException.class,
          () -> Check.valueIsPositive(0));
    }

    @Test
    @DisplayName("given NaN value, when checking finite, then throw InvalidNumberException")
    void givenNaN_whenCheckingFinite_thenThrow() {
      assertThrows(org.uma.jmetal.util.errorchecking.exception.InvalidNumberException.class,
          () -> Check.valueIsFinite(Double.NaN));
    }
  }
}
