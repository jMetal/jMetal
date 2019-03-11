package org.uma.jmetal.solution.util;

import java.util.List;

/**
 * Interface representing classes that generates a list of solution variables
 * @param <V>
 */
public interface VariableGenerator<V> {
  List<V> generate() ;
}
