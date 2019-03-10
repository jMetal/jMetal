package org.uma.jmetal.auto.util.ranking;

import org.uma.jmetal.auto.util.attribute.Attribute;

import java.util.List;

/**
 * Ranks a list of solutions according to the dominance relationship
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Ranking<S> extends Attribute {
  Ranking<S> computeRanking(List<S> solutionList) ;
  List<S> getSubFront(int rank) ;
  int getNumberOfSubFronts() ;
}
