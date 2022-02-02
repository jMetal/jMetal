package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.initializeVelocity.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.initializeVelocity.InitializeVelocity;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class DefaultVelocityInitialization implements InitializeVelocity {
  @Override
  public double[][] initialize(List<DoubleSolution> swarm) {
    Check.notNull(swarm);
    Check.that(swarm.size() > 0, "The swarm size is lower than 1: " + swarm.size());

    int numberOfVariables = swarm.get(0).variables().size() ;
    double[][] speed = new double[swarm.size()][numberOfVariables] ;

    Arrays.fill(speed, 0.0);

    return speed;
  }
}
