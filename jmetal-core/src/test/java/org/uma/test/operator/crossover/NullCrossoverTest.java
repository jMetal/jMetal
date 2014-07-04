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
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operator.crossover.NullCrossover;
import org.uma.jmetal.problem.Kursawe;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.SolutionComparator;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 21/04/14.
 */
public class NullCrossoverTest {
  NullCrossover crossover_ ;
  Problem problem_ ;

  static final double DELTA = 0.0000000000001 ;

  @Before
  public void setUp() throws JMetalException {
    problem_ = new Kursawe("Real", 3) ;
    crossover_ = new NullCrossover.Builder().build() ;
  }

  @After
  public void tearDown() throws Exception {
    crossover_ = null ;
    problem_ = null ;
  }

  @Test
  public void executeWithCorrectParameters() throws ClassNotFoundException {
    Solution[] parent = {new Solution(problem_), new Solution(problem_)};

    Solution[] child = (Solution[])crossover_.execute(parent) ;

    SolutionComparator comparator = new SolutionComparator() ;

    assertEquals(0.0, comparator.compare(parent[0], child[0]), 0.0000000000001);
    assertEquals(0.0, comparator.compare(parent[1], child[1]), 0.0000000000001);
  }

  @Test (expected = JMetalException.class)
  public void wrongNumberOfParametersTest() throws ClassNotFoundException {
    Solution[] parent = {new Solution(problem_)};

    crossover_.execute(parent) ;
  }

  @Test (expected = JMetalException.class)
  public void nullParameterTest() throws ClassNotFoundException {
    crossover_.execute(null) ;
  }
}
