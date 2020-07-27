package org.uma.jmetal.util.bounds;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Describes a pair of lower and upper bounds for a {@link Comparable} value.
 *
 * @param <T>
 *          the type of {@link Bounds}
 */
public interface Bounds<T extends Comparable<T>> {
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
  public static <T extends Comparable<T>> Bounds<T> create(T lowerBound, T upperBound) {
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

  /**
   * Utility method to convert a legacy {@link Pair} into a {@link Bounds}. The
   * resulting {@link Bounds} represents the state of the {@link Pair} at call
   * time. Later changes of the {@link Pair} do not reflect on the previously
   * created {@link Bounds}. It allows to not keep a reference on the {@link Pair}
   * instance, which can be garbage collected.
   * 
   * @param <T>
   *          the type of elements
   * @param pair
   *          the {@link Pair} to translate
   * @return the resulting {@link Bounds}
   * @deprecated This method is here for legacy purpose. Do not use since it
   *             should disappear soon.
   */
  @Deprecated
  public static <T extends Comparable<T>> Bounds<T> fromPair(Pair<T, T> pair) {
    return create(pair.getLeft(), pair.getRight());
  }

  /**
   * Utility method to convert this {@link Bounds} into a legacy {@link Pair}. The
   * resulting {@link Pair} represents the state of the {@link Bounds} at call
   * time. Later changes of the {@link Bounds} do not reflect on the previously
   * created {@link Pair}. It allows to not keep a reference on the {@link Bounds}
   * instance, which can be garbage collected.
   * 
   * @return a {@link Pair} representing this {@link Bounds}
   * @deprecated This method is here for legacy purpose. Do not use since it
   *             should disappear soon.
   */
  @Deprecated
  default public Pair<T, T> toPair() {
    return new ImmutablePair<>(getLowerBound(), getUpperBound());
  }
}
