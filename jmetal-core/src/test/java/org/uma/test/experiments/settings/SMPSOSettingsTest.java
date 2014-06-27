//  SMPSO_SettingsTest.java
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

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.settings.SMPSOSettings;
import org.uma.jmetal.metaheuristic.multiobjective.smpso.SMPSO;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Fonseca;
import org.uma.jmetal.qualityIndicator.fastHypervolume.FastHypervolumeArchive;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.evaluator.MultithreadedSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 27/06/13
 * Time: 07:48
 * To change this template use File | Settings | File Templates.
 */
public class SMPSOSettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("SMPSO.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings smpsoSettings = new SMPSOSettings("Fonseca");
    SMPSO algorithm = (SMPSO)smpsoSettings.configure() ;
    Problem problem = new Fonseca("Real") ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutation() ;

    assertEquals(100, algorithm.getSwarmSize());
    assertEquals(250, algorithm.getMaxIterations());
    assertEquals(100, algorithm.getArchiveSize());

    assertEquals(2.5, algorithm.getC1Max(), epsilon);
    assertEquals(1.5, algorithm.getC1Min(), epsilon);
    assertEquals(2.5, algorithm.getC2Max(), epsilon);
    assertEquals(1.5, algorithm.getC2Min(), epsilon);
    assertEquals(1.0, algorithm.getR1Max(), epsilon);
    assertEquals(0.0, algorithm.getR1Min(), epsilon);
    assertEquals(1.0, algorithm.getR2Max(), epsilon);
    assertEquals(0.0, algorithm.getR2Min(), epsilon);

    assertEquals(0.1, algorithm.getWeightMax(), epsilon);
    assertEquals(0.1, algorithm.getWeightMin(), epsilon);

    assertEquals(-1, algorithm.getChangeVelocity1(), epsilon);
    assertEquals(-1, algorithm.getChangeVelocity2(), epsilon);

    assertEquals(1.0/problem.getNumberOfVariables(), mutation.getMutationProbability(), epsilon);
    assertEquals(20.0, mutation.getDistributionIndex(), epsilon);
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings smpsoSettings = new SMPSOSettings("Fonseca");
    SMPSO algorithm = (SMPSO)smpsoSettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real") ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutation() ;

    assertEquals(100, algorithm.getSwarmSize());
    assertEquals(250, algorithm.getMaxIterations());
    assertEquals(100, algorithm.getArchiveSize());

    assertEquals(2.5, algorithm.getC1Max(), epsilon);
    assertEquals(1.5, algorithm.getC1Min(), epsilon);
    assertEquals(2.5, algorithm.getC2Max(), epsilon);
    assertEquals(1.5, algorithm.getC2Min(), epsilon);
    assertEquals(1.0, algorithm.getR1Max(), epsilon);
    assertEquals(0.0, algorithm.getR1Min(), epsilon);
    assertEquals(1.0, algorithm.getR2Max(), epsilon);
    assertEquals(0.0, algorithm.getR2Min(), epsilon);

    assertEquals(0.1, algorithm.getWeightMax(), epsilon);
    assertEquals(0.1, algorithm.getWeightMin(), epsilon);

    assertEquals(-1, algorithm.getChangeVelocity1(), epsilon);
    assertEquals(-1, algorithm.getChangeVelocity2(), epsilon);
  }


  @Test
  public void testExecuteSMPSO() throws ClassNotFoundException, IOException {
    Settings smpsoSettings = new SMPSOSettings("Fonseca");
    SMPSO algorithm = (SMPSO)smpsoSettings.configure() ;

    SolutionSet solutionSet = algorithm.execute() ;
    assertEquals(100, solutionSet.size());
  }

  @Test
  public void testExecuteSMPSOWithConfigurationFile() throws ClassNotFoundException, IOException {
    Settings smpsoSettings = new SMPSOSettings("Fonseca");
    SMPSO algorithm = (SMPSO)smpsoSettings.configure(configuration_) ;

    SolutionSet solutionSet = algorithm.execute() ;
    assertEquals(100, solutionSet.size());
  }

  @Test
  public void testExecuteSMPSOhv() throws ClassNotFoundException, IOException {
    Problem problem = new Fonseca("Real") ;
    Archive archive = new FastHypervolumeArchive(100, problem.getNumberOfObjectives()) ;
    SolutionSetEvaluator
      evaluator = new SequentialSolutionSetEvaluator();

    Operator mutation = new PolynomialMutation.Builder()
      .distributionIndex(20.0)
      .probability(1.0 / problem.getNumberOfVariables())
      .build();

    SMPSO algorithm = new SMPSO.Builder(problem, archive, evaluator)
      .mutation(mutation)
      .maxIterations(250)
      .swarmSize(100)
      .archiveSize(100)
      .build();

    SolutionSet solutionSet = algorithm.execute() ;
    assertEquals(100, solutionSet.size());
  }

  @Test
  public void testExecuteParallelSMPSO() throws ClassNotFoundException, IOException {
    Problem problem = new Fonseca("Real") ;
    Archive archive = new FastHypervolumeArchive(100, problem.getNumberOfObjectives()) ;
    SolutionSetEvaluator evaluator = new MultithreadedSolutionSetEvaluator(6, problem);

    Operator mutation = new PolynomialMutation.Builder()
      .distributionIndex(20.0)
      .probability(1.0 / problem.getNumberOfVariables())
      .build();

    SMPSO algorithm = new SMPSO.Builder(problem, archive, evaluator)
      .mutation(mutation)
      .maxIterations(250)
      .swarmSize(100)
      .archiveSize(100)
      .build();

    SolutionSet solutionSet = algorithm.execute() ;
    assertEquals(100, solutionSet.size());
  }

  /*
  @Test
  public void testExecuteSMPSOWithConfigurationFile() throws JMException, ClassNotFoundException, IOException {
    Settings smpsoSettings = new SMPSO_Settings("Fonseca");
    Algorithm algorithm = smpsoSettings.configure(configuration) ;
    
    SolutionSet solutionSet = algorithm.execute() ;
    assertEquals("testExecuteSMPSO", 100, solutionSet.size());        
  }  */
}
