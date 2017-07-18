package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;

import java.util.List;

/**
 * Class for calculating the Euclidean distance between a {@link Solution} object a list of {@link Solution}
 * objects in objective space.
 *
 * @author <antonio@lcc.uma.es>
 */
public class EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace<S extends Solution<Double>, L extends List<S>>
    implements Distance<S, L> {

  private EuclideanDistanceBetweenSolutionsInObjectiveSpace<S> distance ;

  public EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace() {
    distance = new EuclideanDistanceBetweenSolutionsInObjectiveSpace<S>() ;
  }

  @Override
  public double getDistance(S solution, L solutionList) {
    double bestDistance = Double.MAX_VALUE;

    for (int i = 0; i < solutionList.size();i++){
      double aux = distance.getDistance(solution, solutionList.get(i));
      if (aux < bestDistance)
        bestDistance = aux;
    }

    return bestDistance;
  }
}
