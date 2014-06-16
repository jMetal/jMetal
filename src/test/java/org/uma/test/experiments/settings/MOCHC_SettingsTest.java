//  MOCHC_SettingsTest.java
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
import org.uma.jmetal.experiments.settings.MOCHC_Settings;
import org.uma.jmetal.operators.crossover.HUXCrossover;
import org.uma.jmetal.operators.mutation.BitFlipMutation;
import org.uma.jmetal.problems.ZDT.ZDT5;

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
 * Date: 20/06/13
 * Time: 22:21
 * To change this template use File | Settings | File Templates.
 */
public class MOCHC_SettingsTest {

  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("MOCHC.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings mochcSettings = new MOCHC_Settings("ZDT5");
    Algorithm algorithm = mochcSettings.configure() ;
    Problem problem = new ZDT5("Binary") ;

    HUXCrossover crossover = (HUXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    //RankingAndCrowdingSelection rankingAndCrowdingSelection =  (RankingAndCrowdingSelection)algorithm.getOperator("newGenerationSelection") ;
    //Problem problem2 = (Problem) rankingAndCrowdingSelection.getParameter("problem");
    BitFlipMutation mutation = (BitFlipMutation)algorithm.getOperator("cataclysmicMutation") ;
    double pm = (Double)mutation.getParameter("probability") ;

    Assert.assertEquals("MOCHC_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    Assert.assertEquals("MOCHC_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    Assert.assertEquals("MOCHC_SettingsTest", 0.25, ((Double)algorithm.getInputParameter("initialConvergenceCount")).doubleValue(), epsilon);
    Assert.assertEquals("MOCHC_SettingsTest", 3, ((Integer)algorithm.getInputParameter("convergenceValue")).intValue());
    Assert.assertEquals("MOCHC_SettingsTest", 0.05, ((Double)algorithm.getInputParameter("preservedPopulation")).doubleValue(), epsilon);

    Assert.assertEquals("MOCHC_SettingsTest", 1.0, pc, epsilon);
    Assert.assertTrue("MOCHC_SettingsTest", problem.getName().equals("ZDT5"));

    Assert.assertEquals("MOCHC_SettingsTest", 0.35, pm, epsilon);
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings mochcSettings = new MOCHC_Settings("ZDT5");
    Algorithm algorithm = mochcSettings.configure(configuration_) ;
    Problem problem = new ZDT5("Binary") ;

    HUXCrossover crossover = (HUXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    //RankingAndCrowdingSelection rankingAndCrowdingSelection =  (RankingAndCrowdingSelection)algorithm.getOperator("newGenerationSelection") ;
    //Problem problem2 = (Problem) rankingAndCrowdingSelection.getParameter("problem");
    BitFlipMutation mutation = (BitFlipMutation)algorithm.getOperator("cataclysmicMutation") ;
    double pm = (Double)mutation.getParameter("probability") ;

    Assert.assertEquals("MOCHC_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    Assert.assertEquals("MOCHC_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    Assert.assertEquals("MOCHC_SettingsTest", 0.25, ((Double)algorithm.getInputParameter("initialConvergenceCount")).doubleValue(), epsilon);
    Assert.assertEquals("MOCHC_SettingsTest", 3, ((Integer)algorithm.getInputParameter("convergenceValue")).intValue());
    Assert.assertEquals("MOCHC_SettingsTest", 0.05, ((Double)algorithm.getInputParameter("preservedPopulation")).doubleValue(), epsilon);

    Assert.assertEquals("MOCHC_SettingsTest", 1.0, pc, epsilon);
    Assert.assertTrue("MOCHC_SettingsTest", problem.getName().equals("ZDT5"));

    Assert.assertEquals("MOCHC_SettingsTest", 0.35, pm, epsilon);
  }
}
