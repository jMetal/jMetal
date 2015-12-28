package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.extremevalues.impl.SolutionListExtremeValues;
import org.uma.jmetal.util.naming.DescribedEntity;

/**
 * Created by ajnebro on 23/12/15.
 */
public interface TaggedSolution<T> extends Solution<T> {
  public String getTag() ;
}
