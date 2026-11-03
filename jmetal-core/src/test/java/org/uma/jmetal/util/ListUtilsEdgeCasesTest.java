package org.uma.jmetal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Edge cases of ListUtils.listAreEquals")
class ListUtilsEdgeCasesTest {

  @Test
  @DisplayName("given lists with the same elements repeated a different number of times, then they differ")
  void givenDifferentMultiplicities_whenComparing_thenTheListsAreNotEqual() {
    // Arrange: same size and same set of elements, but not the same elements
    List<String> first = List.of("a", "a", "b");
    List<String> second = List.of("a", "b", "b");

    // Act & Assert
    assertThat(ListUtils.listAreEquals(first, second)).isFalse();
    assertThat(ListUtils.listAreEquals(second, first)).isFalse();
  }

  @Test
  @DisplayName("given lists with repeated elements in another order, then they are equal")
  void givenRepeatedElementsInAnotherOrder_whenComparing_thenTheListsAreEqual() {
    // Arrange
    List<String> first = List.of("a", "b", "a", "c");
    List<String> second = List.of("c", "a", "a", "b");

    // Act & Assert
    assertThat(ListUtils.listAreEquals(first, second)).isTrue();
  }

  @Test
  @DisplayName("given two empty lists, then they are equal")
  void givenTwoEmptyLists_whenComparing_thenTheListsAreEqual() {
    assertThat(ListUtils.listAreEquals(List.of(), List.of())).isTrue();
  }
}
