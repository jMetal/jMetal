package org.uma.jmetal3.metaheuristic.multiobjective.nsgaii;

import org.uma.jmetal3.encoding.attributes.*;
import org.uma.jmetal3.encoding.attributes.Ranking;

/**
 * Created by antonio on 13/09/14.
 */
public class NSGAIIAttr extends AlgorithmAttributes implements CrowdingDistance, Ranking {
  public NSGAIIAttr() {
    super();
  }

  public NSGAIIAttr(NSGAIIAttr attr) {
    super(attr);
  }

  @Override
  public Double getCrowdingDistance() {
    return (Double) map.get("crowdingDistance") ;
  }

  @Override
  public void setCrowdingDistance(Double crowdingDistance) {
     map.put("crowdingDistance", crowdingDistance) ;
  }

  @Override
  public Integer getRank() {
    return (Integer)map.get("Rank");
  }

  @Override
  public void setRank(Integer rank) {
    map.put("Rank", rank) ;
  }

  @Override
  public AlgorithmAttributes copy() {
    return new NSGAIIAttr(this) ;
  }

  @Override
  public String toString() {
    return "Rank: "+ getRank() + ". CD: " + getCrowdingDistance() ;
  }
}
