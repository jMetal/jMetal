package org.uma.jmetal.parallel.asynchronous.task;

import java.io.Serializable;

/**
 * A unit of work of an asynchronous parallel algorithm, such as a solution to be evaluated, with an
 * identifier. A computed task can be the same object or a new one with the same identifier.
 *
 * @param <S> the type of the contents of the task
 */
public interface ParallelTask<S> extends Serializable {
  /** The object to compute, such as a solution to evaluate. */
  S getContents();

  /** The identifier of the task, which the algorithm that creates it should not repeat. */
  long getIdentifier();

  /**
   * Creates a task.
   *
   * @param identifier the identifier of the task
   * @param data the contents of the task
   * @throws IllegalArgumentException if {@code data} is null
   */
  static <S> ParallelTask<S> create(long identifier, S data) {
    if (data == null) {
      throw new IllegalArgumentException("null data");
    } else {
      return new ParallelTask<>() {
        @Override
        public S getContents() {
          return data;
        }

        @Override
        public long getIdentifier() {
          return identifier;
        }
      };
    }
  }
}
