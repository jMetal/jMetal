package org.uma.jmetal3.encoding.attributes;

import org.uma.jmetal3.core.Solution;

/**
 * Created by antonio on 10/09/14.
 */
public interface RankingSolution extends Solution {
  public int getRank() ;
  public void setRank(int rank) ;
}
