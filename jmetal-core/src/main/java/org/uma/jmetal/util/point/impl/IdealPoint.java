package org.uma.jmetal.util.point.impl;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

/**d
 * Class representing an ideal point (minimization is assumed)
 *
 * @author Antonio J.Nebro 
 */
public class IdealPoint extends ArrayPoint {

  public IdealPoint(int dimension) {
    super(dimension) ;
    Arrays.fill(point, Double.POSITIVE_INFINITY);
  }

  @Override
  public void update(double[] point) {
    Check.that(point.length == this.point.length, () -> "The point to update with has a dimension of " + point.length + " "
            + "while this point has a dimension of " + this.point.length);

    for (int i = 0; i < point.length; i++) {
      if (this.point[i] > point[i]) {
        this.point[i] = point[i];
      }
    }
  }

  public void update(List<? extends Solution<?>> solutionList) {
    for (Solution<?> solution : solutionList) {
      update(solution.objectives()) ;
    }
  }
}
