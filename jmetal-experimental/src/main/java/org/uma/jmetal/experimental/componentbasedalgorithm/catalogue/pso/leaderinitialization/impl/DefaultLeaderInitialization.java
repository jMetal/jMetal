package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.leaderinitialization.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.leaderinitialization.LeaderInitialization;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.List;

/**
 * Initialize the leaders particles.
 * @author D Doblas Jiménez
 * TODO: Tests
 */

public class DefaultLeaderInitialization implements LeaderInitialization {
    @Override
    public BoundedArchive<DoubleSolution> initializeLeader(List<DoubleSolution> swarm, BoundedArchive<DoubleSolution> globalBest) {
        Check.notNull(swarm);
        Check.that(swarm.size() > 0, "The swarm size is empty: " + swarm.size());
        for(DoubleSolution particle: swarm){
            globalBest.add(particle);
        }
        return globalBest;
    }
}
