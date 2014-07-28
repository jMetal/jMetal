//  OperatorTest.java
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

import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.BinarySolutionType;
import org.uma.jmetal.encoding.solutiontype.IntSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.problem.Water;
import org.uma.jmetal.problem.zdt.ZDT1;
import org.uma.jmetal.problem.zdt.ZDT5;
import org.uma.jmetal.problem.singleObjective.OneMax;
import org.uma.jmetal.util.JMetalException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Antonio J. Nebro on 14/06/14.
 */
public class OperatorTest {
  private Operator operator_ ;
  private Problem problem_ ;

  @Before
  public void startup() throws ClassNotFoundException {
    operator_ = new Operator() {
      @Override public Object execute(Object object) throws JMetalException {
        return null;
      }
    } ;

    problem_ = new ZDT1("Real") ;
  }

  @Test
  public void accessingEmptyValidSolutionTypeListTest() throws ClassNotFoundException {
    Solution solution = new Solution(problem_) ;
    assertFalse(operator_.solutionTypeIsValid(solution)) ;

    Solution[] solutions = {new Solution(problem_), new Solution(problem_), new Solution(problem_)} ;
    assertFalse(operator_.solutionTypeIsValid(solutions)) ;
  }

  @Test
  public void addingOneValidTypeTest() throws ClassNotFoundException {
    operator_.addValidSolutionType(RealSolutionType.class);

    assertTrue(operator_.solutionTypeIsValid(new Solution(new ZDT1("Real")))) ;
    assertFalse(operator_.solutionTypeIsValid(new Solution(new OneMax("Binary")))) ;

    Solution[] solutions = {new Solution(problem_), new Solution(problem_), new Solution(problem_)} ;
    assertTrue(operator_.solutionTypeIsValid(solutions)) ;
  }

  @Test
  public void validatingArrayOfSolutionsTest() throws Exception {
    operator_.addValidSolutionType(RealSolutionType.class);
    operator_.addValidSolutionType(BinarySolutionType.class);
    operator_.addValidSolutionType(IntSolutionType.class);

    Solution[] solutions = {
      new Solution(new ZDT1("Real")),
      new Solution(new ZDT5("Binary")),
      new Solution(new Water("Real"))} ;

    assertTrue(operator_.solutionTypeIsValid(solutions)) ;
  }

  @Test
  public void validatingArrayWithInvalidSolutionTypeTest() throws Exception {
    operator_.addValidSolutionType(RealSolutionType.class);
    operator_.addValidSolutionType(BinarySolutionType.class);
    operator_.addValidSolutionType(IntSolutionType.class);

    Solution[] solutions = {
      new Solution(new ZDT1("Real")),
      new Solution(new ZDT5("Binary")),
      new Solution(new Water("BinaryReal"))} ;

    assertFalse(operator_.solutionTypeIsValid(solutions)) ;
  }
}
