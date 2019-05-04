package org.uma.jmetal.util;

import java.util.List;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 * An {@link IndexBounder} provide up & down boundaries for a given type of
 * {@link Number} at a given index. It is mainly used for associating a
 * {@link List} or array of numerical values with a custom set of boundaries for
 * each value. The main use is for bounded {@link Problem}s and
 * {@link Solution}s.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 * @param <T>
 */
public interface IndexBounder<T extends Number> {
  /**
   * @param index
   *          index of the {@link Number}
   * @return lower bound of the {@link Number}
   */
  public T getLowerBound(int index);

  /**
   * @param index
   *          index of the {@link Number}
   * @return upper bound of the {@link Number}
   */
  public T getUpperBound(int index);
}
