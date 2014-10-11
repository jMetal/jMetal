//  NullCrossoverTest.java
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
import org.uma.jmetal45.operator.crossover.NullCrossover;
import org.uma.jmetal45.problem.multiobjective.Kursawe;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.comparator.SolutionComparator;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 21/04/14.
 */
public class NullCrossoverTest {
  NullCrossover crossover;
  Problem problem;

  static final double DELTA = 0.0000000000001 ;

  @Before
  public void setUp() throws JMetalException {
    problem = new Kursawe("Real", 3) ;
    crossover = new NullCrossover.Builder()
      .build() ;
  }

  @After
  public void tearDown() throws Exception {
    crossover = null ;
    problem = null ;
  }

  @Test
  public void executeWithCorrectParameters() throws ClassNotFoundException {
    Solution[] parent = {new Solution(problem), new Solution(problem)};

    Solution[] child = (Solution[]) crossover.execute(parent) ;

    SolutionComparator comparator = new SolutionComparator() ;

    assertEquals(0.0, comparator.compare(parent[0], child[0]), DELTA);
    assertEquals(0.0, comparator.compare(parent[1], child[1]), DELTA);
  }

  @Test (expected = JMetalException.class)
  public void wrongNumberOfParametersTest() throws ClassNotFoundException {
    Solution[] parent = {new Solution(problem)};

    crossover.execute(parent) ;
  }

  @Test (expected = JMetalException.class)
  public void wrongParameterClassTest() throws ClassNotFoundException {
    Solution parent = new Solution(problem) ;

    crossover.execute(parent) ;
  }

    @Test (expected = JMetalException.class)
  public void nullParameterTest() throws ClassNotFoundException {
    crossover.execute(null) ;
  }
}
