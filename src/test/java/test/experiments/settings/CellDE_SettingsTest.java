//  CellDE_SettingsTest.java
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
import jmetal.experiments.Settings;
import jmetal.experiments.settings.CellDE_Settings;
import jmetal.operators.crossover.DifferentialEvolutionCrossover;
import jmetal.util.JMetalException;

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
 * User: Antonio J. Nebro
 * Date: 13/06/13
 * Time: 22:07
 * To change this template use File | Settings | File Templates.
 */
public class CellDE_SettingsTest  {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("CellDE.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void settingsTest() throws JMetalException {
    double epsilon = 0.000000000000001 ;
    Settings cellDESettings = new CellDE_Settings("Fonseca");
    Algorithm algorithm = cellDESettings.configure() ;

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    assertEquals("CellDE_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("CellDE_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    assertEquals("CellDE_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());
    assertEquals("CellDE_SettingsTest", 20, ((Integer)algorithm.getInputParameter("archiveFeedBack")).intValue());

    assertEquals("CellDE_SettingsTest", 0.5, CR, epsilon);
    assertEquals("CellDE_SettingsTest", 0.5, F, epsilon);
  }

  @Test
  public void settingsFromConfigurationFileTest() throws JMetalException {
    double epsilon = 0.000000000000001 ;
    Settings cellDESettings = new CellDE_Settings("Fonseca");
    Algorithm algorithm = cellDESettings.configure(configuration_) ;

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    assertEquals("CellDE_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("CellDE_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    assertEquals("CellDE_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());
    assertEquals("CellDE_SettingsTest", 20, ((Integer)algorithm.getInputParameter("archiveFeedBack")).intValue());

    assertEquals("CellDE_SettingsTest", 0.5, CR, epsilon);
    assertEquals("CellDE_SettingsTest", 0.5, F, epsilon);
  }

  @Test (expected = JMetalException.class)
  public void problemNameInvalidTest() throws JMetalException {
    double epsilon = 0.000000000000001 ;
    Settings cellDESettings = new CellDE_Settings("Anyproblem");
    Algorithm algorithm = cellDESettings.configure() ;
  }
}
