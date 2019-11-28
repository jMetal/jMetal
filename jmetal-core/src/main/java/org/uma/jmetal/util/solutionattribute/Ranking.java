package org.uma.jmetal.util.solutionattribute;

import java.util.List;

/**
 * Ranks a list of solutions according to the dominance relationship
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@Deprecated
public interface Ranking<S> extends SolutionAttribute<S, Integer>{
  Ranking<S> computeRanking(List<S> solutionList) ;
  List<S> getSubFront(int rank) ;
  int getNumberOfSubFronts() ;
}
