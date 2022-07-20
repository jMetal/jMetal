package org.uma.jmetal.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.permutation.PermutationFactory;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class ListUtils {

  /**
   * Selects N random elements from a list without replacement.
   * The size of the list must be equal or higher than the number of elements to select.
   */
  public static <S> List<S> randomSelectionWithoutReplacement(
      int numberOfElementsSelect, List<S> solutionList) {
    JMetalRandom random = JMetalRandom.getInstance();
    return randomSelectionWithoutReplacement(
        numberOfElementsSelect, solutionList, random::nextInt);
  }

  /**
   * Selects N random elements from a list without replacement.
   * The size of the list must be equal or higher than the number of elements to select.
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
  }

  /**
   * Selects N random elements from a list with replacement.
   * The size of the list can be smaller than the number of requested elements.
   */
  public static <S> List<S> randomSelectionWithReplacement(
      int numberOfElementsSelect, List<S> solutionList) {
    JMetalRandom random = JMetalRandom.getInstance();
    return randomSelectionWithReplacement(
        numberOfElementsSelect, solutionList, random::nextInt);
  }

  /**
   * Selects N random elements from a list with replacement.
   * The size of the list can be smaller than the number of requested elements.
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
   * @param firstList A <code>list</code>
   * @param secondList A <code>list</code>
   * @return true if both lists contain the same elements (in any order), false in other case
   */
  public static <S> boolean listAreEquals(List<S> firstList, List<S> secondList) {
    Check.notNull(firstList);
    Check.notNull(secondList);

    if (firstList.size() != secondList.size()) {
      return false ;
    } else {
      return new HashSet<>(firstList).containsAll(secondList) ;
    }
  }
}
