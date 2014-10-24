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

package org.uma.test.metaheuristic.singleobjective.particleswarmoptimization;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.metaheuristic.singleobjective.particleswarmoptimization.StandardPSO2011;
import org.uma.jmetal.problem.singleobjective.Sphere;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 15/03/14.
 */
public class StandardPSO2011Test {

  StandardPSO2011 standardPSO;
  Problem problem;

  @Before
  public void setUp() throws Exception {
    problem = new Sphere("Real", 30) ;
    standardPSO = new StandardPSO2011.Builder(problem)
    .setSwarmSize(40)
    .setMaxIterations(5000)
    .setNumberOfParticlesToInform(3)
    .build() ;
  }

  @Test
  public void parameterTest() {
    assertEquals(40, ((StandardPSO2011)standardPSO).getSwarmSize());
    assertEquals(5000,((StandardPSO2011)standardPSO).getMaxIterations());
    assertEquals(3, ((StandardPSO2011)standardPSO).getNumberOfParticlesToInform());
  }
  
  @Test
  public void valuesOfWeightAndCTest() {
    double offset = 0.001 ;
    assertEquals(1.193, standardPSO.getC(), offset);
    assertEquals(0.721, standardPSO.getWeight(), offset);
  }

/*  @Test
  public void neighbourhoodMethodTest() {
    int [] neighbours;
    neighbours = standardPSO.getNeighbourhood(0) ;

    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 39, neighbours[0]) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 0, neighbours[1]) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 1, neighbours[2]) ;

    neighbours = standardPSO.getNeighbourhood(1) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 0, neighbours[0]) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 1, neighbours[1]) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 2, neighbours[2]) ;

    neighbours = standardPSO.getNeighbourhood(39) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 38, neighbours[0]) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 39, neighbours[1]) ;
    assertEquals("StandardPSO2011Test.neighbourhoodMethodTest", 0, neighbours[2]) ;
  }

  @Test
  public void getNeighborWithMinimumFitnessTest() {

  }

*/
  @After
  public void tearDown() throws Exception {
    problem = null;
    standardPSO = null ;
  }
}
