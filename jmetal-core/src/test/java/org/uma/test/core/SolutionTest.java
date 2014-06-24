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

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.problem.Kursawe;
import org.uma.jmetal.util.JMetalException;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

/**
 * Created by Antonio J. Nebro on 10/05/14.
 */
public class SolutionTest {

  @Test
  public void setDecisionVariablesTest() throws JMetalException, ClassNotFoundException {
    Problem problem = new Kursawe("Real", 3) ;
    Solution solution1 = new Solution(problem) ;
    Solution solution2 = new Solution(problem) ;

    assertFalse(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;

    solution2.setDecisionVariables(solution1.getDecisionVariables()) ;
    //assertArrayEquals(solution1.getDecisionVariables(), solution2.getDecisionVariables()) ;
    assertTrue(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;

  }
}
