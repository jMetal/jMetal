package jmetal.test.experiments.settings;

import experiments.settings.jMetalHome;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.pNSGAII_Settings;
import jmetal.operators.crossover.SBXCrossover;
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
 * Time: 07:41
 * To change this template use File | Settings | File Templates.
 */
public class pNSGAII_SettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(jMetalHome.jMetalHomeConfDir+"/pNSGAII.conf"));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings NSGAIISettings = new pNSGAII_Settings("Fonseca");
    Algorithm algorithm = NSGAIISettings.configure() ;
    Problem problem = new Fonseca("Real") ;
    SBXCrossover crossover = (SBXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    double dic = (Double)crossover.getParameter("distributionIndex") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;

    assertEquals("pNSGAII_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("pNSGAII_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());

    assertEquals("pNSGAII_SettingsTest", 0.9, pc, epsilon);
    assertEquals("pNSGAII_SettingsTest", 20.0, dic, epsilon);

    assertEquals("pNSGAII_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    assertEquals("pNSGAII_SettingsTest", 20.0, dim, epsilon);
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings NSGAIISettings = new pNSGAII_Settings("Fonseca");
    Algorithm algorithm = NSGAIISettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real") ;
    SBXCrossover crossover = (SBXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    double dic = (Double)crossover.getParameter("distributionIndex") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;

    assertEquals("pNSGAII_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("pNSGAII_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());

    assertEquals("pNSGAII_SettingsTest", 0.9, pc, epsilon);
    assertEquals("pNSGAII_SettingsTest", 20.0, dic, epsilon);

    assertEquals("pNSGAII_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    assertEquals("pNSGAII_SettingsTest", 20.0, dim, epsilon);
  }
}
