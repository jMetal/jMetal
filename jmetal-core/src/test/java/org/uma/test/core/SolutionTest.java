//  SolutionTest.java
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
//  along with this program.  If not, see <http://www.gnu.org/licenses/>


package org.uma.test.core;

import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.problem.multiobjective.IntRealProblem;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.util.JMetalException;

import java.util.Arrays;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Antonio J. Nebro on 10/05/14.
 */
public class SolutionTest {
  Problem problemReal = new Kursawe("Real", 3) ;
  Problem problemIntReal = new IntRealProblem("IntReal", 4, 3) ;
  Problem problemBinary = new OneMax("Binary", 256) ;

  @Test
  public void setup() {
  }

  @Test
  public void comparingVariablesWithIdenticalVariableValues() throws ClassNotFoundException {
    Solution solution1 = new Solution(problemReal) ;
    Solution solution2 = new Solution(problemReal) ;

    assertFalse(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;

    solution2.setDecisionVariables(solution1.getDecisionVariables()) ;
    assertTrue(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;

    solution1 = new Solution(problemIntReal) ;
    solution2 = new Solution(problemIntReal) ;

    assertFalse(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;

    solution2.setDecisionVariables(solution1.getDecisionVariables()) ;
    assertTrue(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;

    solution1 = new Solution(problemBinary) ;
    solution2 = new Solution(problemBinary) ;

    assertFalse(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;

    solution2.setDecisionVariables(solution1.getDecisionVariables()) ;
    assertTrue(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;
  }

  @Test
  public void comparingVariablesWithIdenticalFunctionValues() throws JMetalException, ClassNotFoundException {
    Solution solution1 = new Solution(problemReal) ;
    Solution solution2 = new Solution(problemReal) ;

    problemReal.evaluate(solution1);
    problemReal.evaluate(solution2);
    assertFalse(Arrays.equals(solution1.getObjectives(), solution2.getObjectives())) ;

    solution2 = new Solution(solution1) ;
    assertTrue(Arrays.equals(solution1.getObjectives(), solution2.getObjectives())) ;

    solution1 = new Solution(problemIntReal) ;
    solution2 = new Solution(problemIntReal) ;

    problemIntReal.evaluate(solution1);
    problemIntReal.evaluate(solution2);

    assertFalse(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;

    solution2 = new Solution(solution1) ;
    assertTrue(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;

     
    solution1 = new Solution(problemBinary) ;
    solution2 = new Solution(problemBinary) ;

    problemBinary.evaluate(solution1);
    problemBinary.evaluate(solution2);

    assertFalse(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;

    solution2 = new Solution(solution1) ;
    assertTrue(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;
    
  }

  @Test
  public void comparingEqualSolutions() throws JMetalException, ClassNotFoundException {
    Solution solution1 = new Solution(problemReal) ;
    Solution solution2 = new Solution(problemReal) ;

    problemReal.evaluate(solution1);
    problemReal.evaluate(solution2);
    assertNotEquals(solution1, solution2) ;

    solution2 = new Solution(solution1) ;
    assertEquals(solution1, solution2);

    solution1 = new Solution(problemIntReal) ;
    solution2 = new Solution(problemIntReal) ;

    problemIntReal.evaluate(solution1);
    problemIntReal.evaluate(solution2);

    assertNotEquals(solution1, solution2) ;

    solution2 = new Solution(solution1) ;
    assertEquals(solution1, solution2);

    solution1 = new Solution(problemBinary) ;
    solution2 = new Solution(problemBinary) ;

    problemBinary.evaluate(solution1);
    problemBinary.evaluate(solution2);

    assertNotEquals(solution1, solution2) ;

    solution2 = new Solution(solution1) ;
    assertEquals(solution1, solution2);
  }
}
