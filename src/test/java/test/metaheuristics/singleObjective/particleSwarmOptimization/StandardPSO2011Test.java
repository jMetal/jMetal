//  StandardPSO2011Test.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
    standardPSO_ = new StandardPSO2011() ;
    standardPSO_.setProblem(problem_);
    standardPSO_.setInputParameter("swarmSize",40);
    standardPSO_.setInputParameter("maxIterations",5000);
    standardPSO_.setInputParameter("numberOfParticlesToInform",3);
    standardPSO_.initParams();
  }

  @Test
  public void initParamsTest() {
    assertEquals("StandardPSO2011Test.initParamsTest", 40, ((Integer) standardPSO_.getInputParameter("swarmSize")).intValue());
    assertEquals("StandardPSO2011Test.initParamsTest", 5000, ((Integer) standardPSO_.getInputParameter("maxIterations")).intValue());
    assertEquals("StandardPSO2011Test.initParamsTest", 3, ((Integer) standardPSO_.getInputParameter("numberOfParticlesToInform")).intValue());
  }

/*  @Test
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
  }*/

  @Test
  public void getNeighborWithMinimumFitnessTest() {

  }


  @After
  public void tearDown() throws Exception {
    problem_ = null;
    standardPSO_ = null ;
  }
}
