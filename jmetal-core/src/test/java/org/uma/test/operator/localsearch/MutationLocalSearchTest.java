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

package org.uma.test.operator.localsearch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.localSearch.MutationLocalSearch;
import org.uma.jmetal.problem.Kursawe;
import org.uma.jmetal.util.JMetalException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Antonio J. Nebro on 21/04/14.
 */
public class MutationLocalSearchTest {
  MutationLocalSearch localSearch;
  Problem problem;

  static final double DELTA = 0.0000000000001 ;

  @Before
  public void setUp() throws JMetalException {
    problem = new Kursawe("Real", 3) ;
    localSearch = new SBXCrossover.Builder().build() ;
  }

  @After
  public void tearDown() throws Exception {
    crossover = null ;
    problem = null ;
  }

  @Test
  public void defaultParametersTest() {
    assertEquals(20.0, crossover.getDistributionIndex(), DELTA) ;
    assertEquals(0.9, crossover.getCrossoverProbability(), DELTA) ;
  }

  @Test
  public void setCrossoverProbabilityTest() {
    crossover = new SBXCrossover.Builder()
      .probability(1.0)
      .build() ;

    assertEquals(1.0, crossover.getCrossoverProbability(), DELTA) ;
  }

  @Test (expected = JMetalException.class)
  public void setInvalidCrossoverProbabilityTest() {
    crossover = new SBXCrossover.Builder()
      .probability(-2.5)
      .build() ;
  }

  @Test
  public void setCrossoverDistributionIndex() {
    crossover = new SBXCrossover.Builder()
      .distributionIndex(5.0)
      .build() ;

    assertEquals(5.0, crossover.getDistributionIndex(), DELTA) ;
  }

  @Test (expected = JMetalException.class)
  public void setInvalidCrossoverDistributionIndex() {
    crossover = new SBXCrossover.Builder()
      .distributionIndex(-1.25)
      .build() ;
  }

  @Test
  public void operatorExecutionTest() throws ClassNotFoundException {
    crossover = new SBXCrossover.Builder()
      .probability(0.9)
      .distributionIndex(20)
      .build();

    Solution[] parents = {new Solution(problem), new Solution(problem)};

    Object result = crossover.execute(parents);
    assertNotNull(result);
  }
}
