package org.uma.jmetal.qualityindicator.impl;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;

import java.util.List;

/**
 * Set coverage metric
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
@SuppressWarnings("serial")
public class SetCoverage
    extends SimpleDescribedEntity
    implements QualityIndicator<Pair<List<? extends Solution<?>>, List<? extends Solution<?>>>, Pair<Double, Double>> {

  /**
   * Constructor
   */
  public SetCoverage() {
    super("SC", "Set coverage") ;
  }

  @Override
  public Pair<Double, Double> evaluate(
      Pair<List<? extends Solution<?>>, List<? extends Solution<?>>> pairOfSolutionLists) {
    List<? extends Solution<?>> front1 = pairOfSolutionLists.getLeft() ;
    List<? extends Solution<?>> front2 = pairOfSolutionLists.getRight() ;

    if (front1 == null) {
      throw new JMetalException("The first front is null") ;
    } else if (front2 == null) {
      throw new JMetalException("The second front is null");
    }

    return new ImmutablePair<>(evaluate(front1, front2), evaluate(front2, front1));
  }

  /**
   * Calculates the set coverage of set1 over set2
   * @param set1
   * @param set2
   * @return The value of the set coverage
   */
  public double evaluate(List<? extends Solution<?>> set1, List<? extends Solution<?>> set2) {
    double result ;
    int sum = 0 ;

    if (set2.size()==0) {
      if (set1.size()==0) {
        result = 0.0 ;
      } else {
        result = 1.0 ;
      }
    } else {
      for (Solution<?> solution : set2) {
        if (SolutionListUtils.isSolutionDominatedBySolutionList(solution, set1)) {
          sum++;
        }
      }
      result = (double)sum/set2.size() ;
    }
    return result ;
  }

  @Override public String getName() {
    return super.getName() ;
  }
}
