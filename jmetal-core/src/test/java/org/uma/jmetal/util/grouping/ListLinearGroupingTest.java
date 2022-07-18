package org.uma.jmetal.util.grouping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.grouping.impl.ListLinearGrouping;

class ListLinearGroupingTest {
  @Test
  public void shouldConstructorWorkProperly() {
    var numberOfGroups = 4;
    CollectionGrouping<List<Double>> grouping = new ListLinearGrouping<>(numberOfGroups);

    assertEquals(numberOfGroups, grouping.numberOfGroups());
  }

  @Test
  public void shouldGetGroupWithANegativeParameterValueThrowAnException() {
    assertThrows(
        InvalidConditionException.class, () -> new ListLinearGrouping<Integer>(4).getGroup(-1));
  }

  @Test
  public void shouldGetGroupWithParameterValueHigherThanTheNumberOfGropusThrowAnException() {
    assertThrows(
        InvalidConditionException.class, () -> new ListLinearGrouping<Integer>(4).getGroup(4));
  }

  @Test
  public void shouldCreateGroupWorkProperlyWithADivisibleNumberOfGroupsAndAnOrderedIntegerList() {
    var values = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
    var numberOfGroups = 4;

    CollectionGrouping<List<Integer>> grouping = new ListLinearGrouping<>(numberOfGroups);
    grouping.computeGroups(values);

    assertEquals(numberOfGroups, grouping.numberOfGroups());
    var firstGroup = List.of(0, 1, 2);
    assertEquals(firstGroup, grouping.getGroup(0));
    var secondGroup = List.of(3, 4, 5);
    assertEquals(secondGroup, grouping.getGroup(1));
    var thirdGroup = List.of(6, 7, 8);
    assertEquals(thirdGroup, grouping.getGroup(2));
    var fourthGroup = List.of(9, 10, 11);
    assertEquals(fourthGroup, grouping.getGroup(3));
  }

  @Test
  public void shouldCreateGroupWorkProperlyWithADivisibleNumberOfGroupsAndAnUnorderedDoubleList() {
    var values = List.of(11.0, 10.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0);
    var numberOfGroups = 4;

    CollectionGrouping<List<Double>> grouping = new ListLinearGrouping<>(numberOfGroups);
    grouping.computeGroups(values);

    assertEquals(numberOfGroups, grouping.numberOfGroups());
    var firstGroup = List.of(0, 1, 2);
    assertEquals(firstGroup, grouping.getGroup(0));
    var secondGroup = List.of(3, 4, 5);
    assertEquals(secondGroup, grouping.getGroup(1));
    var thirdGroup = List.of(6, 7, 8);
    assertEquals(thirdGroup, grouping.getGroup(2));
    var fourthGroup = List.of(9, 10, 11);
    assertEquals(fourthGroup, grouping.getGroup(3));
  }

  @Test
  public void shouldCreateGroupWorkProperlyWithANonDivisibleNumberOfGroupsAndAnDoubleList() {
    var values = List.of(4.0, 10.0, 9.0, 8.0, 7.0);
    var numberOfGroups = 3;

    CollectionGrouping<List<Double>> grouping = new ListLinearGrouping<>(numberOfGroups);
    grouping.computeGroups(values);

    assertEquals(numberOfGroups, grouping.numberOfGroups());
    var firstGroup = List.of(0);
    assertEquals(firstGroup, grouping.getGroup(0));
    var secondGroup = List.of(1);
    assertEquals(secondGroup, grouping.getGroup(1));
    var thirdGroup = List.of(2, 3, 4);
    assertEquals(thirdGroup, grouping.getGroup(2));
  }

  @Test
  public void shouldCreateGroupWorkProperlyWithANonDivisibleNumberOfGroupsAndAStringList() {
    var values = List.of("B", "C", "V", "AC", "CE", "CD", "A", "L", "G");
    var numberOfGroups = 4;

    CollectionGrouping<List<String>> grouping = new ListLinearGrouping<>(numberOfGroups);
    grouping.computeGroups(values);

    assertEquals(numberOfGroups, grouping.numberOfGroups());
    var firstGroup = List.of(0, 1);
    assertEquals(firstGroup, grouping.getGroup(0));
    var secondGroup = List.of(2, 3);
    assertEquals(secondGroup, grouping.getGroup(1));
    var thirdGroup = List.of(4, 5);
    assertEquals(thirdGroup, grouping.getGroup(2));
    var fourthGroup = List.of(6, 7, 8);
    assertEquals(fourthGroup, grouping.getGroup(3));
  }

  @Test
  public void shouldCreateGroupReturnTheSameGroupsIfItIsInvokedTwiceWithListsOfEqualSize() {
    var values = List.of("B", "C", "V", "AC", "CE", "CD", "A", "L", "G");
    var numberOfGroups = 3;

    CollectionGrouping<List<String>> grouping = new ListLinearGrouping<>(numberOfGroups);
    grouping.computeGroups(values);

    assertEquals(numberOfGroups, grouping.numberOfGroups());
    var firstGroup = List.of(0, 1, 2);
    assertEquals(firstGroup, grouping.getGroup(0));
    var secondGroup = List.of(3, 4, 5);
    assertEquals(secondGroup, grouping.getGroup(1));
    var thirdGroup = List.of(6, 7, 8);
    assertEquals(thirdGroup, grouping.getGroup(2));

    var moreValues = List.of("CB", "AD", "AH", "H", "B", "EFG", "Y", "AD", "YG");
    grouping.computeGroups(moreValues);

    assertEquals(numberOfGroups, grouping.numberOfGroups());
    firstGroup = List.of(0, 1, 2);
    assertEquals(firstGroup, grouping.getGroup(0));
    secondGroup = List.of(3, 4, 5);
    assertEquals(secondGroup, grouping.getGroup(1));
    thirdGroup = List.of(6, 7, 8);
    assertEquals(thirdGroup, grouping.getGroup(2));
  }
}
