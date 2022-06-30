package org.uma.jmetal.problem.integerproblem ;

import java.util.List;
import org.uma.jmetal.problem.BoundedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * Interface representing integer problems
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface IntegerProblem extends Problem<IntegerSolution> {
  List<Bounds<Integer>> getBoundsForVariables() ;
}
