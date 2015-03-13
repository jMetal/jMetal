package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class StrengthRawFitness <S extends Solution>
    extends GenericSolutionAttribute<S, Double> implements DensityEstimator<S>{
  private static final Comparator<Solution> DOMINANCE_COMPARATOR = new DominanceComparator();

  private double distanceBetweenObjectives(S firstSolution, S secondSolution) {
    double diff;
    double distance = 0.0;
    //euclidean distance
    for (int nObj = 0; nObj < firstSolution.getNumberOfObjectives();nObj++){
      diff = firstSolution.getObjective(nObj) - secondSolution.getObjective(nObj);
      distance += Math.pow(diff,2.0);
    } // for

    return Math.sqrt(distance);
  }

  private double [][] distanceMatrix(List<S> solutionSet) {
    //The matrix of distances
    double [][] distance = new double [solutionSet.size()][solutionSet.size()];
    for (int i = 0; i < solutionSet.size(); i++){
      distance[i][i] = 0.0;
      for (int j = i + 1; j < solutionSet.size(); j++){
        distance[i][j] = distanceBetweenObjectives(solutionSet.get(i),solutionSet.get(j));
        distance[j][i] = distance[i][j];
      } // for
    } // for
    return distance;
  } // distanceMatrix


  @Override
  public void computeDensityEstimator(List<S> solutionSet) {
    double [][] distance = this.distanceMatrix(solutionSet);
    double []   strength    = new double[solutionSet.size()];
    double []   rawFitness  = new double[solutionSet.size()];
    double kDistance                                          ;

    // strength(i) = |{j | j <- SolutionSet and i dominate j}|
    for (int i = 0; i < solutionSet.size(); i++) {
      for (int j = 0; j < solutionSet.size();j++) {
        if (DOMINANCE_COMPARATOR.compare(solutionSet.get(i),solutionSet.get(j))==-1) {
          strength[i] += 1.0;
        } // if
      } // for
    } // for


    //Calculate the raw fitness
    // rawFitness(i) = |{sum strenght(j) | j <- SolutionSet and j dominate i}|
    for (int i = 0;i < solutionSet.size(); i++) {
      for (int j = 0; j < solutionSet.size();j++) {
        if (DOMINANCE_COMPARATOR.compare(solutionSet.get(i),solutionSet.get(j))==1) {
          rawFitness[i] += strength[j];
        } // if
      } // for
    } // for


    // Add the distance to the k-th individual. In the reference paper of SPEA2,
    // k = sqrt(population.size()), but a value of k = 1 recommended. See
    // http://www.tik.ee.ethz.ch/pisa/selectors/spea2/spea2_documentation.txt
    int k = 1 ;
    for (int i = 0; i < distance.length; i++) {
      Arrays.sort(distance[i]);
      kDistance = 1.0 / (distance[i][k] + 2.0);
      solutionSet.get(i).setAttribute(getAttributeID(), rawFitness[i] + kDistance);
    } // for
  } // fitnessAsign
}
