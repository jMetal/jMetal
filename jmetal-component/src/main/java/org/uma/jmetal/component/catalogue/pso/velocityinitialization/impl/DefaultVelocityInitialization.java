package org.uma.jmetal.component.catalogue.pso.velocityinitialization.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * @author Antonio J. Nebro
 */
public class DefaultVelocityInitialization implements VelocityInitialization {
  @Override
  /**
   * Initialize the velocity of the particles.
   * @param swarm: List of possible solutions.
   * @return A matrix with the initial velocity of the particles
   */
  public double[][] initialize(List<DoubleSolution> swarm) {
    Check.notNull(swarm);
    Check.that(!swarm.isEmpty(), "The swarm size is empty: " + swarm.size());

    var numberOfVariables = swarm.get(0).variables().size() ;
    var speed = new double[swarm.size()][numberOfVariables] ;

    for (var i = 0; i < speed.length; i++) {
      for (var j = 0; j < numberOfVariables; j++) {
        speed[i][j] = 0.0 ;
      }
    }

    return speed;
  }
}
