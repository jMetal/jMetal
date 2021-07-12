package org.uma.jmetal.util.variablegrouping.impl;

import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.variablegrouping.VariableGrouping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that groups a list of {@link Comparable} entities by ascending order. The number of
 * groups is a constructor parameter and the group size is computed by dividing the list size
 * by the number of groups. If the remainder of this division is not zero, the last group will
 * contains the remaining index values (so its size will be higher than the size of the rest of
 * groups).
 *
 * @author Antonio J. Nebro
 *
 * @param <C>
 */
public class ListOrderedGrouping<C extends Comparable<C>> implements VariableGrouping<List<C>> {
  private final int numberOfGroups;
  private List<Integer> indices;
  private List<List<Integer>> groups;

  public ListOrderedGrouping(int numberOfGroups) {
    this.numberOfGroups = numberOfGroups;
    this.groups = new ArrayList<>(numberOfGroups);
  }

  public void computeGroups(List<C> list) {
    Check.notNull(list);
    indices = new ArrayList<>(list.size());
    IntStream.range(0, list.size()).forEach(i -> indices.add(i));

    indices = indices.stream().sorted(Comparator.comparing(list::get)).collect(Collectors.toList());

    createGroups();
  }

  private void createGroups() {
    int groupSize = computeGroupSize();
    int index = groupValues(groupSize);

    fillRemainingIndices(index);
  }

  /**
   * Method that groups the values and return the index counter
   *
   * @param groupSize
   * @return the position of the index after the grouping
   */
  private int groupValues(int groupSize) {
    int index = 0 ;
    for (int i = 0; i < numberOfGroups; i++) {
      groups.add(new ArrayList<>());
      for (int j = 0; j < groupSize; j++) {
        groups.get(i).add(indices.get(index++));
      }
    }
    return index;
  }

  /**
   * If the indices length is not divisible by the number of groups, the remaining indices
   * are added to the last group
   * @param index
   */
  private void fillRemainingIndices(int index) {
    int lastGroupIndex = groups.size() - 1 ;
    while (index < indices.size()) {
      groups.get(lastGroupIndex).add(indices.get(index++)) ;
    }
  }


  private int computeGroupSize() {
    return indices.size() / numberOfGroups;
  }

  @Override
  public int numberOfGroups() {
    return numberOfGroups;
  }

  @Override
  public List<Integer> getGroup(int groupIndex) {
    Check.that(
        ((groupIndex >= 0) && (groupIndex < numberOfGroups)),
        "The group index " + groupIndex + " is invalid");
    return groups.get(groupIndex);
  }
}
