package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityinitialization.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.List;

/**
 * @author Antonio J. Nebro
 */
public class DefaultVelocityInitialization implements VelocityInitialization {
  @Override
  public double[][] initialize(List<DoubleSolution> swarm) {
    Check.notNull(swarm);
    Check.that(swarm.size() > 0, "The swarm size is empty: " + swarm.size());

    int numberOfVariables = swarm.get(0).variables().size() ;
    double[][] speed = new double[swarm.size()][numberOfVariables] ;

    for (int i = 0 ; i < speed.length; i++) {
      for (int j = 0; j < speed[0].length; j++) {
        speed[i][j] = 0.0 ;
      }
    }

    return speed;
  }
}
