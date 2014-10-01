package org.uma.jmetal3.util.solutionlistsutils;

import org.uma.jmetal3.core.Solution;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 */
public class FindWorstSolution {
  public static int find(List<Solution<?>> solutionList, Comparator<Solution<?>> comparator) {
    if ((solutionList == null) || (solutionList.isEmpty())) {
      return -1;
    }

    int index = 0;
    Solution worstKnown = solutionList.get(0), candidateSolution;
    int flag;
    for (int i = 1; i < solutionList.size(); i++) {
      candidateSolution = solutionList.get(i);
      flag = comparator.compare(worstKnown, candidateSolution);
      if (flag == -1) {
        index = i;
        worstKnown = candidateSolution;
      }
    }

    return index;
  }
}
