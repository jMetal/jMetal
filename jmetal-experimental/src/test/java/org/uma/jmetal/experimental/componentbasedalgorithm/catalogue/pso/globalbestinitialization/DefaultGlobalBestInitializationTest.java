package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestinitialization.impl.DefaultLocalBestInitialization;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultGlobalBestInitializationTest {
  @Test
  public void shouldInitializeRaiseAnExceptionIfTheSwarmIsNull() {
    assertThrows(NullParameterException.class, () -> new DefaultGlobalBestInitialization().initialize(null, new CrowdingDistanceArchive<>(1))) ;
  }

  @Test
  public void shouldInitializeRaiseAnExceptionIfTheGlobalBestIsNull() {
    assertThrows(NullParameterException.class, () -> new DefaultGlobalBestInitialization().initialize(new ArrayList<>(), null)) ;
  }

  @Test
  public void shouldInitializeRaiseAnExceptionIfTheSwarmIsEmpty() {
    assertThrows(InvalidConditionException.class, () -> new DefaultGlobalBestInitialization().initialize(new ArrayList<>(), new CrowdingDistanceArchive<>(1))) ;
  }

  @Test
  public void shouldInitializeReturnAGlobalBestArchiveWithASolution() {
    List<DoubleSolution> swarm = new ArrayList<>();
    DoubleProblem problem = new DummyDoubleProblem(3, 2, 0) ;

    DoubleSolution particle = problem.createSolution() ;
    swarm.add(particle);

    BoundedArchive<DoubleSolution> globalBest = new DefaultGlobalBestInitialization().initialize(swarm, new CrowdingDistanceArchive<>(10)) ;

    assertEquals(1, globalBest.getSolutionList().size());
    assertSame(particle, globalBest.getSolutionList().get(0));
  }
}