//  cMOEAD_SettingsTest.java
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

package org.uma.test.experiment.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.settings.CMOEADSettings;
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Fonseca;
import org.uma.jmetal.util.JMetalException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 13/06/13
 * Time: 22:43
 * To change this template use File | Settings | File Templates.
 */
public class DMOEADSettingsTest {

  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("cMOEAD.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void testSettings() throws JMetalException {
    double epsilon = 0.000000000000001 ;
    Settings cMOEADSettings = new CMOEADSettings("Fonseca");
    Algorithm algorithm = cMOEADSettings.configure() ;
    Problem problem = new Fonseca("Real") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;
    String dataDirectory = (String) algorithm.getInputParameter("dataDirectory");

    String experimentDirectoryName = ClassLoader.getSystemResource(dataDirectory).getPath();

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    Assert.assertEquals("cMOEAD_SettingsTest", 300, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    Assert.assertEquals("cMOEAD_SettingsTest", 150000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());

    Assert.assertEquals("cMOEAD_SettingsTest", 0.9, ((Double)algorithm.getInputParameter("delta")).doubleValue(), epsilon) ;
    Assert.assertEquals("cMOEAD_SettingsTest", 20, ((Integer) algorithm.getInputParameter("T")).intValue());
    Assert.assertEquals("cMOEAD_SettingsTest", 2,   ((Integer)algorithm.getInputParameter("nr")).intValue());

    Assert.assertEquals("cMOEAD_SettingsTest", 1.0, CR, epsilon);
    Assert.assertEquals("cMOEAD_SettingsTest", 0.5, F, epsilon);
    Assert.assertEquals("cMOEAD_SettingsTest", 20.0, dim, epsilon);
    Assert.assertEquals("cMOEAD_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);

    assertNotNull("cMOEAD_SettingsTest", experimentDirectoryName);
  }

  @Test
  public void testSettings2() throws JMetalException {
    double epsilon = 0.000000000000001 ;
    Settings cMOEADSettings = new CMOEADSettings("Fonseca");
    Algorithm algorithm = cMOEADSettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;
    String dataDirectory = (String) algorithm.getInputParameter("dataDirectory");

    String experimentDirectoryName = ClassLoader.getSystemResource(dataDirectory).getPath();

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    Assert.assertEquals("cMOEAD_SettingsTest", 300, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    Assert.assertEquals("cMOEAD_SettingsTest", 150000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());

    Assert.assertEquals("cMOEAD_SettingsTest", 0.9, ((Double)algorithm.getInputParameter("delta")).doubleValue(), epsilon) ;
    Assert.assertEquals("cMOEAD_SettingsTest", 20, ((Integer) algorithm.getInputParameter("T")).intValue());
    Assert.assertEquals("cMOEAD_SettingsTest", 2,   ((Integer)algorithm.getInputParameter("nr")).intValue());

    Assert.assertEquals("cMOEAD_SettingsTest", 1.0, CR, epsilon);
    Assert.assertEquals("cMOEAD_SettingsTest", 0.5, F, epsilon);
    Assert.assertEquals("cMOEAD_SettingsTest", 20.0, dim, epsilon);
    Assert.assertEquals("cMOEAD_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);

    assertNotNull("cMOEAD_SettingsTest", experimentDirectoryName);
  }
}
