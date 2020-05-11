package org.uma.jmetal.util;

import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl.DoubleValueAttributeComparator;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.*;
import java.util.stream.IntStream;

/** @author Antonio J. Nebro */
public class SolutionListUtils {

  public static <S extends Solution<?>> List<S> getNonDominatedSolutions(List<S> solutionList) {
    Ranking<S> ranking = new FastNonDominatedSortRanking<>();
    return ranking.computeRanking(solutionList).getSubFront(0);
  }

  public <S> S findWorstSolution(Collection<S> solutionList, Comparator<S> comparator) {
    if ((solutionList == null) || (solutionList.isEmpty())) {
      throw new IllegalArgumentException("No solution provided: " + solutionList);
    }

    S worstKnown = solutionList.iterator().next();
    for (S candidateSolution : solutionList) {
      if (comparator.compare(worstKnown, candidateSolution) < 0) {
        worstKnown = candidateSolution;
      }
    }

    return worstKnown;
  }

  /**
   * Finds the index of the best solution in the list according to a comparator
   *
   * @param solutionList
   * @param comparator
   * @return The index of the best solution
   */
  public static <S> int findIndexOfBestSolution(List<S> solutionList, Comparator<S> comparator) {
    Check.isNotNull(solutionList);
    Check.isNotNull(comparator);
    Check.collectionIsNotEmpty(solutionList);

    int index = 0;
    S bestKnown = solutionList.get(0);
    S candidateSolution;

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

  /**
   * Finds the index of the worst solution in the list according to a comparator
   *
   * @param solutionList
   * @param comparator
   * @return The index of the best solution
   */
  public static <S> int findIndexOfWorstSolution(
      List<? extends S> solutionList, Comparator<S> comparator) {
    Check.isNotNull(solutionList);
    Check.isNotNull(comparator);
    Check.collectionIsNotEmpty(solutionList);

    int index = 0;
    S worstKnown = solutionList.get(0);
    S candidateSolution;

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

  public static <S> S findBestSolution(List<S> solutionList, Comparator<S> comparator) {
    return solutionList.get(findIndexOfBestSolution(solutionList, comparator));
  }

  public static <S extends Solution<?>> double[][] writeObjectivesToMatrix(List<S> solutionList) {
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
   * This method receives a list of non-dominated solutions and maximum and minimum values of the
   * objectives, and returns a normalized set of solutions.
   *
   * @param solutions A list of non-dominated solutions
   * @param maxValues The maximum values of the objectives
   * @param minValues The minimum values of the objectives
   * @return the normalized list of non-dominated solutions
   */
  public static <S extends Solution<?>> List<S> normalizeSolutionList(
      List<S> solutions, double[] minValues, double[] maxValues) {
    List<S> normalizedSolutions = new ArrayList<>(solutions.size());

    for (S solution : solutions) {
      normalizedSolutions.add(SolutionUtils.normalize(solution, minValues, maxValues));
    }

    return normalizedSolutions;
  }

  /**
   * This method receives a list of non-dominated solutions and maximum and minimum values of the
   * objectives, and returns a normalized set of solutions.
   *
   * @param solutions A list of non-dominated solutions
   * @return the normalized list of non-dominated solutions
   */
  public static <S extends Solution<?>> List<S> normalizeSolutionList(List<S> solutions) {
    Check.isNotNull(solutions);
    Check.collectionIsNotEmpty(solutions);

    double[] minValues = new double[solutions.get(0).getNumberOfObjectives()];
    double[] maxValues = new double[solutions.get(0).getNumberOfObjectives()];

    for (int i = 0; i < minValues.length; i++) {
      int best = findIndexOfBestSolution(solutions, new ObjectiveComparator<>(i));
      int worst = findIndexOfWorstSolution(solutions, new ObjectiveComparator<>(i));
      minValues[i] = solutions.get(best).getObjective(i);
      maxValues[i] = solutions.get(worst).getObjective(i);
    }

    return normalizeSolutionList(solutions, minValues, maxValues);
  }

  /**
   * This method receives a normalized list of non-dominated solutions and return the inverted one.
   * This operation is needed for minimization problem
   *
   * @param solutionSet The front to invert
   * @return The inverted front
   */
  @SuppressWarnings("unchecked")
  public static <S extends Solution<?>> List<S> getInvertedFront(List<S> solutionSet) {
    List<S> invertedFront = new ArrayList<>(solutionSet.size());
    int numberOfObjectives = solutionSet.get(0).getNumberOfObjectives();

    for (int i = 0; i < solutionSet.size(); i++) {
      invertedFront.add(i, (S) solutionSet.get(i).copy());
      for (int j = 0; j < numberOfObjectives; j++) {
        if (solutionSet.get(i).getObjective(j) <= 1.0
            && solutionSet.get(i).getObjective(j) >= 0.0) {
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

  public static <S extends Solution<?>> boolean isSolutionDominatedBySolutionList(
      S solution, List<? extends S> solutionSet) {
    boolean result = false;
    Comparator<S> dominance = new DominanceComparator<S>();

    int i = 0;

    while (!result && (i < solutionSet.size())) {
      if (dominance.compare(solution, solutionSet.get(i)) == 1) {
        result = true;
      }
      i++;
    }

    return result;
  }

  /**
   * This method receives a normalized list of non-dominated solutions and return the inverted one.
   * This operation is needed for minimization problem
   *
   * @param solutionList The front to invert
   * @return The inverted front
   */
  public static <S> List<S> selectNRandomDifferentSolutions(
      int numberOfSolutionsToBeReturned, List<S> solutionList) {
    JMetalRandom random = JMetalRandom.getInstance();
    return selectNRandomDifferentSolutions(
        numberOfSolutionsToBeReturned, solutionList, (low, up) -> random.nextInt(low, up));
  }

  /**
   * This method receives a normalized list of non-dominated solutions and return the inverted one.
   * This operation is needed for minimization problem
   *
   * @param solutionList The front to invert
   * @param randomGenerator The random generator to use
   * @return The inverted front
   */
  public static <S> List<S> selectNRandomDifferentSolutions(
      int numberOfSolutionsToBeReturned,
      List<S> solutionList,
      BoundedRandomGenerator<Integer> randomGenerator) {
    Check.isNotNull(solutionList);
    Check.collectionIsNotEmpty(solutionList);
    Check.that(
        solutionList.size() >= numberOfSolutionsToBeReturned,
        "The solution list size ("
            + solutionList.size()
            + ") is less than "
            + "the number of requested solutions ("
            + numberOfSolutionsToBeReturned
            + ")");

    List<S> resultList = new ArrayList<>(numberOfSolutionsToBeReturned);

    if (solutionList.size() == 1) {
      resultList.add(solutionList.get(0));
    } else {
      Collection<Integer> positions = new HashSet<>(numberOfSolutionsToBeReturned);
      while (positions.size() < numberOfSolutionsToBeReturned) {
        int nextPosition = randomGenerator.getRandomValue(0, solutionList.size() - 1);
        if (!positions.contains(nextPosition)) {
          positions.add(nextPosition);
          resultList.add(solutionList.get(nextPosition));
        }
      }
    }

    return resultList;
  }

  /**
   * Returns a matrix with the euclidean distance between each pair of solutions in the population.
   * Distances are measured in the objective space
   *
   * @param solutionSet
   * @return
   */
  public static <S extends Solution<?>> double[][] distanceMatrix(List<S> solutionSet) {
    double[][] distance = new double[solutionSet.size()][solutionSet.size()];
    for (int i = 0; i < solutionSet.size(); i++) {
      distance[i][i] = 0.0;
      for (int j = i + 1; j < solutionSet.size(); j++) {
        distance[i][j] =
            SolutionUtils.distanceBetweenObjectives(solutionSet.get(i), solutionSet.get(j));
        distance[j][i] = distance[i][j];
      }
    }
    return distance;
  }

  public static <S extends Solution<?>> double[][] normalizedDistanceMatrix(
      List<S> solutionSet, double maxs[], double mins[]) {
    double[][] distance = new double[solutionSet.size()][solutionSet.size()];
    for (int i = 0; i < solutionSet.size(); i++) {
      distance[i][i] = 0.0;
      for (int j = i + 1; j < solutionSet.size(); j++) {
        distance[i][j] =
            SolutionUtils.normalizedDistanceBetweenObjectives(
                solutionSet.get(i), solutionSet.get(j), maxs, mins);
        distance[j][i] = distance[i][j];
      }
    }
    return distance;
  }
  /**
   * Compares two solution lists to determine if both are equals
   *
   * @param solutionList A <code>Solution list</code>
   * @param newSolutionList A <code>Solution list</code>
   * @return true if both are contains the same solutions, false in other case
   */
  public static <S> boolean solutionListsAreEquals(List<S> solutionList, List<S> newSolutionList) {
    boolean found;
    for (int i = 0; i < solutionList.size(); i++) {

      int j = 0;
      found = false;
      while (j < newSolutionList.size()) {
        if (solutionList.get(i).equals(newSolutionList.get(j))) {
          found = true;
        }
        j++;
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

  /**
   * This methods takes a list of solutions, removes a percentage of its solutions, and it is filled
   * with new random generated solutions
   *
   * @param solutionList
   * @param problem
   * @param percentageOfSolutionsToRemove
   */
  public static <S> void restart(
      List<S> solutionList, Problem<S> problem, int percentageOfSolutionsToRemove) {
    Check.isNotNull(solutionList);
    Check.collectionIsNotEmpty(solutionList);
    Check.that(
        (percentageOfSolutionsToRemove >= 0) && (percentageOfSolutionsToRemove <= 100),
        "The percentage of solutions to remove is invalid: " + percentageOfSolutionsToRemove);

    int solutionListOriginalSize = solutionList.size();
    int numberOfSolutionsToRemove =
        (int) (solutionListOriginalSize * percentageOfSolutionsToRemove / 100.0);

    removeSolutionsFromList(solutionList, numberOfSolutionsToRemove);
    fillPopulationWithNewSolutions(solutionList, problem, solutionListOriginalSize);
  }

  /**
   * Removes a number of solutions from a list
   *
   * @param solutionList The list of solutions
   * @param numberOfSolutionsToRemove
   */
  public static <S> void removeSolutionsFromList(
      List<S> solutionList, int numberOfSolutionsToRemove) {
    if (solutionList.size() < numberOfSolutionsToRemove) {
      throw new JMetalException(
          "The list size ("
              + solutionList.size()
              + ") is lower than "
              + "the number of solutions to remove ("
              + numberOfSolutionsToRemove
              + ")");
    }

    for (int i = 0; i < numberOfSolutionsToRemove; i++) {
      solutionList.remove(0);
    }
  }

  /**
   * Fills a population with new solutions until its size is maxListSize
   *
   * @param solutionList The list of solutions
   * @param problem The problem being solved
   * @param maxListSize The target size of the list
   * @param <S> The type of the solutions to be created
   */
  public static <S> void fillPopulationWithNewSolutions(
      List<S> solutionList, Problem<S> problem, int maxListSize) {
    while (solutionList.size() < maxListSize) {
      solutionList.add(problem.createSolution());
    }
  }

  /**
   * Given a solution list and the identifier of an objective (0, 1, etc), returns an array with the
   * values of that objective in all the solutions of the list
   *
   * @param solutionList
   * @param objective
   * @param <S>
   * @return
   */
  public static <S extends Solution<?>> double[] getObjectiveArrayFromSolutionList(
      List<S> solutionList, int objective) {
    double[] result = new double[solutionList.size()];

    for (int i = 0; i < solutionList.size(); i++) {
      result[i] = solutionList.get(i).getObjective(objective);
    }
    return result;
  }

  /**
   * Method implements a version of the distance-based subset selection algorithm described in: H.
   * K. Singh, K. S. Bhattacharjee, and T. Ray, ‘Distance-based subset selection for benchmarking in
   * evolutionary multi/many-objective optimization,’ IEEE Trans. on Evolutionary Computation,
   * 23(5), 904-912, (2019). DOI: https://doi.org/10.1109/TEVC.2018.2883094
   *
   * @param originalSolutionList
   * @param finalListSize The size of the result list
   * @param <S>
   * @return
   */
  public static <S extends Solution<?>> List<S> distanceBasedSubsetSelection(
          List<S> originalSolutionList, int finalListSize) {
    Check.isNotNull(originalSolutionList);
    Check.collectionIsNotEmpty(originalSolutionList);

    if (originalSolutionList.size() <= finalListSize) {
      return originalSolutionList;
    }

    if (originalSolutionList.get(0).getNumberOfObjectives() == 2) {
      Archive<S> archive = new CrowdingDistanceArchive<>(finalListSize) ;
      originalSolutionList.forEach(solution -> archive.add(solution));

      return archive.getSolutionList() ;
    }

    for (int i = 0; i < originalSolutionList.size(); i++) {
      originalSolutionList.get(i).setAttribute("INDEX_", i);
    }

    // STEP 1. Normalize the objectives values of the solution list
    List<S> solutions = new ArrayList<>();
    solutions.addAll(normalizeSolutionList(originalSolutionList));

    // STEP 2. Find the solution having the best objective value, being the objective randomly
    // selected
    int randomObjective =
            JMetalRandom.getInstance().nextInt(0, solutions.get(0).getNumberOfObjectives() - 1);

    int bestSolutionIndex =
            findIndexOfBestSolution(solutions, new ObjectiveComparator<>(randomObjective));

    //  STEP 3. Add the solution to the current list of selected solutions and remove it from the original list
    List<S> selectedSolutions = new ArrayList<>(finalListSize);
    selectedSolutions.add(solutions.get(bestSolutionIndex));
    solutions.remove(bestSolutionIndex);

    // STEP 4. Find the solution having the largest distance to the selected solutions
    Distance<S, List<S>> distance =
            new EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace<>();
    while (selectedSolutions.size() < finalListSize) {
      for (S solution : solutions) {
        solution.setAttribute(
                "SUBSET_SELECTION_DISTANCE", distance.compute(solution, selectedSolutions));
      }
      int largestDistanceSolutionIndex =
              findIndexOfBestSolution(
                      solutions,
                      new DoubleValueAttributeComparator<>(
                              "SUBSET_SELECTION_DISTANCE", AttributeComparator.Ordering.DESCENDING));
      selectedSolutions.add(solutions.get(largestDistanceSolutionIndex));
      solutions.remove(largestDistanceSolutionIndex);
    }

    List<S> resultList = new ArrayList<>();
    for (S solution : selectedSolutions) {
      resultList.add(originalSolutionList.get((int) solution.getAttribute("INDEX_")));
    }

    return resultList;
  }

  /**
   * Given a list of solutions, returns a matrix with the objective values of all the solutions
   * @param solutionList
   * @param <S>
   * @return
   */
  public static <S extends Solution<?>> double[][] getMatrixWithObjectiveValues(List<S> solutionList) {
    double[][] matrix = new double[solutionList.size()][] ;

    IntStream.range(0, solutionList.size()).forEach(i -> matrix[i] = solutionList.get(i).getObjectives()) ;

    return matrix ;
  }
}
