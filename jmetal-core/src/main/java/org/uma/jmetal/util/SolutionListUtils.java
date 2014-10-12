package org.uma.jmetal.util;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 04/10/14.
 */
public class SolutionListUtils {

  public static List<Solution<?>> findNondominatedSolutions(List<Solution<?>> solutionSet) {
    Ranking ranking = new DominanceRanking() ;
    return ranking.computeRanking(solutionSet).getSubfront(0);
  }

  public static int findWorstSolution(List<Solution<?>> solutionList, Comparator<Solution<?>> comparator) {
    if ((solutionList == null) || (solutionList.isEmpty())) {
      return -1;
    }

    int index = 0;
    Solution worstKnown = solutionList.get(0), candidateSolution;
    int flag;
    for (int i = 1; i < solutionList.size(); i++) {
      candidateSolution = solutionList.get(i);
      flag = comparator.compare(worstKnown, candidateSolution);
      if (flag == -1) {
        index = i;
        worstKnown = candidateSolution;
      }
    }

    return index;
  }

  public static double[][] writeObjectivesToMatrix(List<Solution<?>> solutionList) {
    if (solutionList.size() == 0) {
      return new double[0][0];
    }

    int numberOfObjectives = solutionList.get(0).getNumberOfObjectives();
    int solutionListSize = solutionList.size();

    double[][] objectives;
    objectives = new double[solutionListSize][numberOfObjectives];
    for (int i = 0; i < solutionListSize; i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        objectives[i][j] = solutionList.get(i).getObjective(j);
      }
    }
    return objectives;
  }

  /**
   * Gets the maximum values for each objectives in a given list of solutions
   *
   * @param solutionList        The list of solutions
   * @return A list with the maximun values for each objective
   */
  public static List<Double> getMaximumValues(List<Solution<?>> solutionList) {
    List<Double> maximumValue ;
    if ((solutionList == null) || (solutionList.size() == 0)) {
      maximumValue = Collections.EMPTY_LIST ;
    } else {
      int numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;
      maximumValue = new ArrayList<>(numberOfObjectives) ;

      for (int i = 0; i < numberOfObjectives; i++) {
        maximumValue.set(i, Double.MIN_VALUE) ;
      }

      for (Solution<?> solution : solutionList) {
        for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
          if (solution.getObjective(j) > maximumValue.get(j)) {
            maximumValue.set(j, solution.getObjective(j));
          }
        }
      }
    }

    return maximumValue;
  }

  /**
   * Gets the minimum values for each objectives in a given list of solutions
   *
   * @param solutionList        The list of solutions
   * @return A list with the minimum values for each objective
   */
  public static List<Double> getMinimumValues(List<Solution<?>> solutionList) {
    List<Double> minimumValue ;
    if ((solutionList == null) || (solutionList.size() == 0)) {
      minimumValue = Collections.EMPTY_LIST ;
    } else {
      int numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;
      minimumValue = new ArrayList<>(numberOfObjectives) ;

      for (int i = 0; i < numberOfObjectives; i++) {
        minimumValue.set(i, Double.MAX_VALUE) ;
      }

      for (Solution<?> solution : solutionList) {
        for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
          if (solution.getObjective(j) < minimumValue.get(j)) {
            minimumValue.set(j, solution.getObjective(j));
          }
        }
      }
    }

    return minimumValue;
  }

  /**
   * This method receives a list of non-dominated solutions and maximum and mimnimum values of the
   * objecttives, and returns a the normalized set of solutions.
   *
   * @param solutionList A list of non-dominated solutions
   * @param maximumValue The maximum values of the objectives
   * @param minimumValue The minimum values of the objectives
   * @return the normalized list of non-dominated solutions
   */
  public static List<Solution<?>> getNormalizedFront(List<Solution<?>> solutionList,
    List<Double> maximumValue,
    List<Double> minimumValue) {

    List<Solution<?>> normalizedSolutionSet = new ArrayList<>(solutionList.size()) ;

    //double[][] normalizedFront = new double[front.length][];

    int numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;
    for (int i = 0; i < solutionList.size(); i++) {
      Solution<?> solution = solutionList.get(i).copy() ;
      //normalizedFront[i] = new double[front[i].length];
      for (int j = 0; j < numberOfObjectives; j++) {
        double normalizedValue = (solutionList.get(i).getObjective(j) - minimumValue.get(j)) /
          (maximumValue.get(j) - minimumValue.get(j));
       // normalizedFront[i][j] = (front[i][j] - minimumValue[j]) /
         // (maximumValue[j] - minimumValue[j]);
        solution.setObjective(j, normalizedValue);
      }
    }
    return normalizedSolutionSet;
  }

  /**
   * This method receives a normalized list of non-dominated solutions and return the inverted one.
   * This operation is needed for minimization problem
   *
   * @param solutionSet The front to invert
   * @return The inverted front
   */
  public static List<Solution<?>> getInvertedFront(List<Solution<?>> solutionSet) {
    List<Solution<?>> invertedFront = new ArrayList<>(solutionSet.size()) ;
    int numberOfObjectives = solutionSet.get(0).getNumberOfObjectives() ;

    for (int i = 0; i < solutionSet.size(); i++) {
      invertedFront.add(i, solutionSet.get(i).copy()) ;
      for (int j = 0; j < numberOfObjectives; j++) {
        if (solutionSet.get(i).getObjective(j) <= 1.0 &&
          solutionSet.get(i).getObjective(j) >= 0.0) {
          invertedFront.get(i).setObjective(j, 1.0 - solutionSet.get(i).getObjective(j));
        } else if (solutionSet.get(i).getObjective(j) > 1.0) {
          invertedFront.get(i).setObjective(j, 0.0);
        } else if (solutionSet.get(i).getObjective(j) < 0.0) {
          invertedFront.get(i).setObjective(j, 1.0);
        }
      }
    }
    return invertedFront;
  }
}
