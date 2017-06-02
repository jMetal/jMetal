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
  public double getDistance(S solution1, S solution2) {
    double sum = 0.0 ;
    for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
      sum += (solution1.getObjective(i) - referencePoint.getObjective(i)) *
          (solution2.getObjective(i) - referencePoint.getObjective(i));
    }

    double result = sum / (sumOfDistancesToIdealPoint(solution1) * sumOfDistancesToIdealPoint(solution2)) ;

    return result ;
  }

  private double sumOfDistancesToIdealPoint(S solution) {
    double sum = 0.0 ;

    for (int i = 0 ; i < solution.getNumberOfObjectives(); i++) {
      sum += Math.pow(solution.getObjective(i) - referencePoint.getObjective(i), 2.0) ;
    }

    return Math.sqrt(sum) ;
  }
}
