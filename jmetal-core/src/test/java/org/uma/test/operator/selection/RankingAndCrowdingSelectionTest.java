//  RankingAndCrowdingSelectionTest.java
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

package org.uma.test.operator.selection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.operator.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.util.JMetalException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Antonio J. Nebro on 27/06/14.
 */

public class RankingAndCrowdingSelectionTest {
  static final int POPULATION_SIZE = 20 ;
  RankingAndCrowdingSelection selection ;
  SolutionSet solutionSet ;

  @Before
  public void startup() {
    selection = new RankingAndCrowdingSelection.Builder(POPULATION_SIZE)
            .build() ;

    solutionSet = new SolutionSet(POPULATION_SIZE) ;
    for (int i = 0 ; i < POPULATION_SIZE; i++) {
      solutionSet.add(new Solution()) ;
    }
  }

  @Test
  public void executeWithCorrectParametersTest()  {
    assertNotNull(selection.execute(solutionSet));
  }

  @Test
  public void executeWithDifferentParameterTest()  {
    selection = new RankingAndCrowdingSelection.Builder(20)
            .build() ;
    assertEquals(20, ((SolutionSet)selection.execute(solutionSet)).size());
  }

  @Test (expected = JMetalException.class)
  public void executeWithIncorrectParameterValueTest() {
    selection = new RankingAndCrowdingSelection.Builder(50)
            .build() ;

    selection.execute(solutionSet) ;
  }

  @Test (expected = JMetalException.class)
  public void wrongParameterClassTest() {
    selection.execute(new Integer(4)) ;
  }

  @Test (expected = JMetalException.class)
  public void nullParameterTest()  {
    selection.execute(null) ;
  }

  @Test
  public void rankingAndCrowdingWorksOkTest()  {
  //FIXME: to be implemented
  }

  @After
  public void tearDown() {
    selection = null ;
    solutionSet = null ;
  }
}
