//  ConstraintMOEADSettingsTest.java
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
import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.experiment.Settings;
import org.uma.jmetal45.experiment.settings.ConstraintMOEADSettings;
import org.uma.jmetal45.metaheuristic.multiobjective.moead.ConstraintMOEAD;
import org.uma.jmetal45.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal45.operator.mutation.PolynomialMutation;
import org.uma.jmetal45.problem.multiobjective.Srinivas;
import org.uma.jmetal45.util.JMetalException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 13/06/13
 * Time: 22:43
 * To change this template use File | Settings | File Templates.
 */
public class ConstraintMOEADSettingsTest {

  Properties configuration;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration = new Properties();
    InputStreamReader isr = new InputStreamReader(
      new FileInputStream(ClassLoader.getSystemResource("ConstraintMOEAD.conf").getPath()));
    configuration.load(isr);
  }

  @Test
  public void testSettings() throws JMetalException {
    double epsilon = 0.000000000000001 ;
    Settings constraintMOEADSettings = new ConstraintMOEADSettings("Srinivas");
    ConstraintMOEAD algorithm = (ConstraintMOEAD)constraintMOEADSettings.configure() ;
    Problem problem = new Srinivas("Real") ;

    PolynomialMutation mutation = (PolynomialMutation) algorithm.getMutation() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;
    String dataDirectory = (String) algorithm.getDataDirectory();
    String experimentDirectoryName = ClassLoader.getSystemResource(dataDirectory).getPath();

    DifferentialEvolutionCrossover crossover =
      (DifferentialEvolutionCrossover) algorithm.getCrossover();
    double cr = crossover.getCr() ;
    double f = crossover.getF() ;

    Assert.assertEquals(300, algorithm.getPopulationSize());
    Assert.assertEquals(150000, algorithm.getMaxEvaluations());

    Assert.assertEquals(0.9, algorithm.getNeighborhoodSelectionProbability(), epsilon);
    Assert.assertEquals(20, algorithm.getNeighborSize());
    Assert.assertEquals(2, algorithm.getMaximumNumberOfReplacedSolutions());

    Assert.assertEquals(1.0, cr, epsilon);
    Assert.assertEquals(0.5, f, epsilon);
    Assert.assertEquals(20.0, dim, epsilon);
    Assert.assertEquals(1.0 / problem.getNumberOfVariables(), pm, epsilon);

    assertNotNull(experimentDirectoryName);
  }

  @Test
  public void testSettings2() throws JMetalException {
    double epsilon = 0.000000000000001 ;
    Settings constraintMOEADSettings = new ConstraintMOEADSettings("Srinivas");
    ConstraintMOEAD algorithm = (ConstraintMOEAD)constraintMOEADSettings.configure(configuration) ;
    Problem problem = new Srinivas("Real") ;

    PolynomialMutation mutation = (PolynomialMutation) algorithm.getMutation() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;
    String dataDirectory = (String) algorithm.getDataDirectory();
    String experimentDirectoryName = ClassLoader.getSystemResource(dataDirectory).getPath();

    DifferentialEvolutionCrossover crossover =
      (DifferentialEvolutionCrossover) algorithm.getCrossover();
    double cr = crossover.getCr() ;
    double f = crossover.getF() ;

    Assert.assertEquals(300, algorithm.getPopulationSize());
    Assert.assertEquals(150000, algorithm.getMaxEvaluations());

    Assert.assertEquals(0.9, algorithm.getNeighborhoodSelectionProbability(), epsilon);
    Assert.assertEquals(20, algorithm.getNeighborSize());
    Assert.assertEquals(2, algorithm.getMaximumNumberOfReplacedSolutions());

    Assert.assertEquals(1.0, cr, epsilon);
    Assert.assertEquals(0.5, f, epsilon);
    Assert.assertEquals(20.0, dim, epsilon);
    Assert.assertEquals(1.0 / problem.getNumberOfVariables(), pm, epsilon);

    assertNotNull(experimentDirectoryName);
  }
}
