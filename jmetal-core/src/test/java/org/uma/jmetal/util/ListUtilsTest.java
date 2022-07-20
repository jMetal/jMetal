package org.uma.jmetal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NegativeValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Test classes for class {@link ListUtils}
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
class ListUtilsTest {

  @Nested
  @DisplayName("Test cases for method randomSelectionWithoutReplacement()")
  class RandomSelectionWithoutReplacementTestCases {

    @Test
    void selectionWithANegativeNumberOfSolutionsToSelectRaisesAnException() {
      assertThatThrownBy(() -> ListUtils.randomSelectionWithoutReplacement(-1,
          new ArrayList<>(),
          JMetalRandom.getInstance()::nextInt)).isInstanceOf(
          NegativeValueException.class);
    }

    @Test
    void selectionWithANullRandomGeneratorRaisesAnException() {
      assertThatThrownBy(() -> ListUtils.randomSelectionWithoutReplacement(1, List.of(1, 2, 3),
          null)).isInstanceOf(
          NullParameterException.class);
    }

    @Test
    void selectionWithANullListRaisesAnException() {
      assertThatThrownBy(() -> ListUtils.randomSelectionWithoutReplacement(1, null,
          JMetalRandom.getInstance()::nextInt)).isInstanceOf(
          NullParameterException.class);
    }

    @Test
    void selectionWithAnEmptyListRaisesAnException() {
      assertThatThrownBy(() -> ListUtils.randomSelectionWithoutReplacement(1, new ArrayList<>(),
          JMetalRandom.getInstance()::nextInt)).isInstanceOf(
          EmptyCollectionException.class);
    }

    @Test
    void selectionWithAListSizeLowerThanTheNumberOfRequestedElementsRaisesAnException() {
      var list = List.of(1, 2, 3, 4);
      int numberOfRequestedElements = 5;
      assertThatThrownBy(
          () -> ListUtils.randomSelectionWithoutReplacement(numberOfRequestedElements, list,
              JMetalRandom.getInstance()::nextInt)).isInstanceOf(
          InvalidConditionException.class);
    }

    @Test
    void selectionWithAListWithOneElementReturnAListWithThatElement() {
      int value = 4;
      var list = List.of(value);
      List<Integer> resultList = ListUtils.randomSelectionWithoutReplacement(1, list);

      assertThat(resultList).hasSize(1);
      assertThat(resultList.get(0)).isEqualTo(value);
    }

    @Test
    void selectionRequestingAllTheElementsOfAListWorkProperly() {
      var list = List.of(1, 2, 3, 4, 5, 6);
      List<Integer> resultList = ListUtils.randomSelectionWithoutReplacement(list.size(), list);

      assertThat(resultList)
          .hasSize(list.size())
          .containsExactlyInAnyOrderElementsOf(list);
    }

    @Test
    void selectionReturnsTheRequestedNumberOfElements() {
      var list = List.of(1, 2, 3, 4, 5, 6, 7, 8);
      int numberOfRequestedElements = 5 ;
      List<Integer> resultList = ListUtils.randomSelectionWithoutReplacement(numberOfRequestedElements, list);

      assertThat(resultList)
          .hasSize(numberOfRequestedElements) ;
    }
  }

  @Nested
  @DisplayName("Test cases for method randomSelectionWithReplacement()")
  class RandomSelectionWithReplacementTestCases {

    @Test
    void selectionWithANegativeNumberOfSolutionsToSelectRaisesAnException() {
      assertThatThrownBy(() -> ListUtils.randomSelectionWithReplacement(-1,
          new ArrayList<>(),
          JMetalRandom.getInstance()::nextInt)).isInstanceOf(
          NegativeValueException.class);
    }

    @Test
    void selectionWithANullRandomGeneratorRaisesAnException() {
      assertThatThrownBy(() -> ListUtils.randomSelectionWithReplacement(1, List.of(1, 2, 3),
          null)).isInstanceOf(
          NullParameterException.class);
    }

