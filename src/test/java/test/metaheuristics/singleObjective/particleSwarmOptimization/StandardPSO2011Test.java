package test.metaheuristics.singleObjective.particleSwarmOptimization;

import jmetal.core.Problem;
import jmetal.metaheuristics.singleObjective.particleSwarmOptimization.StandardPSO2011;
import jmetal.problems.Fonseca;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 15/03/14.
 */
public class StandardPSO2011Test {

  StandardPSO2011 standardPSO_ ;
  Problem problem_ ;

  @Before
  public void setUp() throws Exception {
    problem_ = new Fonseca("Real") ;
    standardPSO_ = new StandardPSO2011(problem_) ;
    standardPSO_.setInputParameter("swarmSize",40);
    standardPSO_.setInputParameter("maxIterations",5000);
    standardPSO_.initParams();
  }

  @Test
  public void neighbourhoodMethodTest() {
    int [] neighbours;
    neighbours = standardPSO_.getNeighbourhood(0) ;

    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 39, neighbours[0]) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 0, neighbours[1]) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 1, neighbours[2]) ;

    neighbours = standardPSO_.getNeighbourhood(1) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 0, neighbours[0]) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 1, neighbours[1]) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 2, neighbours[2]) ;

    neighbours = standardPSO_.getNeighbourhood(39) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 38, neighbours[0]) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 39, neighbours[1]) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 0, neighbours[2]) ;
  }

  @After
  public void tearDown() throws Exception {
    problem_ = null;
    standardPSO_ = null ;
  }
}
