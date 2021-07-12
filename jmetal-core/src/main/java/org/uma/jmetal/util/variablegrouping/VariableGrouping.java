package org.uma.jmetal.util.variablegrouping;

import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface VariableGrouping<S> {
  int numberOfGroups();
  void computeGroups(S itemsToGroup) ;
  List<Integer> getGroup(int groupIndex);
}
