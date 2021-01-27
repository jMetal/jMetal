package org.uma.jmetal.util.ranking;

import java.util.List;

/**
 * Ranks a list of population according to the dominance relationship
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Ranking<S> {
  Ranking<S> compute(List<S> solutionList) ;
  List<S> getSubFront(int rank) ;
  int getNumberOfSubFronts() ;
  Integer getRank(S solution) ;
}
