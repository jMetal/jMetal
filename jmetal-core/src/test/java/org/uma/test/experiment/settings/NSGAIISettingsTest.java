//  NSGAIIBinary_SettingsTest.java
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

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Fonseca;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 26/06/13
 * Time: 07:56
 * To change this template use File | Settings | File Templates.
 */
public class NSGAIISettingsTest {
  Properties configuration;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("NSGAII.conf").getPath()));
    configuration.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings NSGAIISettings = new org.uma.jmetal.experiment.settings.NSGAIISettings("Fonseca");
    NSGAII algorithm = (NSGAII) NSGAIISettings.configure() ;
    Problem problem = new Fonseca("Real") ;

    SBXCrossover crossover = (SBXCrossover) algorithm.getCrossoverOperator() ;
    double pc = crossover.getCrossoverProbability() ;
    double dic = crossover.getDistributionIndex() ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutationOperator() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;

    assertEquals(100, algorithm.getPopulationSize());
    assertEquals(25000, algorithm.getMaxEvaluations());

    assertEquals(0.9, pc, epsilon);
    assertEquals(20.0, dic, epsilon);

    assertEquals(1.0/problem.getNumberOfVariables(), pm, epsilon);
    assertEquals(20.0, dim, epsilon);
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings NSGAIISettings = new org.uma.jmetal.experiment.settings.NSGAIISettings("Fonseca");
    NSGAII algorithm = (NSGAII)NSGAIISettings.configure(configuration) ;
    Problem problem = new Fonseca("Real") ;

    SBXCrossover crossover = (SBXCrossover) algorithm.getCrossoverOperator() ;
    double pc = crossover.getCrossoverProbability() ;
    double dic = crossover.getDistributionIndex() ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutationOperator() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;

    assertEquals(100, algorithm.getPopulationSize());
    assertEquals(25000, algorithm.getMaxEvaluations());

    assertEquals(0.9, pc, epsilon);
    assertEquals(20.0, dic, epsilon);

    assertEquals(1.0/problem.getNumberOfVariables(), pm, epsilon);
    assertEquals(20.0, dim, epsilon);
  }
}
