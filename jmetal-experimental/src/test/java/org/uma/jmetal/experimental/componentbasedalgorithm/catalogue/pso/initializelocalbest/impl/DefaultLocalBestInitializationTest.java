package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.initializelocalbest.impl;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.initializeVelocity.impl.DefaultVelocityInitialization;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultLocalBestInitializationTest {

  @Test
  public void shouldInitializeRaiseAnExceptionIfTheSwarmIsNull() {
    assertThrows(NullParameterException.class, () -> new DefaultLocalBestInitialization().initialize(null)) ;
  }

  @Test
  public void shouldInitializeReturnAnArrayOfLocalBestParticlesWithTheRightSize() {
    List<DoubleSolution> swarm = new ArrayList<>();
    DoubleProblem problem = new DummyDoubleProblem(3, 2, 0) ;

    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());

    DoubleSolution[] localBests = new DefaultLocalBestInitialization().initialize(swarm) ;

    assertEquals(4, localBests.length);
  }

  @Test
  public void shouldInitializeReturnAnArrayOfLocalBestParticlesEqualToTheOriginalSwarm() {
    List<DoubleSolution> swarm = new ArrayList<>();
    DoubleProblem problem = new DummyDoubleProblem(3, 2, 0) ;

    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());

    DoubleSolution[] localBests = new DefaultLocalBestInitialization().initialize(swarm) ;

    assertEquals(swarm.get(0), localBests[0]);
    assertEquals(swarm.get(1), localBests[1]);
    assertEquals(swarm.get(2), localBests[2]);
    assertEquals(swarm.get(3), localBests[3]);
  }
}