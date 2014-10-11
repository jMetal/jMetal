//  BinaryTournamentTest.java
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

package org.uma.test3.util.comparator;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Antonio J. Nebro on 15/06/14.
 */
public class BinaryTournamentSelectionTest {
  static final int POPULATION_SIZE = 20 ;

  private BinaryTournamentSelection selection ;
  private Problem problem ;
  private List<Solution<?>> population ;
/*
  @Before
  public void startup() throws ClassNotFoundException {
    problem = new Fonseca() ;
    selection = new BinaryTournamentSelection.Builder()
            .build() ;

    population = new ArrayList<>(POPULATION_SIZE) ;
    for (int i = 0 ; i < POPULATION_SIZE; i++) {
      population.add(problem.createSolution()) ;
    }
  }

  @Test
  public void executeWithCorrectParametersTest() {
    assertNotNull(selection.execute(population));
  }

  @Test
  public void executeWithCorrectPopulationSizeOneTest() {
    population = new ArrayList<>(1) ;
    Solution solution = problem.createSolution() ;
    population.add(solution) ;
    assertEquals(solution, selection.execute(population));
  }

  @Test (expected = JMetalException.class)
  public void executeWithPopulationSizeZeroTest() {
    population = new ArrayList<>(1) ;
    selection.execute(population) ;
  }

  @Test
  public void executeWithSolutionSetSizeTwoTest() throws ClassNotFoundException {
    population = new ArrayList<>(2) ;
    population.add(problem.createSolution()) ;
    population.add(problem.createSolution()) ;
    assertNotNull(selection.execute(population));
  }

  @Test (expected = JMetalException.class)
  public void nullParameterTest() {
    selection.execute(null) ;
  }

  @After
  public void tearDown() {
    selection = null ;
    population = null ;
    problem = null ;
  }
  */
}
