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

class ListUtilsTest {

  @Nested
  @DisplayName("Test cases for method randomSelectionWithoutReplacement")
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
    void selectionWithALowerListSizeThanTheNumberOfRequestedElementsRaisesAnException() {
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
  @DisplayName("Test cases for method randomSelectionWithReplacement")
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
    void selectionWithALowerListSizeThanTheNumberOfRequestedElementsRaisesAnException() {
      var list = List.of(1, 2, 3, 4);
      int numberOfRequestedElements = 5;
      assertThatThrownBy(
          () -> ListUtils.randomSelectionWithReplacement(numberOfRequestedElements, list,
              JMetalRandom.getInstance()::nextInt)).isInstanceOf(
          InvalidConditionException.class);
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
}