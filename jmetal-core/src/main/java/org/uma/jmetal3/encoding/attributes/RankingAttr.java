package org.uma.jmetal3.encoding.attributes;

import org.uma.jmetal.encoding.variable.Int;
import org.uma.jmetal3.core.Solution;

/**
 * Attribute representing the rank of a solution, used in NSGA-II and many other algorithms
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface RankingAttr {
  public Integer getRank() ;
  public void setRank(Integer rank) ;
}
