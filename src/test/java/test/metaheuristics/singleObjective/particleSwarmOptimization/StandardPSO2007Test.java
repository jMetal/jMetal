//  StandardPSO2007Test.java
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

package test.metaheuristics.singleObjective.particleSwarmOptimization;

import jmetal.core.Problem;
import jmetal.metaheuristics.singleObjective.particleSwarmOptimization.StandardPSO2007;
import jmetal.problems.singleObjective.Sphere;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 26/03/14.
 */
public class StandardPSO2007Test {

  StandardPSO2007 standardPSO_ ;
  Problem problem_ ;

  @Before
  public void setUp() throws Exception {
    problem_ = new Sphere("Real", 30) ;
    standardPSO_ = new StandardPSO2007() ;
    standardPSO_.setProblem(problem_);
    standardPSO_.setInputParameter("swarmSize",40);
    standardPSO_.setInputParameter("maxIterations",5000);
    standardPSO_.setInputParameter("numberOfParticlesToInform",3);
    standardPSO_.initParams();
  }

  @Test
  public void initParamsTest() {
    assertEquals("StandardPSO2007Test.initParamsTest", 40, ((Integer) standardPSO_.getInputParameter("swarmSize")).intValue());
    assertEquals("StandardPSO2007Test.initParamsTest", 5000, ((Integer) standardPSO_.getInputParameter("maxIterations")).intValue());
    assertEquals("StandardPSO2007Test.initParamsTest", 3, ((Integer) standardPSO_.getInputParameter("numberOfParticlesToInform")).intValue());
  }

  @Test
  public void valuesOfWAndCTest() {
    double offset = 0.001 ;
    assertEquals("StandardPSO2007Test.valuesOfWAndCTest", 1.193, standardPSO_.getC(), offset);
    assertEquals("StandardPSO2007Test.valuesOfWAndCTest", 0.721, standardPSO_.getW(), offset);
  }

  @Test
  public void defaultSwarmSizeTest() {
    assertEquals("StandardPSO2007Test.defaultSwarmSizeTest", 20, 2 * (int)(2*Math.sqrt(problem_.getNumberOfVariables())));
  }


  @Test
  public void neighborTest() {
    StandardPSO2007 newPSO = new StandardPSO2007() ;
    standardPSO_.setProblem(problem_);
    standardPSO_.setInputParameter("swarmSize",10);
    standardPSO_.setInputParameter("numberOfParticlesToInform",3);
  }

  @After
  public void tearDown() throws Exception {
    problem_ = null;
    standardPSO_ = null ;
  }
}
