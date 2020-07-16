package org.uma.jmetal.util.point.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.checking.Check;

import java.util.Arrays;
import java.util.List;

/**
 * Class representing a nadir point (minimization is assumed)
 *
 * @author Antonio J.Nebro <antonio@lcc.uma.es>
 */
public class NadirPoint extends ArrayPoint {

  public NadirPoint(int dimension) {
    super(dimension) ;
    Arrays.fill(point, Double.NEGATIVE_INFINITY);
  }

  @Override
  public void update(double[] point) {
    Check.that(point.length == this.point.length, "The point to be update have a dimension of " + point.length + " "
            + "while the parameter point has a dimension of " + point.length);

    for (int i = 0; i < point.length; i++) {
      if (this.point[i] < point[i]) {
        this.point[i] = point[i];
      }
    }
  }

  public void update(List<? extends Solution<?>> solutionList) {
    for (Solution<?> solution : solutionList) {
      update(solution.getObjectives()) ;
    }
  }
}
