package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.initializeLeader;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import java.util.List;

public interface InitializeLeader {
    BoundedArchive<DoubleSolution> initializeLeader(List<DoubleSolution> swarm, BoundedArchive<DoubleSolution> leaders);
}
