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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.settings.OMOPSOSettings;
import org.uma.jmetal.metaheuristic.multiobjective.omopso.OMOPSO;
import org.uma.jmetal.operator.mutation.NonUniformMutation;
import org.uma.jmetal.operator.mutation.UniformMutation;
import org.uma.jmetal.problem.Fonseca;
import org.uma.jmetal.problem.ZDT.ZDT6;
import org.uma.jmetal.util.evaluator.MultithreadedSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

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
    OMOPSO algorithm = (OMOPSO)omopsoSettings.configure() ;
    Problem problem = new Fonseca("Real");

    UniformMutation uniformMutation = algorithm.getUniformMutation() ;
    NonUniformMutation nonUniformMutation = algorithm.getNonUniformMutation() ;

    Assert.assertEquals(100, algorithm.getSwarmSize());
    Assert.assertEquals(250, algorithm.getMaxIterations());
    Assert.assertEquals(100, algorithm.getArchiveSize());

    Assert.assertEquals(1.0/problem.getNumberOfVariables(), uniformMutation.getMutationProbability() , epsilon);
    Assert.assertEquals(0.5, uniformMutation.getPerturbation() , epsilon);
    Assert.assertEquals(250, nonUniformMutation.getMaxIterations());
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings omopsoSettings = new OMOPSOSettings("Fonseca");
    OMOPSO algorithm = (OMOPSO)omopsoSettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real");

    UniformMutation uniformMutation = algorithm.getUniformMutation() ;
    NonUniformMutation nonUniformMutation = algorithm.getNonUniformMutation() ;

    Assert.assertEquals(100, algorithm.getSwarmSize());
    Assert.assertEquals(250, algorithm.getMaxIterations());
    Assert.assertEquals(100, algorithm.getArchiveSize());

    Assert.assertEquals(1.0/problem.getNumberOfVariables(), uniformMutation.getMutationProbability() , epsilon);
    Assert.assertEquals(0.5, uniformMutation.getPerturbation() , epsilon);
    Assert.assertEquals(250, nonUniformMutation.getMaxIterations());
  }

  @Test
  public void testExecuteOMOPSO() throws ClassNotFoundException, IOException {
    Settings omopsoSettings = new OMOPSOSettings("ZDT6");
    OMOPSO algorithm = (OMOPSO)omopsoSettings.configure() ;

    SolutionSet solutionSet = algorithm.execute() ;
    assertEquals(100, solutionSet.size());
  }

  @Test
  public void testExecuteParallelOMOPSO() throws ClassNotFoundException, IOException {
    Problem problem = new ZDT6("Real") ;
    SolutionSetEvaluator evaluator = new MultithreadedSolutionSetEvaluator(8, problem);

    OMOPSO algorithm;
    UniformMutation uniformMutation;
    NonUniformMutation nonUniformMutation;

    uniformMutation = new UniformMutation.Builder(0.5, 1.0/problem.getNumberOfVariables())
      .build() ;

    nonUniformMutation = new NonUniformMutation.Builder(0.5, 1.0/problem.getNumberOfVariables(), 250)
      .build() ;

    algorithm = new OMOPSO.Builder(problem, evaluator)
      .swarmSize(100)
      .archiveSize(100)
      .maxIterations(250)
      .uniformMutation(uniformMutation)
      .nonUniformMutation(nonUniformMutation)
      .build() ;

    SolutionSet solutionSet = algorithm.execute() ;
    assertEquals(100, solutionSet.size());
  }
}
