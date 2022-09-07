package org.uma.jmetal.component.catalogue.pso.velocityinitialization.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

/**
 * Class that initializes the velocity of the particles according to the standard PSO 2011 (SPSO 2011)
 * Source: Maurice Clerc. Standard Particle Swarm Optimisation. 15 pages. 2012. <hal-00764996>
 *
 * @author Antonio J. Nebro
 */
public class SPSO2011VelocityInitialization implements VelocityInitialization {
  @Override
  /**
   * Initialize the velocity of the particles.
   * @param swarm: List of possible solutions.
   * @return A matrix with the initial velocity of the particles
   */
  public double[][] initialize(List<DoubleSolution> swarm) {
    Check.notNull(swarm);
    Check.that(!swarm.isEmpty(), "The swarm size is empty: " + swarm.size());

    int numberOfVariables = swarm.get(0).variables().size() ;
    double[][] speed = new double[swarm.size()][numberOfVariables] ;
    PseudoRandomGenerator randomGenerator = JMetalRandom.getInstance().getRandomGenerator();

    for (int i = 0 ; i < speed.length; i++) {
      DoubleSolution particle = swarm.get(i) ;
      for (int j = 0; j < numberOfVariables; j++) {
        Bounds<Double> bounds = particle.getBounds(j) ;
        speed[i][j] = (randomGenerator.nextDouble(bounds.getLowerBound() - particle.variables().get(j),
            bounds.getUpperBound() - particle.variables().get(j)));
      }
    }

    return speed;
  }
}
