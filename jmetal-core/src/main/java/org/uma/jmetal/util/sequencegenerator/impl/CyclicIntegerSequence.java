package org.uma.jmetal.util.sequencegenerator.impl;

import java.util.concurrent.atomic.AtomicInteger;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

/**
 * A thread-safe implementation of {@link SequenceGenerator} that generates a cyclic sequence of
 * integers from 0 (inclusive) to the specified size (exclusive). When the end of the sequence is
 * reached, it wraps around to the beginning.
 *
 * <p>This implementation is thread-safe and can be safely used by multiple threads concurrently.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * SequenceGenerator<Integer> sequence = new CyclicIntegerSequence(5);
 * // Will generate: 0, 1, 2, 3, 4, 0, 1, 2, ...
 * }</pre>
 *
 * @author Antonio J. Nebro
 */
public final class CyclicIntegerSequence implements SequenceGenerator<Integer> {
  private final AtomicInteger index;
  private final int size;

  /**
   * Creates a new cyclic integer sequence generator.
   *
   * @param size the size of the sequence (must be positive)
   * @throws IllegalArgumentException if size is not positive
   */
  public CyclicIntegerSequence(int size) {
    Check.that(size > 0, "Size " + size + " must be positive");
    this.size = size;
    this.index = new AtomicInteger(0);
  }

  @Override
  public Integer getValue() {
    return index.get();
  }

  @Override
  public void generateNext() {
    index.updateAndGet(current -> (current + 1) % size);
  }

  @Override
  public int getSequenceLength() {
    return size;
  }

  @Override
  public String toString() {
    return "CyclicIntegerSequence{" + "size=" + size + ", currentIndex=" + index.get() + '}';
  }
}
