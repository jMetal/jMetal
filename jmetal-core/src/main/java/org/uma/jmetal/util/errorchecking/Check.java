package org.uma.jmetal.util.errorchecking;

import java.util.Collection;
import java.util.Map;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.errorchecking.exception.NegativeValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.errorchecking.exception.ValueOutOfRangeException;

/**
 * Utility class that provides common argument and state checks used throughout
 * the library. Each method throws a specific unchecked exception when the
 * corresponding precondition is violated.
 *
 * <p>Exceptions thrown are defined in
 * {@link org.uma.jmetal.util.errorchecking.exception}.</p>
 */
public class Check {

  /**
   * Checks that the provided reference is not {@code null}.
   *
   * @param object the reference to check
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if
   *     {@code object} is {@code null}
   */
  public static void notNull(Object object) {
    if (null == object) {
      throw new NullParameterException("object");
    }
  }

  /**
   * Variant of {@link #notNull(Object)} that includes the parameter name in
   * the thrown exception message.
   *
   * @param object the reference to check
   * @param parameterName the name of the parameter (used in exception message)
   */
  public static void notNull(Object object, String parameterName) {
    if (null == object) {
      throw new NullParameterException(parameterName);
    }
  }

  public static void probabilityIsValid(double value) {
    /**
     * Validates that a numeric value represents a probability in the inclusive
     * range [0.0, 1.0]. Also rejects NaN and infinite values.
     *
     * @param value the probability value to check
     * @throws org.uma.jmetal.util.errorchecking.exception.InvalidProbabilityValueException
     *     if {@code value} is outside the range [0.0, 1.0] or is not a finite number
     */
    if (!Double.isFinite(value) || (value < 0.0) || (value > 1.0)) {
      throw new InvalidProbabilityValueException(value, "value");
    }
  }

  /**
   * Variant that includes a parameter name in the thrown exception.
   */
  public static void probabilityIsValid(double value, String parameterName) {
    if (!Double.isFinite(value) || (value < 0.0) || (value > 1.0)) {
      throw new InvalidProbabilityValueException(value, parameterName);
    }
  }

  public static void valueIsInRange(double value, double lowestValue, double highestValue) {
    /**
     * Checks that {@code value} lies within the inclusive range
     * [{@code lowestValue}, {@code highestValue}].
     *
     * @param value the value to check
     * @param lowestValue lower bound (inclusive)
     * @param highestValue upper bound (inclusive)
     * @throws org.uma.jmetal.util.errorchecking.exception.ValueOutOfRangeException
     *     if {@code value} is outside the specified range
     */
    if ((value < lowestValue) || (value > highestValue)) {
      throw new ValueOutOfRangeException(value, lowestValue, highestValue, "value");
    }
  }

  /**
   * Variant that includes a parameter name in the thrown exception.
   */
  public static void valueIsInRange(double value, double lowestValue, double highestValue, String parameterName) {
    if ((value < lowestValue) || (value > highestValue)) {
      throw new ValueOutOfRangeException(value, lowestValue, highestValue, parameterName);
    }
  }

  public static void valueIsInRange(int value, int lowestValue, int highestValue) {
    /**
     * Integer overload of {@link #valueIsInRange(double,double,double)}.
     *
     * @param value the integer value to check
     * @param lowestValue lower bound (inclusive)
     * @param highestValue upper bound (inclusive)
     * @throws org.uma.jmetal.util.errorchecking.exception.ValueOutOfRangeException
     *     if {@code value} is outside the specified range
     */
    if ((value < lowestValue) || (value > highestValue)) {
      throw new ValueOutOfRangeException(value, lowestValue, highestValue, "value");
    }
  }

  /**
   * Variant that includes a parameter name in the thrown exception.
   */
  public static void valueIsInRange(int value, int lowestValue, int highestValue, String parameterName) {
    if ((value < lowestValue) || (value > highestValue)) {
      throw new ValueOutOfRangeException(value, lowestValue, highestValue, parameterName);
    }
  }

  /**
   * Checks that the provided collection is not {@code null} nor empty.
   *
   * @param collection the collection to check
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if
   *     {@code collection} is {@code null}
   * @throws org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException if
   *     {@code collection} is empty
   */
  public static void collectionIsNotEmpty(Collection<?> collection) {
    notNull(collection);
    if (collection.isEmpty()) {
      throw new EmptyCollectionException("collection");
    }
  }

  /**
   * Variant of {@link #collectionIsNotEmpty(Collection)} that uses the provided
   * parameter name in the exception message.
   *
   * @param collection the collection to check
   * @param parameterName the name of the parameter to include in the exception
   */
  public static void collectionIsNotEmpty(Collection<?> collection, String parameterName) {
    notNull(collection, parameterName);
    if (collection.isEmpty()) {
      throw new EmptyCollectionException(parameterName);
    }
  }

  /**
   * Convenience check that the provided collection reference is not {@code null}
   * and not empty. This is equivalent to calling {@link #notNull(Object)} and
   * {@link #collectionIsNotEmpty(Collection)} but provided as a single, clearer
   * API for common call sites.
   *
   * @param collection the collection to check
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if
   *     {@code collection} is {@code null}
   * @throws org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException if
   *     {@code collection} is empty
   */
  public static void notNullAndNotEmpty(Collection<?> collection) {
    collectionIsNotEmpty(collection);
  }

  /**
   * Variant of {@link #notNullAndNotEmpty(Collection)} that includes a
   * parameter name in any thrown exception message.
   *
   * @param collection the collection to check
   * @param parameterName the name of the parameter to include in the exception
   */
  public static void notNullAndNotEmpty(Collection<?> collection, String parameterName) {
    collectionIsNotEmpty(collection, parameterName);
  }

