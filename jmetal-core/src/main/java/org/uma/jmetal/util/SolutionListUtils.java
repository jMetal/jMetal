package org.uma.jmetal.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl.DoubleValueAttributeComparator;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/** @author Antonio J. Nebro */
public class SolutionListUtils {

  public static <S extends Solution<?>> List<S> getNonDominatedSolutions(List<S> solutionList) {
    Archive<S> nonDominatedSolutionArchive = new NonDominatedSolutionListArchive<>() ;
      for (S s : solutionList) {
          nonDominatedSolutionArchive.add(s);
      }

      return nonDominatedSolutionArchive.getSolutionList();
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
    Check.notNull(solutionList);
    Check.notNull(comparator);
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
    Check.notNull(solutionList);
    Check.notNull(comparator);
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
    if (solutionList.isEmpty()) {
      return new double[0][0];
    }

    int numberOfObjectives = solutionList.get(0).objectives().length;
    int solutionListSize = solutionList.size();

    double[][] objectives;
    objectives = new double[solutionListSize][numberOfObjectives];
    for (int i = 0; i < solutionListSize; i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        objectives[i][j] = solutionList.get(i).objectives()[j];
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
          S normalize = SolutionUtils.normalize(solution, minValues, maxValues);
          normalizedSolutions.add(normalize);
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
    Check.notNull(solutions);
    Check.collectionIsNotEmpty(solutions);

    double[] minValues = new double[solutions.get(0).objectives().length];
    double[] maxValues = new double[solutions.get(0).objectives().length];

    for (int i = 0; i < minValues.length; i++) {
      int best = findIndexOfBestSolution(solutions, new ObjectiveComparator<>(i));
      int worst = findIndexOfWorstSolution(solutions, new ObjectiveComparator<>(i));
      minValues[i] = solutions.get(best).objectives()[i];
      maxValues[i] = solutions.get(worst).objectives()[i];
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
    int numberOfObjectives = solutionSet.get(0).objectives().length;

    for (int i = 0; i < solutionSet.size(); i++) {
      invertedFront.add(i, (S) solutionSet.get(i).copy());
      for (int j = 0; j < numberOfObjectives; j++) {
        if (solutionSet.get(i).objectives()[j] <= 1.0
            && solutionSet.get(i).objectives()[j] >= 0.0) {
          invertedFront.get(i).objectives()[j] = 1.0 - solutionSet.get(i).objectives()[j];
        } else if (solutionSet.get(i).objectives()[j] > 1.0) {
          invertedFront.get(i).objectives()[j] = 0.0;
        } else if (solutionSet.get(i).objectives()[j] < 0.0) {
          invertedFront.get(i).objectives()[j] = 1.0;
        }
      }
    }
    return invertedFront;
  }

  public static <S extends Solution<?>> boolean isSolutionDominatedBySolutionList(
      S solution, List<? extends S> solutionSet) {
    boolean result = false;
    Comparator<S> dominance = new DominanceWithConstraintsComparator<>();

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
        numberOfSolutionsToBeReturned, solutionList, random::nextInt);
  }

  /**
   * This method selects N random different {@link Solution} objects from a list
   *
   * @param solutionList The list
   * @param randomGenerator The random number generator
   * @return The selected solutions
   */
  public static <S> List<S> selectNRandomDifferentSolutions(
      int numberOfSolutionsToBeReturned,
      List<S> solutionList,
      BoundedRandomGenerator<Integer> randomGenerator) {
    Check.notNull(solutionList);
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
      List<S> solutionSet, double[] maxs, double[] mins) {
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
   * @param solutions A <code>Solution list</code>
   * @param newSolutionList A <code>Solution list</code>
   * @return true if both are contains the same solutions, false in other case
   */
  public static <S> boolean solutionListsAreEquals(List<S> solutions, List<S> newSolutionList) {
    boolean found;
    for (S solution : solutions) {

      int j = 0;
      found = false;
      while (j < newSolutionList.size()) {
        if (solution.equals(newSolutionList.get(j))) {
          found = true;
          break;
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
    Check.notNull(solutionList);
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

    if (numberOfSolutionsToRemove > 0) {
      solutionList.subList(0, numberOfSolutionsToRemove).clear();
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
      double[] result = new double[10];
      int count = 0;
      for (S s : solutionList) {
          double v = s.objectives()[objective];
          if (result.length == count) result = Arrays.copyOf(result, count * 2);
          result[count++] = v;
      }
      result = Arrays.copyOfRange(result, 0, count);

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
    Check.notNull(originalSolutionList);
    Check.collectionIsNotEmpty(originalSolutionList);

    if (originalSolutionList.size() <= finalListSize) {
      return originalSolutionList;
    }

    if (originalSolutionList.get(0).objectives().length == 2) {
      Archive<S> archive = new CrowdingDistanceArchive<>(finalListSize) ;
        for (S s : originalSolutionList) {
            archive.add(s);
        }

        return archive.getSolutionList() ;
    }

    for (int i = 0; i < originalSolutionList.size(); i++) {
      originalSolutionList.get(i).attributes().put("INDEX_", i);
    }

    // STEP 1. Normalize the objectives values of the solution list
    List<S> solutions = new ArrayList<>();
    solutions.addAll(normalizeSolutionList(originalSolutionList));

    // STEP 2. Find the solution having the best objective value, being the objective randomly
    // selected
    int randomObjective =
            JMetalRandom.getInstance().nextInt(0, solutions.get(0).objectives().length - 1);

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
        solution.attributes().put(
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
          S index_ = originalSolutionList.get((int) solution.attributes().get("INDEX_"));
          resultList.add(index_);
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

      int bound = solutionList.size();
      for (int i = 0; i < bound; i++) {
          matrix[i] = solutionList.get(i).objectives();
      }

      return matrix ;
  }
}
