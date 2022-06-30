package org.uma.jmetal.problem.doubleproblem;

import java.util.List;
import org.uma.jmetal.problem.BoundedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * Interface representing continuous problems
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleProblem extends Problem<DoubleSolution> {
  List<Bounds<Double>> getBoundsForVariables() ;
}
