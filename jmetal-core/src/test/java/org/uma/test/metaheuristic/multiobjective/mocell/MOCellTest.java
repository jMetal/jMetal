//  MOCellTest.java
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

package org.uma.test.metaheuristic.multiobjective.mocell;

import org.junit.Test;
import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.experiment.settings.MOCellSettings;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Antonio J. Nebro on 30/06/14.
 */
public class MOCellTest {
  Algorithm algorithm;

  @Test
  public void testNumberOfReturnedSolutionsInEasyProblemWithAsyncMOCell1() throws IOException, ClassNotFoundException {
    algorithm = new MOCellSettings("ZDT2").configure("AsyncMOCell1") ;

    SolutionSet solutionSet = algorithm.execute() ;
    /*
    Rationale: the default problem is ZDT2, and usually AsyncMOCell1, configured with standard
    settings, should return 100 solutions
     */
    assertTrue(solutionSet.size() >= 98) ;
  }

  @Test
  public void testNumberOfReturnedSolutionsInEasyProblemWithAsyncMOCell2() throws IOException, ClassNotFoundException {
    algorithm = new MOCellSettings("ZDT1").configure("AsyncMOCell2") ;

    SolutionSet solutionSet = algorithm.execute() ;
    /*
    Rationale: the default problem is ZDT1, and usually AsyncMOCell2, configured with standard
    settings, should return 100 solutions
     */
    assertTrue(solutionSet.size() >= 98) ;
  }

  @Test
  public void testNumberOfReturnedSolutionsInEasyProblemWithAsyncMOCell3() throws IOException, ClassNotFoundException {
    algorithm = new MOCellSettings("ZDT2").configure("AsyncMOCell3") ;

    SolutionSet solutionSet = algorithm.execute() ;
    /*
    Rationale: the default problem is ZDT1, and usually AsyncMOCell3, configured with standard
    settings, should return 100 solutions
     */
    assertTrue(solutionSet.size() >= 98) ;
  }

  @Test
  public void testNumberOfReturnedSolutionsInEasyProblemWithAsyncMOCell4() throws IOException, ClassNotFoundException {
    algorithm = new MOCellSettings("ZDT4").configure("AsyncMOCell4") ;

    SolutionSet solutionSet = algorithm.execute() ;
    /*
    Rationale: the default problem is ZDT1, and usually AsyncMOCell4, configured with standard
    settings, should return 100 solutions
     */
    assertTrue(solutionSet.size() >= 98) ;
  }

  @Test
  public void testNumberOfReturnedSolutionsInEasyProblemWithSyncMOCell1() throws IOException, ClassNotFoundException {
    algorithm = new MOCellSettings("Fonseca").configure("SyncMOCell1") ;

    SolutionSet solutionSet = algorithm.execute() ;
    /*
    Rationale: the default problem is Fonseca, and usually SyncMOCell1, configured with standard
    settings, should return 100 solutions
     */
    assertTrue(solutionSet.size() >= 98) ;
  }

  public void testNumberOfReturnedSolutionsInEasyProblemWithSyncMOCell2() throws IOException, ClassNotFoundException {
    algorithm = new MOCellSettings("ZDT2").configure("SyncMOCell2") ;

    SolutionSet solutionSet = algorithm.execute() ;
    /*
    Rationale: the default problem is ZDT2, and usually SyncMOCell2, configured with standard
    settings, should return 100 solutions
     */
    assertTrue(solutionSet.size() >= 98) ;
  }
}
