package jmetal.test.experiments.settings;

import experiments.settings.jMetalHome;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.GDE3_Settings;
import jmetal.operators.crossover.DifferentialEvolutionCrossover;
import jmetal.problems.Fonseca;
import jmetal.util.JMException;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;


/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 16/06/13
 * Time: 20:59
 * To change this template use File | Settings | File Templates.
 */
public class GDE3_SettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(jMetalHome.jMetalHomeConfDir+"/GDE3.conf"));
    configuration_.load(isr);
  }

  @Test
  public void test() throws JMException {
    double epsilon = 0.000000000000001;
    Settings GDE3Settings = new GDE3_Settings("Fonseca");
    Algorithm algorithm = GDE3Settings.configure();
    Problem problem = new Fonseca("Real");

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    assertEquals("GDE3_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("GDE3_SettingsTest", 250, ((Integer)algorithm.getInputParameter("maxIterations")).intValue());

    assertEquals("GDE3_SettingsTest", 0.5, CR, epsilon);
    assertEquals("GDE3_SettingsTest", 0.5, F, epsilon);
  }

  @Test
  public void test2() throws JMException {
    double epsilon = 0.000000000000001;
    Settings GDE3Settings = new GDE3_Settings("Fonseca");
    Algorithm algorithm = GDE3Settings.configure(configuration_);
    Problem problem = new Fonseca("Real");

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    assertEquals("GDE3_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("GDE3_SettingsTest", 250, ((Integer)algorithm.getInputParameter("maxIterations")).intValue());

    assertEquals("GDE3_SettingsTest", 0.5, CR, epsilon);
    assertEquals("GDE3_SettingsTest", 0.5, F, epsilon);
  }
}
