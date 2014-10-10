//  NSGAIIPermutation_SettingsTest.java
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J.Nebro
 * Date: 29/06/13
 * Time: 16:58
 */
public class NSGAIIPermutationSettingsTest {
  Properties configuration;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("NSGAIIPermutation.conf").getPath()));
    configuration.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    /*double epsilon = 0.000000000000001 ;
    
    Settings nsgaIISettings = new NSGAIIPermutation_Settings("mQAP");
    Algorithm algorithm = nsgaIISettings.configure() ;
    Problem problem = new mQAP("Permutation") ;
    TwoPointsCrossover crossover = (TwoPointsCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("setProbability") ;

    SwapMutation mutation = (SwapMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("setProbability") ;

    assertEquals("NSGAIIPermutation_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("NSGAIIPermutation_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());

    assertEquals("NSGAIIPermutation_SettingsTest", 0.9, pc, epsilon);

    assertEquals("NSGAIIPermutation_SettingsTest", 1.0/problem.getNumberOfRealVariables(), pm, epsilon);
    */
  }
}
