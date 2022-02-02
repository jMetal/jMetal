package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.initializeLeader.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.initializeVelocity.impl.DefaultVelocityInitialization;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * Test for Default Leader Intialization
 * @author D Doblas Jiménez
 * TODO: Revisar si hay que hacer más tests
 */

class DefaultLeaderInitializationTest {
    @Test
    public void shouldInitializeRaiseAnExceptionIfTheSwarmIsNull() {
        assertThrows(NullParameterException.class, () -> new DefaultVelocityInitialization().initialize(null)) ;
    }

    @Test
    public void shouldInitializeRaiseAnExceptionIfTheSwarmIsEmpty() {
        List<DoubleSolution> swarm = new ArrayList<>() ;
        assertThrows(InvalidConditionException.class, () -> new DefaultVelocityInitialization().initialize(swarm)) ;
    }

    /*
    @Test
    public void shouldInitializeLeadersHaveTheParticleAdded(){
        List<DoubleSolution> swarm = new ArrayList<>();
        DoubleProblem problem = new DummyDoubleProblem(3, 2, 0) ;
        BoundedArchive<DoubleSolution> leaders_bounded;
        swarm.add(problem.createSolution());
        swarm.add(problem.createSolution());
        swarm.add(problem.createSolution());
        swarm.add(problem.createSolution());

        BoundedArchive<DoubleSolution> leaders = new DefaultLeaderInitialization().initializeLeader(swarm, leaders_bounded);
        System.out.println("LEADERSS");
        System.out.println(leaders);
    }

     */
}