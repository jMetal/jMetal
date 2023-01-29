package org.uma.jmetal.util.densityestimator.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements the density estimator based on the distance to the k-th nearest solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StrenghtRawFitnessDensityEstimator<S extends Solution<?>>
    implements DensityEstimator<S> {
  private final String attributeId = getClass().getName();
  private int k;

  private static final Comparator<Solution<?>> DOMINANCE_COMPARATOR =
      new DominanceWithConstraintsComparator<Solution<?>>();

  public StrenghtRawFitnessDensityEstimator(int k) {
    this.k = k;
  }

  /**
   * Assigns the KNN distance to all the solutions in a list
   *
   * @param solutionList
   */
  @Override
  public void compute(List<S> solutionList) {
    double[][] distance = SolutionListUtils.distanceMatrix(solutionList);
    double[] strength = new double[solutionList.size()];
    double[] rawFitness = new double[solutionList.size()];
    double kDistance;

    // strength(i) = |{j | j <- SolutionSet and i dominate j}|
    for (int i = 0; i < solutionList.size(); i++) {
      for (S solution : solutionList) {
        if (DOMINANCE_COMPARATOR.compare(solutionList.get(i), solution) == -1) {
          strength[i] += 1.0;
        }
      }
    }

    // Calculate the raw fitness
    // rawFitness(i) = |{sum strenght(j) | j <- SolutionSet and j dominate i}|
    for (int i = 0; i < solutionList.size(); i++) {
      for (int j = 0; j < solutionList.size(); j++) {
        if (DOMINANCE_COMPARATOR.compare(solutionList.get(i), solutionList.get(j)) == 1) {
          rawFitness[i] += strength[j];
        }
      }
    }

    // Add the distance to the k-th individual. In the reference paper of SPEA2,
    // k = sqrt(population.size()), but a value of k = 1 is recommended. See
    // http://www.tik.ee.ethz.ch/pisa/selectors/spea2/spea2_documentation.txt
    for (int i = 0; i < distance.length; i++) {
      Arrays.sort(distance[i]);
      kDistance = 1.0 / (distance[i][k] + 2.0);
      solutionList.get(i).attributes().put(attributeId, rawFitness[i] + kDistance);
    }
  }

  @Override
  public Double value(S solution) {
    Check.notNull(solution);

    Double result = 0.0;
    if (solution.attributes().get(attributeId) != null) {
      result = (Double) solution.attributes().get(attributeId);
    }
    return result;
  }

  @Override
  public Comparator<S> comparator() {
    return Comparator.comparing(this::value);
  }
}
