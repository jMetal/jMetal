package org.uma.jmetal.util.bounds;

import java.io.Serializable;

/**
 * Describes a pair of lower and upper bounds for a {@link Comparable} value.
 *
 * @param <T>
 *          the type of {@link Bounds}
 */
public interface Bounds<T extends Comparable<T>> extends Serializable {
  /** @return the lower limit of these {@link Bounds} */
  T getLowerBound();

  /** @return the upper limit of these {@link Bounds} */
  T getUpperBound();

  /**
   * Restrict the given value within these {@link Bounds}. If the value is lower,
   * it is replaced by {@link #getLowerBound()}. If the value is higher, it is
   * replaced by {@link #getUpperBound()}. Otherwise it is returned as provided.
   * 
   * @param value
   *          the value to restrict
   * @return the value or one of the limits
   */
  default T restrict(T value) {
    T lowerBound = getLowerBound();
    if (lowerBound.compareTo(value) > 0) {
      return lowerBound;
    }

    T upperBound = getUpperBound();
    if (upperBound.compareTo(value) < 0) {
      return upperBound;
    }

    return value;
  }

  /**
   * Create a {@link Bounds} with the given lower and upper limits.
   * 
   * @param <T>
   *          the type of {@link Bounds}
   * @param lowerBound
   *          the lowest limit
   * @param upperBound
   *          the highest limit
   * @return a {@link Bounds} with the given limits
   */
  static <T extends Comparable<T>> Bounds<T> create(T lowerBound, T upperBound) {
    if (lowerBound == null) {
      throw new IllegalArgumentException("null lower bound");
    } else if (upperBound == null) {
      throw new IllegalArgumentException("null upper bound");
    } else if (lowerBound.compareTo(upperBound) > 0) {
      throw new IllegalArgumentException(
          String.format("lower bound (%s) must be below upper bound (%s)", lowerBound, upperBound));
    } else {
      return new Bounds<T>() {
        @Override
        public T getLowerBound() {
          return lowerBound;
        }

        @Override
        public T getUpperBound() {
          return upperBound;
        }
      };
    }
  }
}
