//  MOEAD_DRA_SettingsTest.java
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
//  along with this program.  If not, see <http://www.gnu.org/licenses/>

package org.uma.test.experiments.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.experiments.Settings;
import org.uma.jmetal.experiments.settings.MOEAD_DRA_Settings;
import org.uma.jmetal.operators.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operators.mutation.PolynomialMutation;
import org.uma.jmetal.problems.Fonseca;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 21/06/13
 * Time: 07:48
 * To change this template use File | Settings | File Templates.
 */
public class MOEAD_DRA_SettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("MOEAD_DRA.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings MOEAD_DRASettings = new MOEAD_DRA_Settings("Fonseca");
    Algorithm algorithm = MOEAD_DRASettings.configure() ;
    Problem problem = new Fonseca("Real") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;
    String dataDirectory = (String) algorithm.getInputParameter("dataDirectory");

    String experimentDirectoryName = this.getClass().getClassLoader().getResource(dataDirectory).getPath();

    int populationSize =  ((Integer)algorithm.getInputParameter("populationSize")).intValue() ;


    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    Assert.assertEquals("MOEAD_DRASettings", 600, populationSize);
    Assert.assertEquals("MOEAD_DRASettings", 300000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    Assert.assertEquals("MOEAD_DRASettings", 300, ((Integer)algorithm.getInputParameter("finalSize")).intValue());

    Assert.assertEquals("MOEAD_DRASettings", 0.9, ((Double)algorithm.getInputParameter("delta")).doubleValue(), epsilon) ;
    Assert.assertEquals("MOEAD_DRASettings", 60, ((Integer) algorithm.getInputParameter("T")).intValue());
    Assert.assertEquals("MOEAD_DRASettings", 6,   ((Integer)algorithm.getInputParameter("nr")).intValue());

    Assert.assertEquals("MOEAD_DRASettings", 1.0, CR, epsilon);
    Assert.assertEquals("MOEAD_DRASettings", 0.5, F, epsilon);
    Assert.assertEquals("MOEAD_DRASettings", 20.0, dim, epsilon);
    Assert.assertEquals("MOEAD_DRASettings", 1.0/problem.getNumberOfVariables(), pm, epsilon);

    assertNotNull("MOEAD_DRASettings", experimentDirectoryName);
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings MOEAD_DRASettings = new MOEAD_DRA_Settings("Fonseca");
    Algorithm algorithm = MOEAD_DRASettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;
    String dataDirectory = (String) algorithm.getInputParameter("dataDirectory");

    String experimentDirectoryName = this.getClass().getClassLoader().getResource(dataDirectory).getPath();

    int populationSize =  ((Integer)algorithm.getInputParameter("populationSize")).intValue() ;

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    Assert.assertEquals("MOEAD_DRASettings", 600, populationSize);
    Assert.assertEquals("MOEAD_DRASettings", 300000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    Assert.assertEquals("MOEAD_DRASettings", 300, ((Integer)algorithm.getInputParameter("finalSize")).intValue());

    Assert.assertEquals("MOEAD_DRASettings", 0.9, ((Double)algorithm.getInputParameter("delta")).doubleValue(), epsilon) ;
    Assert.assertEquals("MOEAD_DRASettings", 60, ((Integer) algorithm.getInputParameter("T")).intValue());
    Assert.assertEquals("MOEAD_DRASettings", 6,   ((Integer)algorithm.getInputParameter("nr")).intValue());

    Assert.assertEquals("MOEAD_DRASettings", 1.0, CR, epsilon);
    Assert.assertEquals("MOEAD_DRASettings", 0.5, F, epsilon);
    Assert.assertEquals("MOEAD_DRASettings", 20.0, dim, epsilon);
    Assert.assertEquals("MOEAD_DRASettings", 1.0/problem.getNumberOfVariables(), pm, epsilon);

    assertNotNull("MOEAD_DRASettings", experimentDirectoryName);
  }
}
