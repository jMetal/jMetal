package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.leaderinitialization;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import java.util.List;

public interface LeaderInitialization {
    BoundedArchive<DoubleSolution> initializeLeader(List<DoubleSolution> swarm, BoundedArchive<DoubleSolution> leaders);
}
