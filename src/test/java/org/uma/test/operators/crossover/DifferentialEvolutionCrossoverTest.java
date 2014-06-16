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

package org.uma.test.operators.crossover;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operators.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problems.Kursawe;
import org.uma.jmetal.util.JMetalException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Antonio J. Nebro on 21/04/14.
 */
public class DifferentialEvolutionCrossoverTest {
  DifferentialEvolutionCrossover crossover_ ;
  Problem problem_ ;

  static final double DELTA = 0.0000000000001 ;

  @Before
  public void setUp() throws JMetalException {
    problem_ = new Kursawe("Real", 3) ;
    crossover_ = new DifferentialEvolutionCrossover.Builder().build() ;
  }

  @After
  public void tearDown() throws Exception {
    crossover_ = null ;
    problem_ = null ;
  }

  @Test
  public void defaultParametersTest() {
    assertEquals(0.5, crossover_.getCr(), DELTA) ;
    assertEquals(0.5, crossover_.getF(), DELTA) ;
    assertEquals(0.5, crossover_.getK(), DELTA) ;
    assertEquals("rand/1/bin", crossover_.getVariant());
  }


  @Test
  public void setCrTest() {
    crossover_ = new DifferentialEvolutionCrossover.Builder()
      .cr(0.1)
      .build() ;

    assertEquals(0.1, crossover_.getCr(), DELTA) ;
  }

  @Test (expected = JMetalException.class)
  public void setInvalidCrTest() {
    crossover_ = new DifferentialEvolutionCrossover.Builder().cr(2.0).build() ;
  }

  @Test
  public void setFTest() {
    crossover_ = new DifferentialEvolutionCrossover.Builder()
      .f(0.75)
      .build() ;

    assertEquals(0.75, crossover_.getF(), DELTA) ;
  }

  @Test (expected = JMetalException.class)
  public void setInvalidFTest() {
    crossover_ = new DifferentialEvolutionCrossover.Builder()
      .f(-15)
      .build() ;
  }

  @Test
  public void setKTest() {
    crossover_ = new DifferentialEvolutionCrossover.Builder()
      .k(0.63)
      .build() ;

    assertEquals(0.63, crossover_.getK(), DELTA) ;
  }

  @Test (expected = JMetalException.class)
  public void setInvalidKTest() {
    crossover_ = new DifferentialEvolutionCrossover.Builder()
      .k(-32)
      .build() ;
  }

  @Test
  public void setVariantTest() {
    crossover_ = new DifferentialEvolutionCrossover.Builder()
      .variant("current-to-rand/1/bin")
      .build() ;

    assertTrue("current-to-rand/1/bin".equals(crossover_.getVariant())) ;
  }

  @Test (expected = JMetalException.class)
  public void setWrongVariantTest() throws ClassNotFoundException {
    crossover_ = new DifferentialEvolutionCrossover.Builder().variant("current-to-rand").build() ;
  }

  @Test
  public void operatorExecutionTest() throws ClassNotFoundException {
    crossover_ = new DifferentialEvolutionCrossover.Builder()
      .cr(0.5)
      .f(1.0)
      .build() ;

    Solution current = new Solution(problem_) ;
    Solution[] parents = {new Solution(problem_), new Solution(problem_), new Solution(problem_)} ;

    Object result = crossover_.execute(new Object[]{current, parents}) ;
    assertNotNull(result);
  }
}
