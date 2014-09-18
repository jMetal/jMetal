package org.uma.jmetal3.encoding.attributes;

import org.uma.jmetal3.core.Solution;

/**
 * Attribute representing the crowding distance, used in NSGA-II and many other algorithms
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface CrowdingDistanceAttr {
  public Double getCrowdingDistance() ;
  public void setCrowdingDistance(Double crowdingDistance) ;
}
