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
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operator.crossover.HUXCrossover;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT5;
import org.uma.jmetal.util.JMetalException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Antonio J. Nebro on 27/06/14.
 */
public class HUXCrossoverTest {
  static final double DELTA = 0.0000000000001 ;

  HUXCrossover crossover ;

  @Before
  public void setUp() throws JMetalException, ClassNotFoundException {
    crossover = new HUXCrossover.Builder()
      .build() ;
  }

  @After
  public void tearDown() throws Exception {
    crossover = null ;
  }

  @Test
  public void defaultParametersTest() {
    assertEquals(1.0, crossover.getCrossoverProbability(), DELTA) ;
  }

  @Test
  public void setCrossoverProbabilityTest() {
    crossover = new HUXCrossover.Builder()
      .probability(0.7)
      .build() ;

    assertEquals(0.7, crossover.getCrossoverProbability(), DELTA) ;
  }

  @Test (expected = JMetalException.class)
  public void setInvalidCrossoverProbabilityTest() {
    crossover = new HUXCrossover.Builder()
      .probability(-2.5)
      .build() ;
  }

  @Test (expected = JMetalException.class)
  public void invalidNumberOfArgumentsTest() throws ClassNotFoundException {
    crossover = new HUXCrossover.Builder()
      .probability(0.9)
      .build();

    Solution[] parents = {new Solution(new ZDT5("Binary"))};

    crossover.execute(parents);
  }

  @Test
  public void operatorExecutionTest() throws ClassNotFoundException {
    crossover = new HUXCrossover.Builder()
      .probability(0.9)
      .build();

    Solution[] parents = {new Solution(new ZDT5("Binary")), new Solution(new ZDT5("Binary"))};

    Object result = crossover.execute(parents);
    assertNotNull(result);
  }
}
