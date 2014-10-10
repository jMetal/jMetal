//  PESA2SelectionTest.java
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
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operator.selection.PESA2Selection;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.AdaptiveGridArchive;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Antonio J. Nebro on 15/06/14.
 */
public class PESA2SelectionTest {
	static final int POPULATION_SIZE = 20 ;
	PESA2Selection selection ;
  AdaptiveGridArchive archive ;
  Problem problem ;

	@Before
	public void startup() throws ClassNotFoundException {
		selection = new PESA2Selection.Builder()
		.build() ;

    problem = new Kursawe("Real") ;

		archive = new AdaptiveGridArchive(100, 5, problem.getNumberOfObjectives()) ;
		for (int i = 0 ; i < POPULATION_SIZE; i++) {
      Solution solution = new Solution(problem) ;
      problem.evaluate(solution);
			archive.add(solution) ;
		}
	}

	@Test
	public void executeWithCorrectParametersTest()  {
		assertNotNull(selection.execute(archive));
	}

  @Test (expected = JMetalException.class)
  public void wrongParameterClassTest()  {
    selection.execute(new Integer(4)) ;
  }

  @Test (expected = JMetalException.class)
  public void nullParameterTest() {
    selection.execute(null) ;
  }

	@After
	public void tearDown() {
		selection = null ;
		archive = null ;
    problem = null ;
	}
}
