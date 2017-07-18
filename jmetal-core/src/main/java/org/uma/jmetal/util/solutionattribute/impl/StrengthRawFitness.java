package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("serial")
public class StrengthRawFitness <S extends Solution<?>>
    extends GenericSolutionAttribute<S, Double> implements DensityEstimator<S>{
  private static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator<Solution<?>>();

  @Override
  public void computeDensityEstimator(List<S> solutionSet) {
    double [][] distance = SolutionListUtils.distanceMatrix(solutionSet);
    double []   strength    = new double[solutionSet.size()];
    double []   rawFitness  = new double[solutionSet.size()];
    double kDistance                                          ;

    // strength(i) = |{j | j <- SolutionSet and i dominate j}|
    for (int i = 0; i < solutionSet.size(); i++) {
      for (int j = 0; j < solutionSet.size();j++) {
        if (DOMINANCE_COMPARATOR.compare(solutionSet.get(i),solutionSet.get(j))==-1) {
          strength[i] += 1.0;
        }
      }
    }

    //Calculate the raw fitness
    // rawFitness(i) = |{sum strenght(j) | j <- SolutionSet and j dominate i}|
    for (int i = 0;i < solutionSet.size(); i++) {
      for (int j = 0; j < solutionSet.size();j++) {
        if (DOMINANCE_COMPARATOR.compare(solutionSet.get(i),solutionSet.get(j))==1) {
          rawFitness[i] += strength[j];
        }
      }
    }

    // Add the distance to the k-th individual. In the reference paper of SPEA2,
    // k = sqrt(population.size()), but a value of k = 1 is recommended. See
    // http://www.tik.ee.ethz.ch/pisa/selectors/spea2/spea2_documentation.txt
    int k = 1 ;
    for (int i = 0; i < distance.length; i++) {
      Arrays.sort(distance[i]);
      kDistance = 1.0 / (distance[i][k] + 2.0);
      solutionSet.get(i).setAttribute(getAttributeIdentifier(), rawFitness[i] + kDistance);
    }
  }
}
