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
import org.uma.jmetal.operator.localSearch.MutationLocalSearch;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Kursawe;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.CrowdingArchive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Antonio J. Nebro on 21/04/14.
 */
public class MutationLocalSearchTest {
  MutationLocalSearch localSearch;
  Problem problem;
  Mutation mutation ;
  Archive archive ;
  static final double DELTA = 0.0000000000001 ;

  @Before
  public void setUp() throws JMetalException {
    problem = new Kursawe("Real", 3) ;
    mutation = new PolynomialMutation.Builder()
            .distributionIndex(20)
            .probability(1.0/problem.getNumberOfVariables())
            .build();

    archive = new CrowdingArchive(100, problem.getNumberOfObjectives()) ;

    localSearch = new MutationLocalSearch.Builder(problem)
            .mutationOperator(mutation)
            .improvementRounds(1)
            .archive(archive)
            .build() ;
  }

  @After
  public void tearDown() throws Exception {
    problem = null ;
    mutation = null ;
    archive = null ;
    localSearch = null ;
  }

  @Test
  public void defaultParametersTest() {
    assertEquals(mutation, localSearch.getMutationOperator()) ;
    assertEquals(archive, localSearch.getArchive()) ;
    assertEquals(1, localSearch.getImprovementRounds()) ;
  }

  @Test
  public void operatorExecutionTest() throws ClassNotFoundException {
    Solution solution = new Solution(problem) ;
    problem.evaluate(solution);

    Solution result = (Solution) localSearch.execute(solution);
    assertNotNull(result);
    //assertNotEquals(solution, result);
  }

  @Test (expected = JMetalException.class)
  public void wrongParameterClassTest() throws ClassNotFoundException {
    Solution[] parent = {new Solution(problem), new Solution(problem)};

    localSearch.execute(parent) ;
  }

  @Test (expected = JMetalException.class)
  public void nullParameterTest() throws ClassNotFoundException {
    localSearch.execute(null) ;
  }
}
