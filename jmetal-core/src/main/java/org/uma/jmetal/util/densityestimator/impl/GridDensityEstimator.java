package org.uma.jmetal.util.densityestimator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AdaptiveGrid;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.Comparator;
import java.util.List;

/**
 * This class implements the a density estimator based on the adaptive grid scheme used in the PAES algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GridDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {
  private final String attributeId = getClass().getName();
  private AdaptiveGrid<S> grid;


  public GridDensityEstimator(int bisections, int numberOfObjectives) {
    grid = new AdaptiveGrid<>(bisections, numberOfObjectives) ;
  }

  /**
   * Assigns the KNN distance to all the solutions in a list
   *
   * @param solutionList
   */
  @Override
  public void compute(List<S> solutionList) {
    int size = solutionList.size();

    if (size == 0) {
      return;
    }

    if (size == 1) {
      solutionList.get(0).attributes().put(attributeId, Double.MAX_VALUE);
    }

    grid.updateGrid(solutionList);

    for (S solution : solutionList) {
      solution.attributes().put(attributeId, grid.getLocationDensity(grid.location(solution)));
    }
  }

  @Override
  public Double getValue(S solution) {
    Check.notNull(solution);

    int result = 0 ;
    if (solution.attributes().get(attributeId) != null) {
      result = (Integer) solution.attributes().get(attributeId) ;
    }
    return result * 1.0 ;
  }

  @Override
  public Comparator<S> getComparator() {
    return Comparator.comparing(this::getValue) ;
  }
}
