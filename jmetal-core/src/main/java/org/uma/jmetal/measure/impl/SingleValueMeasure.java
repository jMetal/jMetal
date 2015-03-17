package org.uma.jmetal.measure.impl;

import org.uma.jmetal.measure.PullMeasure;
import org.uma.jmetal.measure.PushMeasure;

/**
 * A {@link org.uma.jmetal.measure.impl.SingleValueMeasure} provides a simple way to define a
 * measure that merely stores a single value
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SingleValueMeasure<T> extends SimplePushMeasure<T> implements
    PullMeasure<T>, PushMeasure<T> {

  private T value ;

  /**
   * Create a {@link org.uma.jmetal.measure.impl.SingleValueMeasure}
   */
  public SingleValueMeasure() {
    super(
        "SingleValue",
        "Generic measure which stores a single value.");
  }

  /**
   * @param value The value to be stored
   */
  public synchronized void set(T value) {
    this.value = value ;
  }

  /**
   * @return the current value
   */
  @Override
  public synchronized T get() {
    return value;
  }

  @Override public void push (T value) {
    super.push(value) ;
  }
}
