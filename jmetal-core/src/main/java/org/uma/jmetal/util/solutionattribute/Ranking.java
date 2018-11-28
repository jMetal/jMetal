package org.uma.jmetal.util.solutionattribute;

import java.util.List;

/**
 * Ranks a list of solutions according to the dominance relationship
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Ranking<S> extends SolutionAttribute<S, Integer>{
  public Ranking<S> computeRanking(List<S> solutionList) ;
  public List<S> getSubfront(int rank) ;
  public int getNumberOfSubfronts() ;
}
