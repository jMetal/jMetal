package org.uma.jmetal.util.distance.impl;

import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;

/**
 * Class for calculating the Euclidean distance between a {@link Solution} object a list of {@link Solution}
 * objects in objective space.
 *
 * @author <antonio@lcc.uma.es>
 */
public class EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace
        <S extends Solution<?>, L extends List<S>>
        implements Distance<S, L> {

  private EuclideanDistanceBetweenVectors distance ;

  public EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace() {
    distance = new EuclideanDistanceBetweenVectors() ;
  }

  @Override
  public double compute(S solution, L solutionList) {
    var bestDistance = Double.MAX_VALUE;

    for (var s : solutionList) {
      var aux = distance.compute(solution.objectives(), s.objectives());
      if (aux < bestDistance)
        bestDistance = aux;
    }

    return bestDistance;
  }
}
