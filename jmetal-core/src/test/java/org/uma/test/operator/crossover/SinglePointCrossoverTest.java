//  SBXCrossoverTest.java
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

package org.uma.test.operator.crossover;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.operator.crossover.SinglePointCrossover;
import org.uma.jmetal45.problem.multiobjective.zdt.ZDT5;
import org.uma.jmetal45.util.JMetalException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Antonio J. Nebro on 21/04/14.
 */
public class SinglePointCrossoverTest {
  SinglePointCrossover crossover;
  Problem problem;

  static final double DELTA = 0.0000000000001 ;

  @Before
  public void setUp() throws JMetalException {
    problem = new ZDT5("Binary", 4) ;
    crossover = new SinglePointCrossover.Builder()
            .setProbability(0.9)
            .build() ;
  }

  @After
  public void tearDown() throws Exception {
    crossover = null ;
    problem = null ;
  }

  @Test
  public void defaultParametersTest() {
    assertEquals(0.9, crossover.getCrossoverProbability(), DELTA) ;
  }

  @Test
  public void setCrossoverProbabilityTest() {
    crossover = new SinglePointCrossover.Builder()
            .setProbability(1.0)
            .build() ;

    assertEquals(1.0, crossover.getCrossoverProbability(), DELTA) ;
  }

  @Test (expected = JMetalException.class)
  public void setInvalidCrossoverProbabilityTest() {
    crossover = new SinglePointCrossover.Builder()
            .setProbability(-2.5)
            .build() ;
  }

  @Test (expected = JMetalException.class)
  public void passingNullParameterTest() {
    crossover.execute(null) ;
  }

  @Test
  public void operatorExecutionTest() throws ClassNotFoundException {
    crossover = new SinglePointCrossover.Builder()
            .setProbability(0.9)
            .build();

    Solution[] parents = {new Solution(problem), new Solution(problem)};

    Object result = crossover.execute(parents);
    assertNotNull(result);
  }
}
