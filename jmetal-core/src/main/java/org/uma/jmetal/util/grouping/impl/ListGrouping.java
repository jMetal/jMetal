package org.uma.jmetal.util.grouping.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.grouping.CollectionGrouping;

/**
 * Abstract class that groups a list of {@link Comparable} entities by some order in the collection. The number of
 * groups is a constructor parameter and the group size is computed by dividing the list size
 * by the number of groups. If the remainder of this division is not zero, the last group will
 * contain the remaining index values (so, its size would be higher than the size of the rest of
 * groups).
 *
 * @author Antonio J. Nebro
 *
 * @param <C>
 */
public abstract class ListGrouping<C extends Comparable<C>> implements CollectionGrouping<List<C>> {
  protected final int numberOfGroups;
  protected List<Integer> indices;
  protected List<List<Integer>> groups;

  protected ListGrouping(int numberOfGroups) {
    this.numberOfGroups = numberOfGroups;
    this.groups = new ArrayList<>(numberOfGroups);
  }

  protected void createGroups() {
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
   * If the indexes length is not divisible by the number of groups, the remaining indices
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
  public List<Integer> group(int groupIndex) {
    Check.that(
            ((groupIndex >= 0) && (groupIndex < numberOfGroups)),
            "The group index " + groupIndex + " is invalid");
    return groups.get(groupIndex);
  }
}
