//  BinaryUtils.java
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

package org.uma.test.util;

import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.problem.Fonseca;
import org.uma.jmetal.problem.singleObjective.OneMax;
import org.uma.jmetal.util.BinaryUtils;
import org.uma.jmetal.util.JMetalException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 08/08/14.
 */
public class BinaryUtilsTest {

  @Test
  public void binarySolutionTypeTest() throws ClassNotFoundException {
    Problem problem = new OneMax("Binary", 512) ;
    Solution solution = new Solution(problem) ;

    assertEquals(512, BinaryUtils.getNumberOfBits(solution)) ;
  }

  @Test
  public void binaryRealSolutionTypeTest() throws ClassNotFoundException {
    Problem problem = new Fonseca("BinaryReal") ;
    Solution solution = new Solution(problem) ;

    int expected = org.uma.jmetal.encoding.variable.BinaryReal.DEFAULT_PRECISION * problem.getNumberOfVariables() ;

    assertEquals(expected, BinaryUtils.getNumberOfBits(solution)) ;
  }

  @Test (expected = JMetalException.class)
  public void wrongSolutionTypeTest() throws ClassNotFoundException {
    Problem problem = new Fonseca("Real") ;
    Solution solution = new Solution(problem) ;

    assertEquals(512, BinaryUtils.getNumberOfBits(solution)) ;
  }
}
