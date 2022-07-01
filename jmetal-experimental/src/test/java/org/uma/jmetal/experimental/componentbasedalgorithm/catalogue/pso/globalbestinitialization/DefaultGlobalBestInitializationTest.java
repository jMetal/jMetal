package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

class DefaultGlobalBestInitializationTest {
  @Test
  public void shouldInitializeRaiseAnExceptionIfTheSwarmIsNull() {
    assertThrows(NullParameterException.class, () -> new DefaultGlobalBestInitialization().initialize(null, Mockito.mock(BoundedArchive.class))) ;
  }

  @Test
  public void shouldInitializeRaiseAnExceptionIfTheGlobalBestIsNull() {
    assertThrows(NullParameterException.class, () -> new DefaultGlobalBestInitialization().initialize(new ArrayList<>(), null)) ;
  }

  @Test
  public void shouldInitializeRaiseAnExceptionIfTheSwarmIsEmpty() {
    assertThrows(InvalidConditionException.class, () -> new DefaultGlobalBestInitialization().initialize(new ArrayList<>(), Mockito.mock(BoundedArchive.class))) ;
  }

  @Test
  public void shouldInitializeReturnAGlobalBestArchiveWithASolution() {
    List<DoubleSolution> swarm = new ArrayList<>();
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;

    DoubleSolution particle = problem.createSolution() ;
    swarm.add(particle);

    BoundedArchive<DoubleSolution> archive = Mockito.mock(BoundedArchive.class) ;
    List<DoubleSolution> archiveList = List.of(particle) ;
    Mockito.when(archive.getSolutionList()).thenReturn(archiveList) ;

    BoundedArchive<DoubleSolution> globalBest = new DefaultGlobalBestInitialization().initialize(swarm, archive) ;

    assertEquals(1, globalBest.getSolutionList().size());
    assertSame(particle, globalBest.getSolutionList().get(0));
  }
}