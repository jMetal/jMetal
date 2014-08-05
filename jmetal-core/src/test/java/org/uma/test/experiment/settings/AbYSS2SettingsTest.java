//  AbYSS2SettingsTest.java
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
import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.settings.AbYSS2Settings;
import org.uma.jmetal.experiment.settings.AbYSSSettings;
import org.uma.jmetal.metaheuristic.multiobjective.abyss.AbYSS2;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.localSearch.MutationLocalSearch;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Fonseca;
import org.uma.jmetal.util.JMetalException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 12/06/13
 * Time: 07:48
 */
public class AbYSS2SettingsTest {
  Properties configuration;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("AbYSS.conf").getPath()));
    configuration.load(isr);
  }

  @Test
  public void testSettings() throws JMetalException {
    double epsilon = 0.000000000000001 ;
    Settings abyssSettings = new AbYSS2Settings("Fonseca");
    AbYSS2 algorithm = (AbYSS2)abyssSettings.configure() ;
    Problem problem = new Fonseca("Real") ;

    assertEquals(20, algorithm.getPopulationSize());
    assertEquals(25000, algorithm.getMaxEvaluations());
    assertEquals(10, algorithm.getRefSet1Size());
    assertEquals(10, algorithm.getRefSet2Size());
    assertEquals(4, algorithm.getNumberOfSubranges());
    assertEquals(100, algorithm.getArchive().getMaximumSize());
    assertEquals(1, ((MutationLocalSearch)algorithm.getImprovementOperator()).getImprovementRounds());
  }
}
