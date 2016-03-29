package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.solution.Solution;

/**
 * Created by ajnebro on 23/12/15.
 */
public interface TaggedSolution<T> extends Solution<T> {
  public String getTag() ;
}
