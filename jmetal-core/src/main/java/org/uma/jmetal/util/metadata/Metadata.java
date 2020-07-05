package org.uma.jmetal.util.metadata;

public interface Metadata<S, V> {
  V read(S source);
  
  interface RW<S, V> extends Metadata<S, V> {
    void write(S source, V value);
  }
}
