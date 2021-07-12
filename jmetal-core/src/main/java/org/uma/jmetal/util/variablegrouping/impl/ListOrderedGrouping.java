package org.uma.jmetal.util.variablegrouping.impl;

import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.variablegrouping.VariableGrouping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ListOrderedGrouping<C extends Comparable<C>> implements VariableGrouping<List<C>> {
  private int numberOfGroups;
  private List<Integer> indices;
  public List<List<Integer>> groups;

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
    int groupSize = indices.size() / numberOfGroups;
    int index = 0 ;
    for (int i = 0; i < numberOfGroups; i++) {
      groups.add(new ArrayList<>());
      for (int j = 0; j < groupSize; j++) {
        groups.get(i).add(indices.get(index++));
      }
    }

    int lastGroupIndex = groups.size() - 1 ;
    while (index < indices.size()) {
      groups.get(lastGroupIndex).add(indices.get(index++)) ;
    }
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
