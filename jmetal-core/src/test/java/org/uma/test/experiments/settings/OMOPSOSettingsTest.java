//  OMOPSO_SettingsTest.java
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
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.settings.OMOPSOSettings;
import org.uma.jmetal.operator.mutation.NonUniformMutation;
import org.uma.jmetal.operator.mutation.UniformMutation;
import org.uma.jmetal.problem.Fonseca;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 26/06/13
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */
public class OMOPSOSettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("OMOPSO.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings omopsoSettings = new OMOPSOSettings("Fonseca");
    Algorithm algorithm = omopsoSettings.configure() ;
    Problem problem = new Fonseca("Real");

    UniformMutation uniformMutation = (UniformMutation)algorithm.getOperator("uniformMutation")  ;
    NonUniformMutation nonUniformMutation = (NonUniformMutation)algorithm.getOperator("nonUniformMutation")  ;

    Assert.assertEquals("OMOPSO_SettingsTest", 100, ((Integer)algorithm.getInputParameter("swarmSize")).intValue());
    Assert.assertEquals("OMOPSO_SettingsTest", 250, ((Integer)algorithm.getInputParameter("maxIterations")).intValue());
    Assert.assertEquals("OMOPSO_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());

    Assert.assertEquals("OMOPSO_SettingsTest", 1.0/problem.getNumberOfVariables(), (Double)uniformMutation.getParameter("probability") , epsilon);
    Assert.assertEquals("OMOPSO_SettingsTest", 0.5, (Double)uniformMutation.getParameter("perturbation") , epsilon);
    Assert.assertEquals("OMOPSO_SettingsTest", 250, ((Integer)nonUniformMutation.getParameter("maxIterations")).intValue());
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings omopsoSettings = new OMOPSOSettings("Fonseca");
    Algorithm algorithm = omopsoSettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real");

    UniformMutation uniformMutation = (UniformMutation)algorithm.getOperator("uniformMutation")  ;
    NonUniformMutation nonUniformMutation = (NonUniformMutation)algorithm.getOperator("nonUniformMutation")  ;

    Assert.assertEquals("OMOPSO_SettingsTest", 100, ((Integer)algorithm.getInputParameter("swarmSize")).intValue());
    Assert.assertEquals("OMOPSO_SettingsTest", 250, ((Integer)algorithm.getInputParameter("maxIterations")).intValue());
    Assert.assertEquals("OMOPSO_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());

    Assert.assertEquals("OMOPSO_SettingsTest", 1.0/problem.getNumberOfVariables(), (Double)uniformMutation.getParameter("probability") , epsilon);
    Assert.assertEquals("OMOPSO_SettingsTest", 0.5, (Double)uniformMutation.getParameter("perturbation") , epsilon);
    Assert.assertEquals("OMOPSO_SettingsTest", 250, ((Integer)nonUniformMutation.getParameter("maxIterations")).intValue());
  }
}
