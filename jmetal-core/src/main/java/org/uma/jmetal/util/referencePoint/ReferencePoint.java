package org.uma.jmetal.util.referencePoint;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.point.util.PointSolution;

/**
 * Interface representing a reference point
 *
 * @author <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class ReferencePoint extends PointSolution {
  public ReferencePoint(int numberOfObjectives) {
    super(numberOfObjectives);
  }

  public abstract void update(Solution<?> solution) ;
}
