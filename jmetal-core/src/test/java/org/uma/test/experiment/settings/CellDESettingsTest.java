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

package org.uma.test.experiment.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.settings.CellDESettings;
import org.uma.jmetal.metaheuristic.multiobjective.cellde.CellDE;
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.util.JMetalException;

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
public class CellDESettingsTest {
  Properties configuration ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("CellDE.conf").getPath()));
    configuration.load(isr);
  }

  @Test
  public void settingsTest() throws JMetalException {
    double epsilon = 0.000000000000001 ;
    Settings cellDESettings = new CellDESettings("Fonseca");
    CellDE algorithm = (CellDE)cellDESettings.configure() ;

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getCrossover() ;

    assertEquals("CellDE_SettingsTest", 100, algorithm.getPopulationSize());
    assertEquals("CellDE_SettingsTest", 25000, algorithm.getMaxEvaluations());
    assertEquals("CellDE_SettingsTest", 100, algorithm.getArchiveSize());
    assertEquals("CellDE_SettingsTest", 20, algorithm.getNumberOfFeedbackSolutionsFromArchive());

    assertEquals("CellDE_SettingsTest", 0.5, crossover.getCr(), epsilon);
    assertEquals("CellDE_SettingsTest", 0.5, crossover.getF(), epsilon);
  }

  @Test
  public void settingsFromConfigurationFileTest() throws JMetalException {
    double epsilon = 0.000000000000001 ;
    Settings cellDESettings = new CellDESettings("Fonseca");
    CellDE algorithm = (CellDE)cellDESettings.configure(configuration) ;

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getCrossover() ;

    assertEquals("CellDE_SettingsTest", 100, algorithm.getPopulationSize());
    assertEquals("CellDE_SettingsTest", 25000, algorithm.getMaxEvaluations());
    assertEquals("CellDE_SettingsTest", 100, algorithm.getArchiveSize());
    assertEquals("CellDE_SettingsTest", 20, algorithm.getNumberOfFeedbackSolutionsFromArchive());

    assertEquals("CellDE_SettingsTest", 0.5, crossover.getCr(), epsilon);
    assertEquals("CellDE_SettingsTest", 0.5, crossover.getF(), epsilon);
  }
}
