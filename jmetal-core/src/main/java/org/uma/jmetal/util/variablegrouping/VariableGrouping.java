package org.uma.jmetal.util.variablegrouping;

import java.util.List;

/**
 * Interface representing objects that group collections according to some criterion
 *
 * @author Antonio J. Nebro
 *
 * @param <S>
 */
public interface VariableGrouping<S> {
  int numberOfGroups();
  void computeGroups(S itemsToGroup) ;
  List<Integer> getGroup(int groupIndex);
}
