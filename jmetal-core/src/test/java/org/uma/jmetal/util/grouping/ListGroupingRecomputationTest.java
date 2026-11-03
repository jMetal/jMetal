package org.uma.jmetal.util.grouping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.grouping.impl.ListLinearGrouping;
import org.uma.jmetal.util.grouping.impl.ListOrderedGrouping;

@DisplayName("Groupings that are computed more than once")
class ListGroupingRecomputationTest {

  @Test
  @DisplayName("given a linear grouping, when a list of another size is grouped, then only its groups remain")
  void givenALinearGrouping_whenAListOfAnotherSizeIsGrouped_thenOnlyItsGroupsRemain() {
    // Arrange
    var grouping = new ListLinearGrouping<Integer>(2);
    grouping.computeGroups(List.of(5, 6, 7, 8));

    // Act
    grouping.computeGroups(List.of(1, 2, 3, 4, 5, 6));

    // Assert
    assertThat(grouping.group(0)).containsExactly(0, 1, 2);
    assertThat(grouping.group(1)).containsExactly(3, 4, 5);
  }

  @Test
  @DisplayName("given an ordered grouping, when it is computed again, then only the new groups remain")
  void givenAnOrderedGrouping_whenItIsComputedAgain_thenOnlyTheNewGroupsRemain() {
    // Arrange
    var grouping = new ListOrderedGrouping<Integer>(2);
    grouping.computeGroups(List.of(9, 1, 5, 3));

    // Act
    grouping.computeGroups(List.of(4, 8, 2, 6));

    // Assert: the indices of the values in ascending order are 2 (2), 0 (4), 3 (6) and 1 (8)
    assertThat(grouping.group(0)).containsExactly(2, 0);
    assertThat(grouping.group(1)).containsExactly(3, 1);
  }

  @Test
  @DisplayName("given a number of groups that is not positive, when created, then it is rejected")
  void givenANonPositiveNumberOfGroups_whenCreated_thenItIsRejected() {
    assertThrows(InvalidConditionException.class, () -> new ListLinearGrouping<Integer>(0));
    assertThrows(InvalidConditionException.class, () -> new ListOrderedGrouping<Integer>(-1));
  }
}
