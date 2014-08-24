//  DifferentialEvolutionCrossoverTest.java
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

package org.uma.test.operator.crossover;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.util.JMetalException;

import static org.junit.Assert.*;

/**
 * Created by Antonio J. Nebro on 21/04/14.
 */
public class DifferentialEvolutionCrossoverTest {
  DifferentialEvolutionCrossover crossover;
  Problem problem;

  static final double DELTA = 0.0000000000001 ;

  @Before
  public void setUp() throws JMetalException {
    problem = new Kursawe("Real", 3) ;
    crossover = new DifferentialEvolutionCrossover.Builder().build() ;
  }

  @After
  public void tearDown() throws Exception {
    crossover = null ;
    problem = null ;
  }

  @Test
  public void defaultParametersTest() {
    assertEquals(0.5, crossover.getCr(), DELTA) ;
    assertEquals(0.5, crossover.getF(), DELTA) ;
    assertEquals(0.5, crossover.getK(), DELTA) ;
    assertEquals("rand/1/bin", crossover.getVariant());
  }


  @Test
  public void setCrTest() {
    crossover = new DifferentialEvolutionCrossover.Builder()
            .setCr(0.1)
            .build() ;

    assertEquals(0.1, crossover.getCr(), DELTA) ;
  }

  @Test (expected = JMetalException.class)
  public void setInvalidCrTest() {
    crossover = new DifferentialEvolutionCrossover.Builder().setCr(2.0).build() ;
  }

  @Test
  public void setFTest() {
    crossover = new DifferentialEvolutionCrossover.Builder()
            .setF(0.75)
            .build() ;

    assertEquals(0.75, crossover.getF(), DELTA) ;
  }

  @Test (expected = JMetalException.class)
  public void setInvalidFTest() {
    crossover = new DifferentialEvolutionCrossover.Builder()
            .setF(-15)
            .build() ;
  }

  @Test
  public void setKTest() {
    crossover = new DifferentialEvolutionCrossover.Builder()
            .setK(0.63)
            .build() ;

    assertEquals(0.63, crossover.getK(), DELTA) ;
  }

  @Test (expected = JMetalException.class)
  public void setInvalidKTest() {
    crossover = new DifferentialEvolutionCrossover.Builder()
            .setK(-32)
            .build() ;
  }

  @Test
  public void setVariantTest() {
    crossover = new DifferentialEvolutionCrossover.Builder()
            .setVariant("current-to-rand/1/bin")
            .build() ;

    assertTrue("current-to-rand/1/bin".equals(crossover.getVariant())) ;
  }

  @Test (expected = JMetalException.class)
  public void setWrongVariantTest() throws ClassNotFoundException {
    crossover = new DifferentialEvolutionCrossover.Builder()
            .setVariant("current-to-rand")
            .build() ;
  }

  @Test
  public void operatorExecutionTest() throws ClassNotFoundException {
    crossover = new DifferentialEvolutionCrossover.Builder()
            .setCr(0.5)
            .setF(1.0)
            .build() ;

    Solution current = new Solution(problem) ;
    Solution[] parents = {new Solution(problem), new Solution(problem), new Solution(problem)} ;

    Object result = crossover.execute(new Object[]{current, parents}) ;
    assertNotNull(result);
  }
}
