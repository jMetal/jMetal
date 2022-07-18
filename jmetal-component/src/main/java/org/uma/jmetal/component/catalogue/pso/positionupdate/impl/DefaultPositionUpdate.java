package org.uma.jmetal.component.catalogue.pso.positionupdate.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * @author Antonio J. Nebro
 */
public class DefaultPositionUpdate implements PositionUpdate {
  protected double velocityChangeWhenLowerLimitIsReached;
  protected double velocityChangeWhenUpperLimitIsReached;
  List<Bounds<Double>> positionBounds;

  /**
   * Constructor
   * @param velocityChangeWhenLowerLimitIsReached: Double value which represent the lower position limit to reach.
   * @param velocityChangeWhenUpperLimitIsReached: Double value which represent the upper position limit to reach.
   * @param positionBounds: List of positions.
   */
  public DefaultPositionUpdate(double velocityChangeWhenLowerLimitIsReached, double velocityChangeWhenUpperLimitIsReached,
                               List<Bounds<Double>> positionBounds) {
    this.velocityChangeWhenLowerLimitIsReached = velocityChangeWhenLowerLimitIsReached;
    this.velocityChangeWhenUpperLimitIsReached = velocityChangeWhenUpperLimitIsReached;
    Check.notNull(positionBounds);

    this.positionBounds = positionBounds;
  }

  @Override
  /** Update the position of the particles.
   * @param swarm: List of possible solutions.
   * @param speed: Speed of the particle.
   * @return swarm updated.
   */
  public List<DoubleSolution> update(List<DoubleSolution> swarm, double[][] speed) {
    Check.notNull(swarm);
    Check.that(!swarm.isEmpty(), "The swarm is empty: " + swarm.size());
    Check.that(swarm.get(0).variables().size() == positionBounds.size(), "The sizes of the list of " +
            "bounds and variables do not match: " + positionBounds.size() + " vs " + swarm.get(0).variables().size());
    Check.that(swarm.size() == speed.length, "The sizes of the list of particles and the speed matrix " +
            "do not match: " + swarm.size() + " vs " + speed.length);

    for (var i = 0; i < swarm.size(); i++) {
      var particle = swarm.get(i);
      for (var j = 0; j < particle.variables().size(); j++) {
        particle.variables().set(j, particle.variables().get(j) + speed[i][j]);

        var bounds = positionBounds.get(j);
        var lowerBound = bounds.getLowerBound();
        var upperBound = bounds.getUpperBound();
        if (particle.variables().get(j) < lowerBound) {
          particle.variables().set(j, lowerBound);
          speed[i][j] = speed[i][j] * velocityChangeWhenLowerLimitIsReached;
        }
        if (particle.variables().get(j) > upperBound) {
          particle.variables().set(j, upperBound);
          speed[i][j] = speed[i][j] * velocityChangeWhenUpperLimitIsReached;
        }
      }
    }

    return swarm;
  }

  /**
   * Getter of velocityChangeWhenLowerLimitIsReached variable
   */
  public double getVelocityChangeWhenLowerLimitIsReached() {
    return velocityChangeWhenLowerLimitIsReached;
  }

  /**
   * Getter of velocityChangeWhenUpperLimitIsReached variable
   */
  public double getVelocityChangeWhenUpperLimitIsReached() {
    return velocityChangeWhenUpperLimitIsReached;
  }
}
