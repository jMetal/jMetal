package test.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.dMOPSO_Settings;
import jmetal.util.JMException;

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
public class dMOPSO_SettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("dMOPSO.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void test() throws JMException {
    double epsilon = 0.000000000000001;
    Settings dMOPSOSettings = new dMOPSO_Settings("Fonseca");
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
  public void test2() throws JMException {
    //double epsilon = 0.000000000000001;
    Settings dMOPSOSettings = new dMOPSO_Settings("Fonseca");
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
