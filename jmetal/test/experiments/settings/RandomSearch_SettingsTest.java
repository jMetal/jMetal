package jmetal.test.experiments.settings;

import experiments.settings.jMetalHome;
import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.RandomSearch_Settings;
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
 * User: antelverde
 * Date: 27/06/13
 * Time: 07:54
 * To change this template use File | Settings | File Templates.
 */
public class RandomSearch_SettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(jMetalHome.jMetalHomeConfDir+"/RandomSearch.conf"));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    Settings randomSettings = new RandomSearch_Settings("Fonseca");
    Algorithm algorithm = randomSettings.configure() ;

    assertEquals("RandomSearch_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
  }

  @Test
  public void testConfigure2() throws Exception {
    Settings randomSettings = new RandomSearch_Settings("Fonseca");
    Algorithm algorithm = randomSettings.configure(configuration_) ;

    assertEquals("RandomSearch_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
  }
}
