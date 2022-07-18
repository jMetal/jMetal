package org.uma.jmetal.component.catalogue.pso.localbestinitialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.impl.DefaultLocalBestInitialization;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

class DefaultLocalBestInitializationTest {

  @Test
  void initializeRaisesAnExceptionIfTheSwarmIsNull() {
    assertThrows(NullParameterException.class, () -> new DefaultLocalBestInitialization().initialize(null)) ;
  }

  @Test
  void shouldInitializeReturnsAnArrayOfLocalBestParticlesWithTheRightSize() {
    List<DoubleSolution> swarm = new ArrayList<>();
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;

    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());

    var localBests = new DefaultLocalBestInitialization().initialize(swarm) ;

    assertEquals(4, localBests.length);
  }

  @Test
  void initializeReturnsAnArrayOfLocalBestParticlesEqualToTheOriginalSwarm() {
    List<DoubleSolution> swarm = new ArrayList<>();
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;

    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());

    var localBests = new DefaultLocalBestInitialization().initialize(swarm) ;

    assertEquals(swarm.get(0), localBests[0]);
    assertEquals(swarm.get(1), localBests[1]);
    assertEquals(swarm.get(2), localBests[2]);
    assertEquals(swarm.get(3), localBests[3]);
  }
}