package org.uma.jmetal.util.grouping.impl;

import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.grouping.CollectionGrouping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that groups a list of {@link Comparable} entities by ascending order. The number of
 * groups is a constructor parameter and the group size is computed by dividing the list size
 * by the number of groups. If the remainder of this division is not zero, the last group will
 * contains the remaining index values (so, its size would be higher than the size of the rest of
 * groups).
 *
 * @author Antonio J. Nebro
 *
 * @param <C>
 */
public class ListOrderedGrouping<C extends Comparable<C>> extends ListGrouping<C> {

  public ListOrderedGrouping(int numberOfGroups) {
    super(numberOfGroups) ;
  }

  @Override
  public void computeGroups(List<C> list) {
    Check.notNull(list);
    indices = new ArrayList<>(list.size());
    IntStream.range(0, list.size()).forEach(i -> indices.add(i));

    indices = indices.stream().sorted(Comparator.comparing(list::get)).collect(Collectors.toList());

    createGroups();
  }
}