    @Test
    void selectionWithANullListRaisesAnException() {
      assertThatThrownBy(() -> ListUtils.randomSelectionWithReplacement(1, null,
          JMetalRandom.getInstance()::nextInt)).isInstanceOf(
          NullParameterException.class);
    }

    @Test
    void selectionWithAnEmptyListRaisesAnException() {
      assertThatThrownBy(() -> ListUtils.randomSelectionWithReplacement(1, new ArrayList<>(),
          JMetalRandom.getInstance()::nextInt)).isInstanceOf(
          EmptyCollectionException.class);
    }

    @Test
    void selectionWithAListSizeLowerThanTheNumberOfRequestedElementsReturnsTheRightNumberOfElements() {
      var list = List.of(1, 2, 3, 4);
      int numberOfRequestedElements = 10;

      assertThat(ListUtils.randomSelectionWithReplacement(numberOfRequestedElements, list)).hasSize(numberOfRequestedElements) ;
    }

    @Test
    void selectionWithAListWithOneElementReturnAListWithThatElement() {
      int value = 4;
      var list = List.of(value);
      List<Integer> resultList = ListUtils.randomSelectionWithReplacement(1, list);

      assertThat(resultList).hasSize(1);
      assertThat(resultList.get(0)).isEqualTo(value);
    }

    @Test
    void selectionReturnsTheRequestedNumberOfElements() {
      var list = List.of(1, 2, 3, 4, 5, 6, 7, 8);
      int numberOfRequestedElements = 5 ;
      List<Integer> resultList = ListUtils.randomSelectionWithReplacement(numberOfRequestedElements, list);

      assertThat(resultList)
          .hasSize(numberOfRequestedElements) ;
    }
  }

  @Nested
  @DisplayName("Test cases for method listAreEquals()")
  class listAreEqualsTestCases {
    @Test
    void comparingTwoListsRaisesAnExceptionIfTheFirstOneIsNull() {
      assertThatThrownBy(
          () -> ListUtils.listAreEquals(null, List.of(4))).isInstanceOf(
          NullParameterException.class);
    }

    @Test
    void comparingTwoListsRaisesAnExceptionIfTheSecondOneIsNull() {
      assertThatThrownBy(
          () -> ListUtils.listAreEquals(List.of(4), null)).isInstanceOf(
          NullParameterException.class);
    }

    @Test
    void comparingTwoListOfDifferentLengthReturnsFalse() {
      List<Double> list1 = List.of(1.0, 2.0) ;
      List<Double> list2 = List.of(1.0, 2.0, 3.0) ;

      assertThat(ListUtils.listAreEquals(list1, list2)).isFalse();
    }

    @Test
    void comparingTwoListWithTheSameElementReturnsTrue() {
      List<Double> list1 = List.of(1.0) ;
      List<Double> list2 = List.of(1.0) ;

      assertThat(ListUtils.listAreEquals(list1, list2)).isTrue();
    }

    @Test
    void comparingTwoListWithADifferentElementReturnsFalse() {
      List<Double> list1 = List.of(1.0) ;
      List<Double> list2 = List.of(3.0) ;

      assertThat(ListUtils.listAreEquals(list1, list2)).isFalse();
    }

    @Test
    void comparingTwoListWithTwoEqualsElementsReturnsTrue() {
      List<Double> list1 = List.of(1.0, 2.0) ;
      List<Double> list2 = List.of(2.0, 1.0) ;

      assertThat(ListUtils.listAreEquals(list1, list2)).isTrue();
    }

    @Test
    void comparingTwoListWithFourEqualsElementsReturnsTrue() {
      List<String> list1 = List.of("this", "dog", "is", "white") ;
      List<String> list2 = List.of("dog", "white", "this", "is") ;

      assertThat(ListUtils.listAreEquals(list1, list2)).isTrue();
    }
  }
}