//  ProblemFactoryTest.java
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

package org.uma.test.problem;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Antonio J. Nebro on 21/08/14.
 */
public class ProblemFactoryTest {

  @Test
  public void kursaweProblemTest() {
    Object[] problemParams = {"Real"};
    Problem problem = (new ProblemFactory()).getProblem("Kursawe", problemParams);

    assertNotNull(problem) ;
    assertEquals("Kursawe", problem.getName()) ;
    assertEquals(3, problem.getNumberOfVariables()) ;
    assertEquals(2, problem.getNumberOfObjectives()) ;
    assertEquals(0, problem.getNumberOfConstraints()) ;
  }

  @Test
  public void zdt1ProblemTest() {
    Object[] problemParams = {"Real"};
    Problem problem = (new ProblemFactory()).getProblem("ZDT1", problemParams);

    assertNotNull(problem) ;
    assertEquals("ZDT1", problem.getName()) ;
    assertEquals(30, problem.getNumberOfVariables()) ;
    assertEquals(2, problem.getNumberOfObjectives()) ;
    assertEquals(0, problem.getNumberOfConstraints()) ;
  }

  @Test
  public void lz09F2ProblemTest() {
    Object[] problemParams = {"Real"};
    Problem problem = (new ProblemFactory()).getProblem("LZ09F2", problemParams);

    assertNotNull(problem) ;
    assertEquals("LZ09F2", problem.getName()) ;
    assertEquals(30, problem.getNumberOfVariables()) ;
    assertEquals(2, problem.getNumberOfObjectives()) ;
    assertEquals(0, problem.getNumberOfConstraints()) ;
  }

  @Test
  public void wfg3ProblemTest() {
    Object[] problemParams = {"Real"};
    Problem problem = (new ProblemFactory()).getProblem("WFG3", problemParams);

    assertNotNull(problem) ;
    assertEquals("WFG3", problem.getName()) ;
    assertEquals(6, problem.getNumberOfVariables()) ;
    assertEquals(2, problem.getNumberOfObjectives()) ;
    assertEquals(0, problem.getNumberOfConstraints()) ;
  }

  @Test
  public void dtlz7ProblemTest() {
    Object[] problemParams = {"Real"};
    Problem problem = (new ProblemFactory()).getProblem("DTLZ7", problemParams);

    assertNotNull(problem) ;
    assertEquals("DTLZ7", problem.getName()) ;
    assertEquals(22, problem.getNumberOfVariables()) ;
    assertEquals(3, problem.getNumberOfObjectives()) ;
    assertEquals(0, problem.getNumberOfConstraints()) ;
  }

  @Test
  public void uf5ProblemTest() {
    Object[] problemParams = {"Real"};
    Problem problem = (new ProblemFactory()).getProblem("UF5", problemParams);

    assertNotNull(problem) ;
    assertEquals("UF5", problem.getName()) ;
    assertEquals(30, problem.getNumberOfVariables()) ;
    assertEquals(2, problem.getNumberOfObjectives()) ;
    assertEquals(0, problem.getNumberOfConstraints()) ;
  }

  @Test
  public void waterProblemTest() {
    Object[] problemParams = {"Real"};
    Problem problem = (new ProblemFactory()).getProblem("Water", problemParams);

    assertNotNull(problem) ;
    assertEquals("Water", problem.getName()) ;
    assertEquals(3, problem.getNumberOfVariables()) ;
    assertEquals(5, problem.getNumberOfObjectives()) ;
    assertEquals(7, problem.getNumberOfConstraints()) ;
  }

  @Test
  public void oneMaxProblemTest() {
    Object[] problemParams = {"Binary"};
    Problem problem = (new ProblemFactory()).getProblem("OneMax", problemParams);

    assertNotNull(problem) ;
    assertEquals("OneMax", problem.getName()) ;
    assertEquals(1, problem.getNumberOfVariables()) ;
    assertEquals(1, problem.getNumberOfObjectives()) ;
    assertEquals(0, problem.getNumberOfConstraints()) ;
    assertEquals(512, problem.getLength(0));
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void nonExistingProblemTest() {
    exception.expect(JMetalException.class);

    Object[] problemParams = {"Real"};
    Problem problem = (new ProblemFactory()).getProblem("UnknownProblemName", problemParams);
  }

  @Test (expected = JMetalException.class)
  public void invalidParametersTest() {
    Object[] problemParams = {"noexisting parameter"};
    Problem problem = (new ProblemFactory()).getProblem("ZDT1", problemParams);
  }

}
