package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityinitialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityinitialization.impl.DefaultVelocityInitialization;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

class DefaultVelocityInitializationTest {

  @Test
  public void shouldInitializeRaiseAnExceptionIfTheSwarmIsNull() {
    assertThrows(NullParameterException.class, () -> new DefaultVelocityInitialization().initialize(null)) ;
  }

  @Test
  public void shouldInitializeRaiseAnExceptionIfTheSwarmIsEmpty() {
    List<DoubleSolution> swarm = new ArrayList<>() ;
    assertThrows(InvalidConditionException.class, () -> new DefaultVelocityInitialization().initialize(swarm)) ;
  }

  @Test
  public void shouldInitializeReturnASpeedMatrixWithTheRightDimensions() {
    List<DoubleSolution> swarm = new ArrayList<>();
    DoubleProblem problem = new DummyDoubleProblem(3, 2, 0) ;

    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());

    double[][] speed = new DefaultVelocityInitialization().initialize(swarm) ;
    assertEquals(4, speed.length) ;
    assertEquals(3, speed[0].length) ;
  }

  @Test
  public void shouldInitializeASpeedMatrixFullOfZeroes() {
    List<DoubleSolution> swarm = new ArrayList<>();
    DoubleProblem problem = new DummyDoubleProblem(2, 2, 0) ;

    swarm.add(problem.createSolution());
    swarm.add(problem.createSolution());

    double[][] speed = new DefaultVelocityInitialization().initialize(swarm) ;
    assertEquals(0.0, speed[0][0]) ;
    assertEquals(0.0, speed[0][1]) ;
    assertEquals(0.0, speed[1][0]) ;
    assertEquals(0.0, speed[1][1]) ;
  }
}