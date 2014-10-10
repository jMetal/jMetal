//  PESA2SettingsTest.java
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
import org.uma.jmetal.experiment.settings.PESA2Settings;
import org.uma.jmetal.metaheuristic.multiobjective.pesa2.PESA2;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.Fonseca;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 19/08/13
 */
public class PESA2SettingsTest {
  Properties configuration;

  @Before
  public void init() throws IOException {
    configuration = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("PESA2.conf").getPath()));
    configuration.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings PESA2Settings = new PESA2Settings("Fonseca");
    PESA2 algorithm = (PESA2) PESA2Settings.configure() ;
    Problem problem = new Fonseca("Real") ;

    SBXCrossover crossover = (SBXCrossover) algorithm.getCrossover();
    PolynomialMutation mutation = (PolynomialMutation) algorithm.getMutation();

    assertEquals(10, algorithm.getPopulationSize());
    assertEquals(25000, algorithm.getMaxEvaluations());
    assertEquals(100, algorithm.getArchiveSize());
    assertEquals(5, algorithm.getBiSections());

    assertEquals(0.9, crossover.getCrossoverProbability(), epsilon);
    assertEquals(20.0, crossover.getDistributionIndex(), epsilon);

    assertEquals(1.0/problem.getNumberOfVariables(), mutation.getMutationProbability(), epsilon);
    assertEquals(20.0, mutation.getDistributionIndex(), epsilon);
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;

    Settings PESA2Settings = new PESA2Settings("Fonseca");
    PESA2 algorithm = (PESA2) PESA2Settings.configure(configuration) ;
    Problem problem = new Fonseca("Real") ;

    SBXCrossover crossover = (SBXCrossover) algorithm.getCrossover();
    PolynomialMutation mutation = (PolynomialMutation) algorithm.getMutation();

    assertEquals(10, algorithm.getPopulationSize());
    assertEquals(25000, algorithm.getMaxEvaluations());
    assertEquals(100, algorithm.getArchiveSize());
    assertEquals(5, algorithm.getBiSections());

    assertEquals(0.9, crossover.getCrossoverProbability(), epsilon);
    assertEquals(20.0, crossover.getDistributionIndex(), epsilon);

    assertEquals(1.0/problem.getNumberOfVariables(), mutation.getMutationProbability(), epsilon);
    assertEquals(20.0, mutation.getDistributionIndex(), epsilon);
  }
}
