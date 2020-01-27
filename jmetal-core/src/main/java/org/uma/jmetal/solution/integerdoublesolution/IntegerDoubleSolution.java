package org.uma.jmetal.solution.integerdoublesolution;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

/**
 * Interface representing solutions containing an integer solution and a double solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@Deprecated
public interface IntegerDoubleSolution extends Solution<Solution<?>> {
  IntegerSolution getIntegerSolution() ;
  DoubleSolution getDoubleSolution() ;
}
