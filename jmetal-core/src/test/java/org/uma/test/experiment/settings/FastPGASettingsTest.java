//  FastPGASettingsTest.java
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
import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.experiment.Settings;
import org.uma.jmetal45.experiment.settings.FastPGASettings;
import org.uma.jmetal45.metaheuristic.multiobjective.fastpga.FastPGA;
import org.uma.jmetal45.problem.multiobjective.Fonseca;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 19/08/14
 */
public class FastPGASettingsTest {
  Properties configuration;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("FastPGA.conf").getPath()));
    configuration.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings FastPGASettings = new FastPGASettings("Fonseca");
    FastPGA algorithm = (FastPGA) FastPGASettings.configure() ;
    Problem problem = new Fonseca("Real") ;

    assertEquals(100, algorithm.getMaxPopulationSize());
    assertEquals(100, algorithm.getInitialPopulationSize());
    assertEquals(25000, algorithm.getMaxEvaluations());

    assertEquals(20.0, algorithm.getA(), epsilon);
    assertEquals(1.0, algorithm.getB(), epsilon);
    assertEquals(20.0, algorithm.getC(), epsilon);
    assertEquals(0.0, algorithm.getD(), epsilon);

    assertEquals(1, algorithm.getTermination());
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings FastPGASettings = new FastPGASettings("Fonseca");
    FastPGA algorithm = (FastPGA) FastPGASettings.configure(configuration) ;
    Problem problem = new Fonseca("Real") ;

    assertEquals(100, algorithm.getMaxPopulationSize());
    assertEquals(100, algorithm.getInitialPopulationSize());
    assertEquals(25000, algorithm.getMaxEvaluations());

    assertEquals(20.0, algorithm.getA(), epsilon);
    assertEquals(1.0, algorithm.getB(), epsilon);
    assertEquals(20.0, algorithm.getC(), epsilon);
    assertEquals(0.0, algorithm.getD(), epsilon);

    assertEquals(1, algorithm.getTermination());
  }
}