  /**
   * Checks that the provided {@code String} is not {@code null} and not blank
   * (i.e. contains at least one non-whitespace character).
   *
   * @param s the string to check
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if
   *     {@code s} is {@code null}
   * @throws org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException if
   *     {@code s} is empty or blank
   */
  public static void stringIsNotBlank(String s) {
    notNull(s);
    if (s.isBlank()) {
      throw new org.uma.jmetal.util.errorchecking.exception.EmptyStringException("string");
    }
  }

  public static void stringIsNotBlank(String s, String parameterName) {
    notNull(s, parameterName);
    if (s.isBlank()) {
      throw new EmptyCollectionException(parameterName);
    }
  }

  /**
   * Checks that the provided array is not {@code null} and has at least one
   * element.
   *
   * @param array the array to check
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if
   *     {@code array} is {@code null}
   * @throws org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException if
   *     {@code array} has length zero
   */
  public static void arrayIsNotEmpty(Object[] array) {
    notNull(array);
    if (array.length == 0) {
      throw new org.uma.jmetal.util.errorchecking.exception.EmptyArrayException("array");
    }
  }

  public static void arrayIsNotEmpty(Object[] array, String parameterName) {
    notNull(array, parameterName);
    if (array.length == 0) {
      throw new EmptyCollectionException(parameterName);
    }
  }

  /**
   * Checks that the provided map is not {@code null} and not empty.
   *
   * @param map the map to check
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if
   *     {@code map} is {@code null}
   * @throws org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException if
   *     {@code map} is empty
   */
  public static void mapIsNotEmpty(Map<?, ?> map) {
    notNull(map);
    if (map.isEmpty()) {
      throw new org.uma.jmetal.util.errorchecking.exception.EmptyMapException("map");
    }
  }

  public static void mapIsNotEmpty(Map<?, ?> map, String parameterName) {
    notNull(map, parameterName);
    if (map.isEmpty()) {
      throw new EmptyCollectionException(parameterName);
    }
  }

  /**
   * Ensures that a collection does not contain {@code null} elements.
   *
   * @param collection the collection to inspect
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if
   *     {@code collection} is {@code null} or contains a {@code null} element
   */
  public static void noNullElements(Collection<?> collection) {
    notNull(collection);
    for (Object e : collection) {
      if (e == null) {
        throw new org.uma.jmetal.util.errorchecking.exception.NullElementException("collection");
      }
    }
  }

  public static void noNullElements(Collection<?> collection, String parameterName) {
    notNull(collection, parameterName);
    for (Object e : collection) {
      if (e == null) {
        throw new NullParameterException(parameterName + " element");
      }
    }
  }

  /**
   * Validates that {@code index} is a valid index into a sequence of the
   * specified {@code size}: {@code 0 <= index < size}.
   *
   * @param index the index to check
   * @param size the size of the sequence
   * @throws org.uma.jmetal.util.errorchecking.exception.ValueOutOfRangeException if
   *     {@code index} is outside the valid range
   */
  public static void indexInRange(int index, int size) {
    if (index < 0 || index >= size) {
      throw new ValueOutOfRangeException(index, 0, size - 1, "index");
    }
  }

  public static void indexInRange(int index, int size, String parameterName) {
    if (index < 0 || index >= size) {
      throw new ValueOutOfRangeException(index, 0, size - 1, parameterName);
    }
  }

  /**
   * Ensures that {@code value} is strictly positive (> 0).
   *
   * @param value the value to check
   * @throws org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException if
   *     {@code value} is not positive
   */
  public static void valueIsPositive(double value) {
    if (!(value > 0.0)) {
      throw new org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException(value, "value");
    }
  }

  public static void valueIsPositive(double value, String parameterName) {
    if (!(value > 0.0)) {
      throw new org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException(value, parameterName);
    }
  }

  public static void valueIsPositive(int value) {
    if (value <= 0) {
      throw new org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException(value, "value");
    }
  }

  public static void valueIsPositive(int value, String parameterName) {
    if (value <= 0) {
      throw new org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException(value, parameterName);
    }
  }

  /**
   * Validates that {@code value} is a finite, non-NaN number.
   *
   * @param value the double value to check
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException
   *     if {@code value} is NaN or infinite
   */
  public static void valueIsFinite(double value) {
    if (!Double.isFinite(value)) {
      throw new org.uma.jmetal.util.errorchecking.exception.InvalidNumberException(value);
    }
  }

  public static void valueIsFinite(double value, String parameterName) {
    if (!Double.isFinite(value)) {
      throw new org.uma.jmetal.util.errorchecking.exception.InvalidNumberException(value, parameterName);
    }
  }

  /**
   * Validates a boolean expression and throws an {@link InvalidConditionException}
   * with the provided message when the expression is false. Use this for
   * precondition checks that require a custom error message.
   *
   * @param expression the boolean condition expected to be true
   * @param message the message to include in the thrown exception when the
   *     condition is false
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException
   *     if {@code expression} is {@code false}
   */
  public static void that(boolean expression, String message) {
    if (!expression) {
      throw new InvalidConditionException(message);
    }
  }

  /**
   * Ensures that the provided numeric value is not negative. Zero is allowed.
   *
   * @param value the value to check
   * @throws org.uma.jmetal.util.errorchecking.exception.NegativeValueException
   *     if {@code value} is negative
   */
  public static void valueIsNotNegative(double value) {
    if (value < 0.0) {
      throw new NegativeValueException(value);
    }
  }

  public static void valueIsNotNegative(double value, String parameterName) {
    if (value < 0.0) {
      throw new NegativeValueException(value, parameterName);
    }
  }
}
