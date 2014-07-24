//  GDE3_SettingsTest.java
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

import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.settings.GDE3Settings;
import org.uma.jmetal.metaheuristic.multiobjective.gde3.GDE3;
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.util.JMetalException;
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
 * User: Antonio J. Nebro
 * Date: 16/06/13
 * Time: 20:59
 * To change this template use File | Settings | File Templates.
 */
public class GDE3SettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("GDE3.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void test() throws JMetalException {
    double epsilon = 0.000000000000001;
    GDE3Settings GDE3Settings = new org.uma.jmetal.experiment.settings.GDE3Settings("Fonseca");
    GDE3 algorithm = (GDE3) GDE3Settings.configure();

    DifferentialEvolutionCrossover crossover =
      (DifferentialEvolutionCrossover) algorithm.getCrossoverOperator();

    Assert.assertEquals("GDE3_SettingsTest", 100, algorithm.getPopulationSize());
    Assert.assertEquals("GDE3_SettingsTest", 250, algorithm.getMaxIterations());

    Assert.assertEquals("GDE3_SettingsTest", 0.5, crossover.getCr(), epsilon);
    Assert.assertEquals("GDE3_SettingsTest", 0.5, crossover.getF(), epsilon);
  }

  @Test
  public void test2() throws JMetalException {
    double epsilon = 0.000000000000001;
    Settings GDE3Settings = new org.uma.jmetal.experiment.settings.GDE3Settings("Fonseca");
    GDE3 algorithm = (GDE3) GDE3Settings.configure(configuration_);

    DifferentialEvolutionCrossover crossover =
      (DifferentialEvolutionCrossover) algorithm.getCrossoverOperator();

    Assert.assertEquals("GDE3_SettingsTest", 100, algorithm.getPopulationSize());
    Assert.assertEquals("GDE3_SettingsTest", 250, algorithm.getMaxIterations());

    Assert.assertEquals("GDE3_SettingsTest", 0.5, crossover.getCr(), epsilon);
    Assert.assertEquals("GDE3_SettingsTest", 0.5, crossover.getF(), epsilon);
  }
}
