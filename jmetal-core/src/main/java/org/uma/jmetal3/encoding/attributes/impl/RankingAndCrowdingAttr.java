package org.uma.jmetal3.encoding.attributes.impl;

import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.attributes.*;
import org.uma.jmetal3.encoding.attributes.RankingAttr;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class RankingAndCrowdingAttr extends AlgorithmAttributes implements CrowdingDistanceAttr, RankingAttr {
  private int rank;
  private double crowdingDistance ;

  public RankingAndCrowdingAttr() {
    rank = 0 ;
    crowdingDistance = 0.0 ;
  }

  public RankingAndCrowdingAttr(RankingAndCrowdingAttr attr) {
    this.rank = attr.rank ;
    this.crowdingDistance = attr.crowdingDistance ;
  }

  @Override
  public Double getCrowdingDistance() {
    return crowdingDistance ;
  }

  @Override
  public void setCrowdingDistance(Double crowdingDistance) {
     this.crowdingDistance = crowdingDistance ;
  }

  @Override
  public Integer getRank() {
    return rank ;
  }

  @Override
  public void setRank(Integer rank) {
    this.rank = rank ;
  }

  @Override
  public Object clone() {
    return new RankingAndCrowdingAttr(this) ;
  }

  @Override
  public String toString() {
    return "Rank: "+ getRank() + ". CD: " + getCrowdingDistance() ;
  }

  public static RankingAndCrowdingAttr getAttributes(Solution solution) {
    return (RankingAndCrowdingAttr)solution.getAlgorithmAttributes() ;
  }
}
