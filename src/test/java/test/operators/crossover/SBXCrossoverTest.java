//  SBXCrossoverTest.java
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

package test.operators.crossover;

import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.crossover.SBXCrossover;
import jmetal.problems.Kursawe;
import jmetal.util.JMException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 21/04/14.
 */
public class SBXCrossoverTest {
  Operator crossover_ ;
  Problem problem_ ;
  HashMap parameters_ ;

  static final double DELTA = 0.0000000000001 ;

  @Before
  public void setUp()  {
    problem_ = new Kursawe("Real", 3) ;

    parameters_ = new HashMap() ;
    try {
      crossover_ = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters_);
    } catch (JMException e) {
      e.printStackTrace();
    }
  }

  @After
  public void tearDown() throws Exception {
    crossover_ = null ;
    problem_ = null ;
  }

  @Test
  public void defaultParametersTest() {
    assertEquals("SBXCrossoverTest.testDefaultParameters",
            20.0, ((SBXCrossover)crossover_).getDistributionIndex(), DELTA) ;
    assertEquals("SBXCrossoverTest.testDefaultParameters",
            0.9, ((SBXCrossover)crossover_).getCrossoverProbability(), DELTA) ;
  }

  @Test
  public void setCrossoverProbabilityTest() {
    parameters_.put("probability", 1.0) ;

    try {
      crossover_ = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters_);
    } catch (JMException e) {
      e.printStackTrace();
    }

    assertEquals("SBXCrossoverTest.setCrossoverProbabilityTest",
            1.0,
            ((SBXCrossover)crossover_).getCrossoverProbability(), DELTA) ;
  }

  @Test
  public void setMutationDistributionIndex() {
    parameters_.put("distributionIndex", 5.0) ;

    try {
      crossover_ = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters_);
    } catch (JMException e) {
      e.printStackTrace();
    }

    assertEquals("SBXCrossoverTest.setCrossoverDistributionIndex",
            5.0,
            ((SBXCrossover)crossover_).getDistributionIndex(), DELTA) ;
  }
}
