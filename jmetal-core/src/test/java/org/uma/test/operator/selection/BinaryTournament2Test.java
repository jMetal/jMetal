//  BinaryTournament2Test.java
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
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.core.SolutionSet;
import org.uma.jmetal45.operator.selection.BinaryTournament2;
import org.uma.jmetal45.util.JMetalException;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Antonio J. Nebro on 15/06/14.
 */
public class BinaryTournament2Test {
	static final int POPULATION_SIZE = 20 ;
	BinaryTournament2 selection ;
	SolutionSet solutionSet ;

	@Before
	public void startup() {
		selection = new BinaryTournament2.Builder()
		.build() ;

		solutionSet = new SolutionSet(POPULATION_SIZE) ;
		for (int i = 0 ; i < POPULATION_SIZE; i++) {
			solutionSet.add(new Solution()) ;
		}
	}

	@Test
	public void executeWithCorrectParametersTest() {
		assertNotNull((Solution)selection.execute(solutionSet));
	}

	@Test (expected = JMetalException.class)
	public void executeWithPopulationSizeOneTest() {
		solutionSet = new SolutionSet(1) ;
		solutionSet.add(new Solution()) ;
		selection.execute(solutionSet) ;
	}

	@Test (expected = JMetalException.class)
	public void executeWithPopulationSizeZeroTest() {
		solutionSet = new SolutionSet(0) ;
		selection.execute(solutionSet) ;
	}

	@Test
	public void executeWithSolutionSetSizeTwoTest() {
		solutionSet = new SolutionSet(2) ;
		solutionSet.add(new Solution()) ;
		solutionSet.add(new Solution()) ;
		assertNotNull((Solution)selection.execute(solutionSet));  
	}
	
  @Test (expected = JMetalException.class)
  public void wrongParameterClassTest() {
    selection.execute(new Integer(4)) ;
  }

  @Test (expected = JMetalException.class)
  public void nullParameterTest() {
    selection.execute(null) ;
  }
  
  /* FIXME: pending to test
  @Test 
  public void theResultIsAPermutationTest() {
  	SolutionSet selectedSolutions = new SolutionSet(POPULATION_SIZE) ;
  	for (int i = 0; i < POPULATION_SIZE; i++) {
  		selectedSolutions.add((Solution)selection.execute(solutionSet)) ;
  	}

  	boolean found = true ;
  	for (int i = 0 ; i < POPULATION_SIZE; i++) {
  		
  	}
  	
  	Arrays.sort(selectedSolutions) ;
    assertArrayEquals(permutation, selectedSolutions) ;
  }
  */
  
	@After
	public void tearDown() {
		selection = null ;
		solutionSet = null ;
	}
}
