package org.uma.jmetal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.permutation.PermutationFactory;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class ListUtils {

  /**
   * Selects N random elements from a list without replacement
   */
  public static <S> List<S> randomSelectionWithoutReplacement(
      int numberOfElementsSelect, List<S> solutionList) {
    JMetalRandom random = JMetalRandom.getInstance();
    return randomSelectionWithoutReplacement(
        numberOfElementsSelect, solutionList, random::nextInt);
  }

  /**
   * Selects N random elements from a list without replacement
   *
   * @param list The list
   * @param randomGenerator The random number generator
   * @return The selected elements
   */
  public static <S> List<S> randomSelectionWithoutReplacement(
      int numberOfElementsToSelect,
      List<S> list,
      BoundedRandomGenerator<Integer> randomGenerator) {
    Check.notNull(list);
    Check.notNull(randomGenerator);
    Check.valueIsNotNegative(numberOfElementsToSelect);
    Check.collectionIsNotEmpty(list);
    Check.that(
        list.size() >= numberOfElementsToSelect,
        "The solution list size ("
            + list.size()
            + ") is less than "
            + "the number of requested solutions ("
            + numberOfElementsToSelect
            + ")");

    List<Integer> selectedIndices = PermutationFactory
        .createIntegerPermutation(list.size(), randomGenerator)
        .subList(0, numberOfElementsToSelect) ;

    return selectedIndices.stream().map(list::get).collect(
        Collectors.toList());

    /*
    List<S> resultList = new ArrayList<>(numberOfSolutionsToBeReturned);

    if (list.size() == 1) {
      resultList.add(list.get(0));
    } else {
      Collection<Integer> positions = new HashSet<>(numberOfSolutionsToBeReturned);
      while (positions.size() < numberOfSolutionsToBeReturned) {
        int nextPosition = randomGenerator.getRandomValue(0, list.size() - 1);
        if (!positions.contains(nextPosition)) {
          positions.add(nextPosition);
          resultList.add(list.get(nextPosition));
        }
      }
    }

    return resultList;

     */
  }

  /**
   * Selects N random elements from a list with replacement
   */
  public static <S> List<S> randomSelectionWithReplacement(
      int numberOfElementsSelect, List<S> solutionList) {
    JMetalRandom random = JMetalRandom.getInstance();
    return randomSelectionWithReplacement(
        numberOfElementsSelect, solutionList, random::nextInt);
  }

  /**
   * Selects N random elements from a list with replacement
   *
   * @param list The list
   * @param randomGenerator The random number generator
   * @return The selected elements
   */
  public static <S> List<S> randomSelectionWithReplacement(
      int numberOfElementsToSelect,
      List<S> list,
      BoundedRandomGenerator<Integer> randomGenerator) {
    Check.notNull(list);
    Check.notNull(randomGenerator);
    Check.valueIsNotNegative(numberOfElementsToSelect);
    Check.collectionIsNotEmpty(list);
    Check.that(
        list.size() >= numberOfElementsToSelect,
        "The solution list size ("
            + list.size()
            + ") is less than "
            + "the number of requested solutions ("
            + numberOfElementsToSelect
            + ")");

    List<S> selectedSolutions = new ArrayList<>();

    IntStream.range(0, numberOfElementsToSelect)
        .forEach(
            i ->
                selectedSolutions.add(
                    list.get(randomGenerator.getRandomValue(0, list.size()-1))));

    return selectedSolutions;
  }

  /**
   * Compares two lists to determine if they are equals
   *
   * @param solutions A <code>list</code>
   * @param newSolutionList A <code>list</code>
   * @return true if both are contains equals solutions, false in other case
   */
  public static <S> boolean listAreEquals(List<S> solutions, List<S> newSolutionList) {
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
}
