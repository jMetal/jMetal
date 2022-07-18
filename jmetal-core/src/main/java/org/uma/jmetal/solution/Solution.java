package org.uma.jmetal.solution;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Interface representing a Solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @param <T> Type (Double, Integer, etc.)
 */
public interface Solution<T> extends Serializable {
  List<T> variables() ;
  double[] objectives() ;
  double[] constraints() ;
  Map<Object,Object> attributes() ;

  @Nullable Solution<T> copy() ;
}
