package org.uma.jmetal3.metaheuristic.multiobjective.nsgaii;

import org.uma.jmetal3.encoding.attributes.*;
import org.uma.jmetal3.encoding.attributes.Ranking;

/**
 * Created by antonio on 13/09/14.
 */
public class NSGAIIAttr extends Attributes implements CrowdingDistance, Ranking {
  public NSGAIIAttr() {
    super();
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
    return (Integer)map.get("Ranking");
  }

  @Override
  public void setRank(Integer rank) {
    map.put("Ranking", rank) ;

  }
}
