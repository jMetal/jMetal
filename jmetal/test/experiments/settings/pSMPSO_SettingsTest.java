package jmetal.test.experiments.settings;

import experiments.settings.jMetalHome;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.pSMPSO_Settings;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.problems.Fonseca;
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
 * Time: 07:44
 * To change this template use File | Settings | File Templates.
 */
public class pSMPSO_SettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(jMetalHome.jMetalHomeConfDir+"/pSMPSO.conf"));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings smpsoSettings = new pSMPSO_Settings("Fonseca");
    Algorithm algorithm = smpsoSettings.configure() ;
    Problem problem = new Fonseca("Real") ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;

    assertEquals("pSMPSO_SettingsTest", 100, ((Integer)algorithm.getInputParameter("swarmSize")).intValue());
    assertEquals("pSMPSO_SettingsTest", 250, ((Integer)algorithm.getInputParameter("maxIterations")).intValue());
    assertEquals("pSMPSO_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());

    assertEquals("pSMPSO_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    assertEquals("pSMPSO_SettingsTest", 20.0, dim, epsilon);
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings smpsoSettings = new pSMPSO_Settings("Fonseca");
    Algorithm algorithm = smpsoSettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real") ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;

    assertEquals("pSMPSO_SettingsTest", 100, ((Integer)algorithm.getInputParameter("swarmSize")).intValue());
    assertEquals("pSMPSO_SettingsTest", 250, ((Integer)algorithm.getInputParameter("maxIterations")).intValue());
    assertEquals("pSMPSO_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());

    assertEquals("pSMPSO_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    assertEquals("pSMPSO_SettingsTest", 20.0, dim, epsilon);
  }
}
