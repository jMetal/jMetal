package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.initializeLeader.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.initializeLeader.InitializeLeader;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.List;

/**
 * Initialize the leaders particles.
 * @author D Doblas Jiménez
 * TODO: Tests
 */

public class DefaultLeaderInitialization implements InitializeLeader {
    @Override
    public BoundedArchive<DoubleSolution> initializeLeader(List<DoubleSolution> swarm, BoundedArchive<DoubleSolution> leaders) {
        Check.notNull(swarm);
        Check.that(swarm.size() > 0, "The swarm size is empty: " + swarm.size());
        var leaders_bounded_archive = leaders;
        for(DoubleSolution particle: swarm){
            leaders_bounded_archive.add(particle);
        }
        return leaders_bounded_archive;
    }
}
