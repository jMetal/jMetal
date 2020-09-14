package org.uma.jmetal.parallel.asynchronous.task;

import java.io.Serializable;

public interface ParallelTask<S> extends Serializable {
  S getContents();
  long getIdentifier();

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
