package org.uma.jmetal.component.ranking;

import org.uma.jmetal.solution.util.attribute.Attribute;

import java.util.List;

/**
 * Ranks a list of population according to the dominance relationship
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Ranking<S> extends Attribute<S> {
  Ranking<S> computeRanking(List<S> solutionList) ;
  List<S> getSubFront(int rank) ;
  int getNumberOfSubFronts() ;
}
