package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.util.comparator.LexicographicalPointComparator;
import org.uma.jmetal.util.point.util.distance.EuclideanDistance;
import org.uma.jmetal.util.point.util.distance.PointDistance;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class implements a fitness quality indicator for single objective metaheuristics.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class Fitness<S extends Solution<?>> extends GenericIndicator<S> {

  /** Default constructor */
  public Fitness() {}

  /**
   * Evaluate() method
   *
   * @param solutionList
   * @return
   */
  @Override
  public Double evaluate(List<S> solutionList) {
    solutionList.sort(new ObjectiveComparator<>(0));

    return solutionList.get(0).getObjective(0);
  }

  @Override
  public String getName() {
    return "Fitness";
  }

  @Override
  public String getDescription() {
    return "Single objective fitness quality indicator";
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true;
  }
}
