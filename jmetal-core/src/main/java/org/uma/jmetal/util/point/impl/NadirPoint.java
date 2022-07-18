package org.uma.jmetal.util.point.impl;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

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
  public void update(double @NotNull [] point) {
    Check.that(point.length == this.point.length, "The point to be update have a dimension of " + point.length + " "
            + "while the parameter point has a dimension of " + point.length);

    for (var i = 0; i < point.length; i++) {
      if (this.point[i] < point[i]) {
        this.point[i] = point[i];
      }
    }
  }

  public void update(List<? extends Solution<?>> solutionList) {
    for (@NotNull Solution<?> solution : solutionList) {
      update(solution.objectives()) ;
    }
  }
}
