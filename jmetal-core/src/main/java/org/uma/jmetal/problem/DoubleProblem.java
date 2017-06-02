package org.uma.jmetal.problem;

import org.uma.jmetal.solution.DoubleSolution;

/**
 * Interface representing continuous problems
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleProblem extends Problem<DoubleSolution> {
  Double getLowerBound(int index) ;
  Double getUpperBound(int index) ;
}
