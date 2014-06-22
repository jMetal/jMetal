//  dMOPSO_SettingsTest.java
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

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.settings.DMOPSOSettings;
import org.uma.jmetal.util.JMetalException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 16/06/13
 * Time: 00:28
 */
public class DMOPSOSettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("dMOPSO.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void test() throws JMetalException {
    double epsilon = 0.000000000000001;
    Settings dMOPSOSettings = new DMOPSOSettings("Fonseca");
    Algorithm algorithm = dMOPSOSettings.configure();

    String dataDirectory = (String) algorithm.getInputParameter("dataDirectory");
    System.out.println(dataDirectory);
    //File experimentDirectory = new File(dataDirectory);
    String experimentDirectoryName = ClassLoader.getSystemResource(dataDirectory).getPath();


    Assert.assertEquals("dMOPSO_SettingsTest", 100, ((Integer) algorithm.getInputParameter("swarmSize")).intValue());
    Assert.assertEquals("dMOPSO_SettingsTest", 250, ((Integer) algorithm.getInputParameter("maxIterations")).intValue());
    Assert.assertEquals("dMOPSO_SettingsTest", 2, ((Integer) algorithm.getInputParameter("maxAge")).intValue());
    Assert.assertEquals("dMOPSO_SettingsTest", "_TCHE", algorithm.getInputParameter("functionType"));

    assertNotNull("dMOPSO_SettingsTest", experimentDirectoryName);
  }

  @Test
  public void test2() throws JMetalException {
    //double epsilon = 0.000000000000001;
    Settings dMOPSOSettings = new DMOPSOSettings("Fonseca");
    Algorithm algorithm = dMOPSOSettings.configure(configuration_);

    String dataDirectory = (String) algorithm.getInputParameter("dataDirectory");
    System.out.println(dataDirectory);
    //File experimentDirectory = new File(dataDirectory);
    String experimentDirectoryName = ClassLoader.getSystemResource(dataDirectory).getPath();

    Assert.assertEquals("dMOPSO_SettingsTest", 100, ((Integer) algorithm.getInputParameter("swarmSize")).intValue());
    Assert.assertEquals("dMOPSO_SettingsTest", 250, ((Integer) algorithm.getInputParameter("maxIterations")).intValue());
    Assert.assertEquals("dMOPSO_SettingsTest", 100, ((Integer) algorithm.getInputParameter("swarmSize")).intValue());
    Assert.assertEquals("dMOPSO_SettingsTest", 250, ((Integer) algorithm.getInputParameter("maxIterations")).intValue());
    Assert.assertEquals("dMOPSO_SettingsTest", 2, ((Integer) algorithm.getInputParameter("maxAge")).intValue());
    Assert.assertEquals("dMOPSO_SettingsTest", "_TCHE", algorithm.getInputParameter("functionType"));

    assertNotNull("dMOPSO_SettingsTest", experimentDirectoryName);
  }
}
