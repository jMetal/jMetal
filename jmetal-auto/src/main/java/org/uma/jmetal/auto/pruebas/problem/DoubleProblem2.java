package org.uma.jmetal.auto.pruebas.problem;

import java.util.List;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * Interface representing continuous problems
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleProblem2 extends Problem2<DoubleSolution> {
  List<Bounds<Double>> getVariableBounds() ;
}
