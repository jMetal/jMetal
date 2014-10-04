package org.uma.jmetal3.util.solutionlistsutils;

import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.util.solutionattribute.Ranking;
import org.uma.jmetal3.util.solutionattribute.impl.DominanceRanking;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 29/09/14.
 */
public class FindNondominatedSolutions {
  private static Ranking ranking = new DominanceRanking() ;

  public static List<Solution<?>> getNonDominatedSolutions(List<Solution<?>> solutionSet) {
    return ranking.computeRanking(solutionSet).getSubfront(0);
  }
}

