//  PAESSettingsTest.java
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

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.settings.PAESSettings;
import org.uma.jmetal.metaheuristic.multiobjective.paes.PAES;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.Fonseca;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 27/06/13
 * Time: 07:36
 * To change this template use File | Settings | File Templates.
 */
public class PAESSettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("PAES.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings paesSettings = new PAESSettings("Fonseca");
    PAES algorithm = (PAES)paesSettings.configure() ;
    Problem problem = new Fonseca("Real") ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutationOperator() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;

    assertEquals("PAES_SettingsTest", 100, algorithm.getArchiveSize());
    assertEquals("PAES_SettingsTest", 25000, algorithm.getMaxEvaluations());
    assertEquals("PAES_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    assertEquals("PAES_SettingsTest", 20.0, dim, epsilon);
    assertEquals("PAES_SettingsTest", 5, algorithm.getBiSections());
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings paesSettings = new PAESSettings("Fonseca");
    PAES algorithm = (PAES)paesSettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real") ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutationOperator() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;

    assertEquals("PAES_SettingsTest", 100, algorithm.getArchiveSize());
    assertEquals("PAES_SettingsTest", 25000, algorithm.getMaxEvaluations());
    assertEquals("PAES_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    assertEquals("PAES_SettingsTest", 20.0, dim, epsilon);
    assertEquals("PAES_SettingsTest", 5, algorithm.getBiSections());
  }
}
