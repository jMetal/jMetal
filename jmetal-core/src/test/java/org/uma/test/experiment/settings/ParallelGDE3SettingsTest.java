//  ParallelGDE3SettingsTest.java
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
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.settings.GDE3Settings;
import org.uma.jmetal.experiment.settings.ParallelGDE3Settings;
import org.uma.jmetal.metaheuristic.multiobjective.gde3.GDE3;
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.MultithreadedSolutionSetEvaluator;

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
 */
public class ParallelGDE3SettingsTest {
  Properties configuration ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("parallelGDE3.conf").getPath()));
    configuration.load(isr);
  }

  @Test
  public void test() throws JMetalException {
    double epsilon = 0.000000000000001;
    ParallelGDE3Settings GDE3Settings = new ParallelGDE3Settings("Fonseca");
    GDE3 algorithm = (GDE3) GDE3Settings.configure();

    DifferentialEvolutionCrossover crossover =
      (DifferentialEvolutionCrossover) algorithm.getCrossoverOperator();

    Assert.assertEquals(100, algorithm.getPopulationSize());
    Assert.assertEquals(250, algorithm.getMaxIterations());

    Assert.assertEquals(0.5, crossover.getCr(), epsilon);
    Assert.assertEquals(0.5, crossover.getF(), epsilon);
    
    Assert.assertEquals(4, ((MultithreadedSolutionSetEvaluator)algorithm.getEvaluator()).getNumberOfThreads()) ;
  }

  @Test
  public void test2() throws JMetalException {
    double epsilon = 0.000000000000001;
    ParallelGDE3Settings GDE3Settings = new ParallelGDE3Settings("Fonseca");
    GDE3 algorithm = (GDE3) GDE3Settings.configure(configuration);

    DifferentialEvolutionCrossover crossover =
      (DifferentialEvolutionCrossover) algorithm.getCrossoverOperator();

    Assert.assertEquals(100, algorithm.getPopulationSize());
    Assert.assertEquals(250, algorithm.getMaxIterations());

    Assert.assertEquals(0.5, crossover.getCr(), epsilon);
    Assert.assertEquals(0.5, crossover.getF(), epsilon);
    
    Assert.assertEquals(8, ((MultithreadedSolutionSetEvaluator)algorithm.getEvaluator()).getNumberOfThreads()) ;

  }
}
