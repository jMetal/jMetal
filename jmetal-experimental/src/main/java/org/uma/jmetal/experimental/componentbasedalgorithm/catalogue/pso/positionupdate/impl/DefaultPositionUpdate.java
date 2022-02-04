package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.List;

public class DefaultPositionUpdate implements PositionUpdate {
  protected double velocityChangeWhenLowerLimitIsReached;
  protected double velocityChangeWhenUpperLimitIsReached;
  List<Bounds<Double>> positionBounds;

  public DefaultPositionUpdate(double velocityChangeWhenLowerLimitIsReached, double velocityChangeWhenUpperLimitIsReached,
                               List<Bounds<Double>> positionBounds) {
    this.velocityChangeWhenLowerLimitIsReached = velocityChangeWhenLowerLimitIsReached;
    this.velocityChangeWhenUpperLimitIsReached = velocityChangeWhenUpperLimitIsReached;
    Check.notNull(positionBounds);

    this.positionBounds = positionBounds;
  }

  @Override
  public List<DoubleSolution> update(List<DoubleSolution> swarm, double[][] speed) {
    Check.notNull(swarm);
    Check.that(swarm.size() > 0, "The swarm size is not > 0: " + swarm.size());
    Check.that(swarm.get(0).variables().size() == positionBounds.size(), "The sizes of the list of " +
            "bounds and varibles do not match: " + positionBounds.size() + " vs " + swarm.get(0).variables().size());
    Check.that(swarm.size() == speed.length, "The sizes of the list of particles and the speed matrix " +
            "do not match: " + swarm.size() + " vs " + speed.length);

    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = swarm.get(i);
      for (int j = 0; j < particle.variables().size(); j++) {
        particle.variables().set(j, particle.variables().get(j) + speed[i][j]);

        Bounds<Double> bounds = positionBounds.get(j);
        Double lowerBound = bounds.getLowerBound();
        Double upperBound = bounds.getUpperBound();
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

  public double getVelocityChangeWhenLowerLimitIsReached() {
    return velocityChangeWhenLowerLimitIsReached;
  }

  public double getVelocityChangeWhenUpperLimitIsReached() {
    return velocityChangeWhenUpperLimitIsReached;
  }
}
