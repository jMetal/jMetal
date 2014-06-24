//  SolutionSetTest.java
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
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.problem.Kursawe;
import org.uma.jmetal.util.JMetalException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 10/06/13
 * Time: 07:41
 */

public class SolutionSetTest {
  int maxSize_ = 10 ;
  SolutionSet solutionSet_ ;

  @Before
  public void setUp() throws Exception {
    solutionSet_ = new SolutionSet(maxSize_) ;
  }

  @After
  public void tearDown() throws Exception {
    solutionSet_ = null ;
  }

  /**
   * Test: Check that adding a new solutiontype to an empty solutiontype set leads this one to have 1 element
   * @throws Exception
   */
  @Test
  public void testAddOneElementToAnEmptySolutionSet() throws Exception {
    boolean result ;
    result = solutionSet_.add(new Solution()) ;
    assertEquals("SolutionSetTest", 1, solutionSet_.size()) ;
    assertTrue("SolutionSetTest", result);
  }

  /**
   * Test: Adding an element to a full solutiontype set must return a false value because it is not added
   * @throws org.uma.jmetal.util.JMetalException
   */
  @Test (expected = JMetalException.class)
  public void testAddOneElementToAFullSolutionSet() throws JMetalException {
    for (int i = 0 ; i < maxSize_ ; i++) {
      solutionSet_.add(new Solution());
    }

    solutionSet_.add(new Solution()) ;
  }

  /**
   * Test: Getting an element out of founds must raise an exception
   * @throws Exception
   */
  @Test  (expected = IndexOutOfBoundsException.class)
  public void testGetElementOutOfBounds() throws Exception {
    for (int i = 0 ; i < 5 ; i++)
      solutionSet_.add(new Solution()) ;

    Solution solution = solutionSet_.get(7) ;
  }

  @Test
  public void testGetMaxSize() {
    assertEquals("SolutionSetTest", 10, solutionSet_.getMaxSize());
  }

  @Test
  public void testEquals() throws JMetalException, ClassNotFoundException {
    Problem problem = new Kursawe("Real", 3) ;
    SolutionSet solutionSet1 = new SolutionSet(10) ;
    SolutionSet solutionSet2 = new SolutionSet(10) ;
    //SolutionSet solutionSet3 = new SolutionSet(10) ;
    Solution solution = null ;

    for (int i = 0 ; i < 10; i++) {
      solution = new Solution(problem) ;
      problem.evaluate(solution);
      solutionSet1.add(solution) ;
      //solutionSet3.add(new Solution(solutiontype)) ;
    }

    solutionSet2.add(new Solution(problem)) ;

    assertFalse(solutionSet1.equals(null)) ;
    assertFalse(solutionSet1.equals(solution)) ;
    assertTrue(solutionSet1.equals(solutionSet1)) ;
    assertFalse(solutionSet1.equals(solutionSet2)) ;

    //assertTrue(solutionSet1.equals(solutionSet3)) ;
  }

  @Test
  public void isEmptyTest() throws JMetalException {
    assertTrue(solutionSet_.isEmtpy()) ;

    solutionSet_.add(new Solution()) ;
    assertFalse(solutionSet_.isEmtpy()) ;
  }
}
