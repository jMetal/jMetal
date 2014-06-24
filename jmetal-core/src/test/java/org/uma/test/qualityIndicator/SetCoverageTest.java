//  SetCoverageTest.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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

package org.uma.test.qualityIndicator;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.problem.Kursawe;
import org.uma.jmetal.qualityIndicator.SetCoverage;
import org.uma.jmetal.util.JMetalException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Antonio J. Nebro on 31/05/14.
 */
public class SetCoverageTest {
  private static final double EPSILON = 0.0000000000001 ;
  private SolutionSet solutionSet1_ ;
  private SolutionSet solutionSet2_ ;
  private int solutionSetSize_ ;
  private Problem problem_ ;


  @Before
  public void startup() throws JMetalException {
    problem_ = new Kursawe("Real", 3) ;
    solutionSetSize_ = 4 ;
    solutionSet1_ = new SolutionSet(solutionSetSize_) ;
    solutionSet2_ = new SolutionSet(solutionSetSize_) ;
  }

  @Test
  public void emptySetsTest() {
    assertEquals(0.0, new SetCoverage().setCoverage(solutionSet2_, solutionSet1_), EPSILON) ;
  }

  @Test
  public void fullCoverageTest() throws ClassNotFoundException, JMetalException {
    // Creating set (0,N-1), (1,N-2), (2, N-3), ... , (N-1, 0)
    Solution solution ;
    for (int i = 0 ; i< solutionSetSize_; i++) {
      solution = new Solution(problem_);
      solution.setObjective(0, i);
      solution.setObjective(1, solutionSetSize_ - 1 - i);
      solutionSet1_.add(solution);
    }
    // Creating set (0.5,N), (1.5, N-1), (2.5, N-2), ... , (N-1+0.5, 1)
    for (int i = 0 ; i< solutionSetSize_; i++) {
      solution = new Solution(problem_);
      solution.setObjective(0, i+0.5);
      solution.setObjective(1, solutionSetSize_ - i);
      solutionSet2_.add(solution);
    }

    assertEquals(1.0, new SetCoverage().setCoverage(solutionSet1_, solutionSet2_), EPSILON) ;
    assertEquals(0.0, new SetCoverage().setCoverage(solutionSet2_, solutionSet1_), EPSILON) ;
  }

  @Test
  public void nonDominatedSetsCoverageTest() throws ClassNotFoundException, JMetalException {
    // Creating set (0,N-1), (1,N-2), (2, N-3), ... , (N-1, 0)
    Solution solution ;
    for (int i = 0 ; i< solutionSetSize_; i++) {
      solution = new Solution(problem_);
      solution.setObjective(0, i);
      solution.setObjective(1, solutionSetSize_ - 1 - i);
      solutionSet1_.add(solution);
    }
    // Creating set (0.5,N), (1.5, N-1), (2.5, N-2), ... , (N-1+0.5, 1)
    for (int i = 0 ; i< solutionSetSize_; i++) {
      solution = new Solution(problem_);
      solution.setObjective(0, i+0.5);
      solution.setObjective(1, solutionSetSize_ - i);
      solutionSet2_.add(solution);
    }

    /*
    Configuration.logger_.info("Set1");
    solutionSet1_.printObjectives();
    Configuration.logger_.info("Set2");
    solutionSet2_.printObjectives();
    */
    // Modifying a solutiontype to make it non-dominated with respect to solutionset 1
    solutionSet2_.get(0).setObjective(0, -1.0);
    solutionSet2_.get(0).setObjective(1, solutionSetSize_-1.2);
    /*
    Configuration.logger_.info("Set22222");
    solutionSet2_.printObjectives();
    */
    assertNotEquals(1.0, new SetCoverage().setCoverage(solutionSet2_, solutionSet1_), EPSILON) ;
    assertNotEquals(1.0, new SetCoverage().setCoverage(solutionSet1_, solutionSet2_), EPSILON) ;
  }

  @Test
  public void coverageWhenASetIsEmpty() throws ClassNotFoundException, JMetalException {
    // Creating set (0,N-1), (1,N-2), (2, N-3), ... , (N-1, 0)
    Solution solution ;
    for (int i = 0 ; i< solutionSetSize_; i++) {
      solution = new Solution(problem_);
      solution.setObjective(0, i);
      solution.setObjective(1, solutionSetSize_ - 1 - i);
      solutionSet1_.add(solution);
    }

    // The second solutiontype set is empty. Try the tests
    assertEquals(1.0, new SetCoverage().setCoverage(solutionSet1_, solutionSet2_), EPSILON) ;
    assertEquals(0.0, new SetCoverage().setCoverage(solutionSet2_, solutionSet1_), EPSILON) ;
  }

  @Test
  public void transformArraysToSolutionSetTest() throws JMetalException {
    // Creating set (0,N-1), (1,N-2), (2, N-3), ... , (N-1, 0)
    double[][] arrayContainingFront = new double[solutionSetSize_][2];
    for (int i = 0; i < solutionSetSize_; i++) {
      arrayContainingFront[i][0] = i;
      arrayContainingFront[i][1] = solutionSetSize_ - 1 - i;
    }

    SolutionSet solutionSet = new SetCoverage().transformArraysToSolutionSet(arrayContainingFront);
    assertEquals(0.0, solutionSet.get(0).getObjective(0), EPSILON) ;
    assertEquals(solutionSetSize_-1, solutionSet.get(0).getObjective(1), EPSILON) ;
    assertEquals(solutionSetSize_-1, solutionSet.get(solutionSetSize_-1).getObjective(0), EPSILON) ;
    assertEquals(0.0, solutionSet.get(solutionSetSize_-1).getObjective(1), EPSILON) ;
  }


  @After
  public void teardown() throws JMetalException {
    problem_ = null ;
    solutionSet1_ = null ;
    solutionSet2_ = null ;
  }

}
