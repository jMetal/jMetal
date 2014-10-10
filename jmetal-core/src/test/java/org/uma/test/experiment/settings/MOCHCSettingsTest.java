//  MOCHCSettingsTest.java
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.settings.MOCHCSettings;
import org.uma.jmetal.metaheuristic.multiobjective.mochc.MOCHC;
import org.uma.jmetal.operator.crossover.HUXCrossover;
import org.uma.jmetal.operator.mutation.BitFlipMutation;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT5;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 20/06/13
 * Time: 22:21
 * To change this template use File | Settings | File Templates.
 */
public class MOCHCSettingsTest {
  Properties configuration;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("MOCHC.conf").getPath()));
    configuration.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings mochcSettings = new MOCHCSettings("ZDT5");
    MOCHC algorithm = (MOCHC)mochcSettings.configure() ;
    Problem problem = new ZDT5("Binary") ;

    HUXCrossover crossover =  (HUXCrossover)algorithm.getCrossover();
    double pc = crossover.getCrossoverProbability() ;

    BitFlipMutation mutation = (BitFlipMutation)algorithm.getCataclysmicMutation() ;

    Assert.assertEquals(100, algorithm.getPopulationSize());
    Assert.assertEquals(25000, algorithm.getMaxEvaluation());
    Assert.assertEquals(0.25, algorithm.getInitialConvergenceCount(),  epsilon);
    Assert.assertEquals(3, algorithm.getConvergenceValue(), epsilon) ;
    Assert.assertEquals(0.05, algorithm.getPreservedPopulation(), epsilon);

    Assert.assertEquals(1.0, pc, epsilon);
    Assert.assertTrue(problem.getName().equals("ZDT5"));

    Assert.assertEquals(0.35, mutation.getMutationProbability(), epsilon);
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings mochcSettings = new MOCHCSettings("ZDT5");
    MOCHC algorithm = (MOCHC)mochcSettings.configure(configuration) ;
    Problem problem = new ZDT5("Binary") ;

    HUXCrossover crossover =  (HUXCrossover)algorithm.getCrossover();
    double pc = crossover.getCrossoverProbability() ;

    BitFlipMutation mutation = (BitFlipMutation)algorithm.getCataclysmicMutation() ;

    Assert.assertEquals(100, algorithm.getPopulationSize());
    Assert.assertEquals(25000, algorithm.getMaxEvaluation());
    Assert.assertEquals(0.25, algorithm.getInitialConvergenceCount(),  epsilon);
    Assert.assertEquals(3, algorithm.getConvergenceValue(), epsilon) ;
    Assert.assertEquals(0.05, algorithm.getPreservedPopulation(), epsilon);

    Assert.assertEquals(1.0, pc, epsilon);
    Assert.assertTrue(problem.getName().equals("ZDT5"));

    Assert.assertEquals(0.35, mutation.getMutationProbability(), epsilon);
  }
}
