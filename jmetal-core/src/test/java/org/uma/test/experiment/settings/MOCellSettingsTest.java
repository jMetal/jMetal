//  MOCellSettings.java
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
import org.uma.jmetal.experiment.settings.MOCellSettings;
import org.uma.jmetal.metaheuristic.multiobjective.mocell.*;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.Fonseca;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 17/06/13
 * Time: 22:54
 */
public class MOCellSettingsTest {
  Properties configuration ;

  @Before
  public void init() throws IOException {
    configuration = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("MOCell.conf").getPath()));
    configuration.load(isr);
  }

  @Test
  public void testConfigureAsyncMOCell1() throws Exception {
    double epsilon = 0.000000000000001 ;
    MOCellSettings mocellSettings = new MOCellSettings("Fonseca");
    AsyncMOCell1 algorithm = (AsyncMOCell1)mocellSettings.configure("AsyncMOCell1") ;

    Problem problem = new Fonseca("Real") ;

    SBXCrossover crossover = (SBXCrossover)algorithm.getCrossoverOperator() ;
    double pc = crossover.getCrossoverProbability() ;
    double dic = crossover.getDistributionIndex() ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutationOperator() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;

    Assert.assertEquals(100, algorithm.getPopulationSize());
    Assert.assertEquals(25000, algorithm.getMaxEvaluations());
    Assert.assertEquals(100,algorithm.getArchiveSize());
    Assert.assertEquals(20, algorithm.getNumberOfFeedbackSolutionsFromArchive());

    Assert.assertEquals(0.9, pc, epsilon);
    Assert.assertEquals(20.0, dic, epsilon);

    Assert.assertEquals(1.0/problem.getNumberOfVariables(), pm, epsilon);
    Assert.assertEquals(20.0, dim, epsilon);
  }

  @Test
  public void testConfigureAsyncMOCell2() throws Exception {
    double epsilon = 0.000000000000001 ;
    MOCellSettings mocellSettings = new MOCellSettings("Fonseca");
    AsyncMOCell2 algorithm = (AsyncMOCell2)mocellSettings.configure("AsyncMOCell2") ;

    Problem problem = new Fonseca("Real") ;

    SBXCrossover crossover = (SBXCrossover)algorithm.getCrossoverOperator() ;
    double pc = crossover.getCrossoverProbability() ;
    double dic = crossover.getDistributionIndex() ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutationOperator() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;

    Assert.assertEquals(100, algorithm.getPopulationSize());
    Assert.assertEquals(25000, algorithm.getMaxEvaluations());
    Assert.assertEquals(100,algorithm.getArchiveSize());
    Assert.assertEquals(20, algorithm.getNumberOfFeedbackSolutionsFromArchive());

    Assert.assertEquals(0.9, pc, epsilon);
    Assert.assertEquals(20.0, dic, epsilon);

    Assert.assertEquals(1.0/problem.getNumberOfVariables(), pm, epsilon);
    Assert.assertEquals(20.0, dim, epsilon);
  }

  @Test
  public void testConfigureAsyncMOCell3() throws Exception {
    double epsilon = 0.000000000000001 ;
    MOCellSettings mocellSettings = new MOCellSettings("Fonseca");
    AsyncMOCell3 algorithm = (AsyncMOCell3)mocellSettings.configure("AsyncMOCell3") ;

    Problem problem = new Fonseca("Real") ;

    SBXCrossover crossover = (SBXCrossover)algorithm.getCrossoverOperator() ;
    double pc = crossover.getCrossoverProbability() ;
    double dic = crossover.getDistributionIndex() ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutationOperator() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;

    Assert.assertEquals(100, algorithm.getPopulationSize());
    Assert.assertEquals(25000, algorithm.getMaxEvaluations());
    Assert.assertEquals(100,algorithm.getArchiveSize());
    Assert.assertEquals(20, algorithm.getNumberOfFeedbackSolutionsFromArchive());

    Assert.assertEquals(0.9, pc, epsilon);
    Assert.assertEquals(20.0, dic, epsilon);

    Assert.assertEquals(1.0/problem.getNumberOfVariables(), pm, epsilon);
    Assert.assertEquals(20.0, dim, epsilon);
  }

  @Test
  public void testConfigureAsyncMOCell4() throws Exception {
    double epsilon = 0.000000000000001 ;
    MOCellSettings mocellSettings = new MOCellSettings("Fonseca");
    AsyncMOCell4 algorithm = (AsyncMOCell4)mocellSettings.configure("AsyncMOCell4") ;

    Problem problem = new Fonseca("Real") ;

    SBXCrossover crossover = (SBXCrossover)algorithm.getCrossoverOperator() ;
    double pc = crossover.getCrossoverProbability() ;
    double dic = crossover.getDistributionIndex() ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutationOperator() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;

    Assert.assertEquals(100, algorithm.getPopulationSize());
    Assert.assertEquals(25000, algorithm.getMaxEvaluations());
    Assert.assertEquals(100,algorithm.getArchiveSize());
    Assert.assertEquals(20, algorithm.getNumberOfFeedbackSolutionsFromArchive());

    Assert.assertEquals(0.9, pc, epsilon);
    Assert.assertEquals(20.0, dic, epsilon);

    Assert.assertEquals(1.0/problem.getNumberOfVariables(), pm, epsilon);
    Assert.assertEquals(20.0, dim, epsilon);
  }

  @Test
  public void testConfigureSyncMOCell1() throws Exception {
    double epsilon = 0.000000000000001 ;
    MOCellSettings mocellSettings = new MOCellSettings("Fonseca");
    SyncMOCell1 algorithm = (SyncMOCell1)mocellSettings.configure("SyncMOCell1") ;

    Problem problem = new Fonseca("Real") ;

    SBXCrossover crossover = (SBXCrossover)algorithm.getCrossoverOperator() ;
    double pc = crossover.getCrossoverProbability() ;
    double dic = crossover.getDistributionIndex() ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutationOperator() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;

    Assert.assertEquals(100, algorithm.getPopulationSize());
    Assert.assertEquals(25000, algorithm.getMaxEvaluations());
    Assert.assertEquals(100,algorithm.getArchiveSize());
    Assert.assertEquals(20, algorithm.getNumberOfFeedbackSolutionsFromArchive());

    Assert.assertEquals(0.9, pc, epsilon);
    Assert.assertEquals(20.0, dic, epsilon);

    Assert.assertEquals(1.0/problem.getNumberOfVariables(), pm, epsilon);
    Assert.assertEquals(20.0, dim, epsilon);
  }

  @Test
  public void testConfigureSyncMOCell2() throws Exception {
    double epsilon = 0.000000000000001 ;
    MOCellSettings mocellSettings = new MOCellSettings("Fonseca");
    SyncMOCell2 algorithm = (SyncMOCell2)mocellSettings.configure("SyncMOCell2") ;

    Problem problem = new Fonseca("Real") ;

    SBXCrossover crossover = (SBXCrossover)algorithm.getCrossoverOperator() ;
    double pc = crossover.getCrossoverProbability() ;
    double dic = crossover.getDistributionIndex() ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutationOperator() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;

    Assert.assertEquals(100, algorithm.getPopulationSize());
    Assert.assertEquals(25000, algorithm.getMaxEvaluations());
    Assert.assertEquals(100,algorithm.getArchiveSize());
    Assert.assertEquals(20, algorithm.getNumberOfFeedbackSolutionsFromArchive());

    Assert.assertEquals(0.9, pc, epsilon);
    Assert.assertEquals(20.0, dic, epsilon);

    Assert.assertEquals(1.0/problem.getNumberOfVariables(), pm, epsilon);
    Assert.assertEquals(20.0, dim, epsilon);
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings mocellSettings = new MOCellSettings("Fonseca");
    MOCellTemplate algorithm = (MOCellTemplate)mocellSettings.configure(configuration) ;

    Problem problem = new Fonseca("Real") ;

    SBXCrossover crossover = (SBXCrossover)algorithm.getCrossoverOperator() ;
    double pc = crossover.getCrossoverProbability() ;
    double dic = crossover.getDistributionIndex() ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getMutationOperator() ;
    double pm = mutation.getMutationProbability() ;
    double dim = mutation.getDistributionIndex() ;

    Assert.assertEquals(100, algorithm.getPopulationSize());
    Assert.assertEquals(25000, algorithm.getMaxEvaluations());
    Assert.assertEquals(100,algorithm.getArchiveSize());
    Assert.assertEquals(20, algorithm.getNumberOfFeedbackSolutionsFromArchive());

    Assert.assertEquals(0.9, pc, epsilon);
    Assert.assertEquals(20.0, dic, epsilon);

    Assert.assertEquals(1.0/problem.getNumberOfVariables(), pm, epsilon);
    Assert.assertEquals(20.0, dim, epsilon);
  }
}
