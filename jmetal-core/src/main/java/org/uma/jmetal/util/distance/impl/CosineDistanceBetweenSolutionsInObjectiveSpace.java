package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;

/**
 * Class for calculating the cosine distance between two {@link Solution} objects in objective space.
 *
 * @author <antonio@lcc.uma.es>
 */
public class CosineDistanceBetweenSolutionsInObjectiveSpace<S extends Solution<?>>
    implements Distance<S, S> {

  private S referencePoint;

  public CosineDistanceBetweenSolutionsInObjectiveSpace(S referencePoint) {
    this.referencePoint = referencePoint ;
  }

  @Override
  public double compute(S solution1, S solution2) {
    double sum = 0.0 ;
    for (int i = 0; i < solution1.objectives().length; i++) {
      sum += (solution1.objectives()[i] - referencePoint.objectives()[i]) *
          (solution2.objectives()[i] - referencePoint.objectives()[i]);
    }

    double result = sum / (sumOfDistancesToIdealPoint(solution1) * sumOfDistancesToIdealPoint(solution2)) ;

    return result ;
  }

  private double sumOfDistancesToIdealPoint(S solution) {
    double sum = 0.0 ;

    for (int i = 0 ; i < solution.objectives().length; i++) {
      sum += Math.pow(solution.objectives()[i] - referencePoint.objectives()[i], 2.0) ;
    }

    return Math.sqrt(sum) ;
  }
}
