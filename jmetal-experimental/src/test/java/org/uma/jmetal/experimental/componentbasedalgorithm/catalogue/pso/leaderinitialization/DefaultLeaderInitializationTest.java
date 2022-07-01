package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.leaderinitialization;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Test for Default Leader Intialization
 * @author D Doblas Jiménez
 * TODO: Revisar si hay que hacer más tests
 */

class DefaultLeaderInitializationTest {
    @Test
    public void shouldInitializeRaiseAnExceptionIfTheSwarmIsNull() {
        //assertThrows(NullParameterException.class, () -> new DefaultGlobalBestInitialization().initialize(null)) ;
    }

    @Test
    public void shouldInitializeRaiseAnExceptionIfTheSwarmIsEmpty() {
        List<DoubleSolution> swarm = new ArrayList<>() ;
        //assertThrows(InvalidConditionException.class, () -> new DefaultVelocityInitialization().initialize(swarm)) ;
    }

    /*
    @Test
    public void shouldInitializeLeadersHaveTheParticleAdded(){
        List<DoubleSolution> swarm = new ArrayList<>();
        DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;
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