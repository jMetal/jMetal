//  MOCell_SettingsTest.java
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

package test.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.MOCell_Settings;
import jmetal.operators.crossover.SBXCrossover;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.problems.Fonseca;

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
 * User: antelverde
 * Date: 17/06/13
 * Time: 22:54
 * To change this template use File | Settings | File Templates.
 */
public class MOCell_SettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("MOCell.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings mocellSettings = new MOCell_Settings("Fonseca");
    Algorithm algorithm = mocellSettings.configure() ;
    Problem problem = new Fonseca("Real") ;
    SBXCrossover crossover = (SBXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    double dic = (Double)crossover.getParameter("distributionIndex") ;
    //MutationLocalSearch improvement = (MutationLocalSearch)algorithm.getOperator("improvement") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;

    Assert.assertEquals("MOCell_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    Assert.assertEquals("MOCell_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    Assert.assertEquals("MOCell_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());
    Assert.assertEquals("MOCell_SettingsTest", 20, ((Integer)algorithm.getInputParameter("feedBack")).intValue());

    Assert.assertEquals("MOCell_SettingsTest", 0.9, pc, epsilon);
    Assert.assertEquals("MOCell_SettingsTest", 20.0, dic, epsilon);

    Assert.assertEquals("MOCell_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    Assert.assertEquals("MOCell_SettingsTest", 20.0, dim, epsilon);
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings mocellSettings = new MOCell_Settings("Fonseca");
    Algorithm algorithm = mocellSettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real") ;
    SBXCrossover crossover = (SBXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    double dic = (Double)crossover.getParameter("distributionIndex") ;
    //MutationLocalSearch improvement = (MutationLocalSearch)algorithm.getOperator("improvement") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;

    Assert.assertEquals("MOCell_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    Assert.assertEquals("MOCell_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    Assert.assertEquals("MOCell_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());
    Assert.assertEquals("MOCell_SettingsTest", 20, ((Integer)algorithm.getInputParameter("feedback")).intValue());

    Assert.assertEquals("MOCell_SettingsTest", 0.9, pc, epsilon);
    Assert.assertEquals("MOCell_SettingsTest", 20.0, dic, epsilon);

    Assert.assertEquals("MOCell_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    Assert.assertEquals("MOCell_SettingsTest", 20.0, dim, epsilon);
  }
}
