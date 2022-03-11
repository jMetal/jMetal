package org.uma.jmetal.util.grouping.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Class that groups a list of {@link Comparable} entities by order in the collection. The number of
 * groups is a constructor parameter and the group size is computed by dividing the list size by the
 * number of groups. If the remainder of this division is not zero, the last group will contain the
 * remaining index values (so, its size would be higher than the size of the rest of groups).
 *
 * To avoid unnecessary recomputing of the groups, the state variable {@link #lastListSize} records
 * the size of the last list that was grouped and only recomputes the groups if the size of the new list
 * is different.
 *
 * @author Antonio J. Nebro
 * @param <C>
 */
public class ListLinearGrouping<C extends Comparable<C>> extends ListGrouping<C> {
  private int lastListSize = 0;

  public ListLinearGrouping(int numberOfGroups) {
    super(numberOfGroups);
  }

  @Override
  public void computeGroups(List<C> list) {
    Check.notNull(list);

    if (lastListSize != list.size()) {
      indices = new ArrayList<>(list.size());
      IntStream.range(0, list.size()).forEach(i -> indices.add(i));

      createGroups();
      lastListSize = list.size();
    }
  }
}
