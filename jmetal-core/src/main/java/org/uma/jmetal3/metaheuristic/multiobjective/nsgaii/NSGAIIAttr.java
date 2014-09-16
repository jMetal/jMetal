package org.uma.jmetal3.metaheuristic.multiobjective.nsgaii;

import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.attributes.*;
import org.uma.jmetal3.encoding.attributes.Ranking;

/**
 * Created by antonio on 13/09/14.
 */
public class NSGAIIAttr extends AlgorithmAttributes implements CrowdingDistance, Ranking {
  private int rank;
  private double crowdingDistance ;

  public NSGAIIAttr() {
    rank = 0 ;
    crowdingDistance = 0.0 ;
  }

  public NSGAIIAttr(NSGAIIAttr attr) {
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
  public AlgorithmAttributes copy() {
    return new NSGAIIAttr(this) ;
  }

  @Override
  public String toString() {
    return "Rank: "+ getRank() + ". CD: " + getCrowdingDistance() ;
  }

  public static NSGAIIAttr getAttributes(Solution solution) {
    return (NSGAIIAttr)solution.getAlgorithmAttributes() ;
  }
}
