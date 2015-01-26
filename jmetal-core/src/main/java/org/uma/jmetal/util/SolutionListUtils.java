package org.uma.jmetal.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.*;

/**
 * Created by Antonio J. Nebro on 04/10/14.
 */
public class SolutionListUtils {

  public static <S extends Solution> List<S> getNondominatedSolutions(List<S> solutionList) {
    Ranking ranking = new DominanceRanking() ;
    return ranking.computeRanking(solutionList).getSubfront(0);
  }

  public int findWorstSolution(List<? extends Solution> solutionList, Comparator<Solution> comparator) {
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

  /**
   * Finds the index of the best solution in the list according to a comparator
   * @param solutionList
   * @param comparator
   * @return The index of the best solution
   */
  public static int findIndexOfBestSolution(List<? extends Solution> solutionList, Comparator<Solution> comparator) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (solutionList.isEmpty()) {
      throw new JMetalException("The solution list is empty") ;
    } else if (comparator == null) {
      throw new JMetalException("The comparator is null") ;
    }

    int index = 0;
    Solution bestKnown = solutionList.get(0) ;
    Solution candidateSolution ;

    int flag;
    for (int i = 1; i < solutionList.size(); i++) {
      candidateSolution = solutionList.get(i);
      flag = comparator.compare(bestKnown, candidateSolution);
      if (flag == 1) {
        index = i;
        bestKnown = candidateSolution;
      }
    }

    return index;
  }

  public static Solution findBestSolution(List<? extends Solution> solutionList, Comparator<Solution> comparator) {
    return solutionList.get(findIndexOfBestSolution(solutionList, comparator)) ;
  }

  public static <S extends Solution> double[][] writeObjectivesToMatrix(List<S> solutionList) {
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
   * @return A list with the maximum values for each objective
   */
  public static List<Double> getMaximumValues(List<Solution> solutionList) {
    List<Double> maximumValue ;
    if ((solutionList == null) || (solutionList.size() == 0)) {
      maximumValue = Collections.EMPTY_LIST ;
    } else {
      int numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;
      maximumValue = new ArrayList<>(numberOfObjectives) ;

      for (int i = 0; i < numberOfObjectives; i++) {
        maximumValue.set(i, Double.MIN_VALUE) ;
      }

      for (Solution solution : solutionList) {
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
  public static List<Double> getMinimumValues(List<Solution> solutionList) {
    List<Double> minimumValue ;
    if ((solutionList == null) || (solutionList.size() == 0)) {
      minimumValue = Collections.EMPTY_LIST ;
    } else {
      int numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;
      minimumValue = new ArrayList<>(numberOfObjectives) ;

      for (int i = 0; i < numberOfObjectives; i++) {
        minimumValue.set(i, Double.MAX_VALUE) ;
      }

      for (Solution solution : solutionList) {
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
   * This method receives a list of non-dominated solutions and maximum and minimum values of the
   * objectives, and returns a the normalized set of solutions.
   *
   * @param solutionList A list of non-dominated solutions
   * @param maximumValue The maximum values of the objectives
   * @param minimumValue The minimum values of the objectives
   * @return the normalized list of non-dominated solutions
   */
  public static List<Solution> getNormalizedFront(List<Solution> solutionList,
    List<Double> maximumValue,
    List<Double> minimumValue) {

    List<Solution> normalizedSolutionSet = new ArrayList<>(solutionList.size()) ;

    int numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;
    for (int i = 0; i < solutionList.size(); i++) {
      Solution<?> solution = solutionList.get(i).copy() ;
      for (int j = 0; j < numberOfObjectives; j++) {
        double normalizedValue = (solutionList.get(i).getObjective(j) - minimumValue.get(j)) /
          (maximumValue.get(j) - minimumValue.get(j));
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
  public static <S extends Solution> List<S> getInvertedFront(List<S> solutionSet) {
    List<S> invertedFront = new ArrayList<>(solutionSet.size()) ;
    int numberOfObjectives = solutionSet.get(0).getNumberOfObjectives() ;

    for (int i = 0; i < solutionSet.size(); i++) {
      invertedFront.add(i, (S) solutionSet.get(i).copy()) ;
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

  public static <S extends Solution> boolean isSolutionDominatedBySolutionList(S solution, List<S> solutionSet) {
    boolean result = false ;
    Comparator<Solution> dominance = new DominanceComparator() ;

    int i = 0 ;

    while (!result && (i < solutionSet.size())) {
      if (dominance.compare(solution, solutionSet.get(i)) == 1) {
        result = true ;
      }
      i++ ;
    }

    return result ;
  }

  /**
   * This method receives a normalized list of non-dominated solutions and return the inverted one.
   * This operation is needed for minimization problem
   *
   * @param solutionList The front to invert
   * @return The inverted front
   */
  public static <S extends Solution> List<S> selectNRandomDifferentSolutions(
      int numberOfSolutionsToBeReturned, List<S> solutionList) {
    if (null == solutionList) {
      throw new JMetalException("The solution list is null") ;
    } else if (solutionList.size() == 0) {
      throw new JMetalException("The solution list is empty") ;
    } else if (solutionList.size() < numberOfSolutionsToBeReturned) {
      throw new JMetalException("The solution list size (" + solutionList.size() +") is less than "
          + "the number of requested solutions ("+numberOfSolutionsToBeReturned+")") ;
    }

    JMetalRandom randomGenerator = JMetalRandom.getInstance() ;
    List<S> resultList = new ArrayList<>(numberOfSolutionsToBeReturned);

    if (solutionList.size() == 1) {
      resultList.add(solutionList.get(0));
    } else {
      Collection<Integer> positions = new HashSet<>(numberOfSolutionsToBeReturned);
      while (positions.size() < numberOfSolutionsToBeReturned) {
        int nextPosition = randomGenerator.nextInt(0, solutionList.size() - 1);
        if (!positions.contains(nextPosition)) {
          positions.add(nextPosition);
          resultList.add(solutionList.get(nextPosition));
        }
      }
    }

    return resultList ;
  }
}
