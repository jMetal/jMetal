//  PolynomialMutationTest.java
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

package test.operators.mutation;

import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.problems.Kursawe;
import jmetal.util.JMException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Antonio J. Nebro on 21/04/14.
 */
public class PolynomialMutationTest {
  Operator mutation_ ;
  Problem problem_ ;
  HashMap parameters_ ;

  static final double DELTA = 0.0000000000001 ;

  @Before
  public void setUp()  {
    problem_ = new Kursawe("Real", 3) ;

    parameters_ = new HashMap() ;
    try {
      mutation_ = MutationFactory.getMutationOperator("PolynomialMutation", parameters_);
    } catch (JMException e) {
      e.printStackTrace();
    }
  }

  @After
  public void tearDown() throws Exception {
    mutation_ = null ;
    problem_ = null ;
  }

  @Test
  public void defaultParametersTest() {
    assertEquals("PolynomialMutationTest.testDefaultParameters",
            20.0, ((PolynomialMutation)mutation_).getDistributionIndex(), DELTA) ;
    assertNull("PolynomialMutationTest.testDefaultParameters",
            mutation_.getParameter("probability"));
  }

  @Test
  public void setMutationProbabilityTest() {
    parameters_.put("probability", 1.0/problem_.getNumberOfVariables()) ;

    try {
      mutation_ = MutationFactory.getMutationOperator("PolynomialMutation", parameters_);
    } catch (JMException e) {
      e.printStackTrace();
    }

    assertEquals("PolynomialMutationTest.setMutationProbabilityTest",
            1.0/problem_.getNumberOfVariables(),
            ((PolynomialMutation)mutation_).getMutationProbability(), DELTA) ;
  }

  @Test
  public void setMutationDistributionInex() {
    parameters_.put("distributionIndex", 5.0) ;

    try {
      mutation_ = MutationFactory.getMutationOperator("PolynomialMutation", parameters_);
    } catch (JMException e) {
      e.printStackTrace();
    }

    assertEquals("PolynomialMutationTest.setMutationDistributionInex",
            5.0,
            ((PolynomialMutation)mutation_).getDistributionIndex(), DELTA) ;
  }
}
