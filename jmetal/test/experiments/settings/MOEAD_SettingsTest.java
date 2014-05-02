package jmetal.test.experiments.settings;

import experiments.settings.jMetalHome;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.MOEAD_Settings;
import jmetal.operators.crossover.DifferentialEvolutionCrossover;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.problems.Fonseca;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 26/06/13
 * Time: 07:48
 * To change this template use File | Settings | File Templates.
 */
public class MOEAD_SettingsTest {

  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(jMetalHome.jMetalHomeConfDir+"/MOEAD.conf"));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001;
    Settings MOEADSettings = new MOEAD_Settings("Fonseca");
    Algorithm algorithm = MOEADSettings.configure();
    Problem problem = new Fonseca("Real");
    PolynomialMutation mutation = (PolynomialMutation) algorithm.getOperator("mutation");
    double pm = (Double) mutation.getParameter("probability");
    double dim = (Double) mutation.getParameter("distributionIndex");
    String dataDirectory = (String) algorithm.getInputParameter("dataDirectory");
    System.out.println(dataDirectory);
    File experimentDirectory = new File(dataDirectory);


    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover) algorithm.getOperator("crossover");
    double CR = (Double) crossover.getParameter("CR");
    double F = (Double) crossover.getParameter("F");

    assertEquals("MOEAD_SettingsTest", 300, ((Integer) algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("MOEAD_SettingsTest", 150000, ((Integer) algorithm.getInputParameter("maxEvaluations")).intValue());

    assertEquals("MOEAD_SettingsTest", 0.9, ((Double) algorithm.getInputParameter("delta")).doubleValue(), epsilon);
    assertEquals("MOEAD_SettingsTest", 20, ((Integer) algorithm.getInputParameter("T")).intValue());
    assertEquals("MOEAD_SettingsTest", 2, ((Integer) algorithm.getInputParameter("nr")).intValue());

    assertEquals("MOEAD_SettingsTest", 1.0, CR, epsilon);
    assertEquals("MOEAD_SettingsTest", 0.5, F, epsilon);
    assertEquals("MOEAD_SettingsTest", 20.0, dim, epsilon);
    assertEquals("MOEAD_SettingsTest", 1.0 / problem.getNumberOfVariables(), pm, epsilon);

    assertTrue("MOEAD_SettingsTest", experimentDirectory.exists());
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001;
    Settings MOEADSettings = new MOEAD_Settings("Fonseca");
    Algorithm algorithm = MOEADSettings.configure(configuration_);
    Problem problem = new Fonseca("Real");
    PolynomialMutation mutation = (PolynomialMutation) algorithm.getOperator("mutation");
    double pm = (Double) mutation.getParameter("probability");
    double dim = (Double) mutation.getParameter("distributionIndex");
    String dataDirectory = (String) algorithm.getInputParameter("dataDirectory");
    System.out.println(dataDirectory);
    File experimentDirectory = new File(dataDirectory);


    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover) algorithm.getOperator("crossover");
    double CR = (Double) crossover.getParameter("CR");
    double F = (Double) crossover.getParameter("F");

    assertEquals("MOEAD_SettingsTest", 300, ((Integer) algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("MOEAD_SettingsTest", 150000, ((Integer) algorithm.getInputParameter("maxEvaluations")).intValue());

    assertEquals("MOEAD_SettingsTest", 0.9, ((Double) algorithm.getInputParameter("delta")).doubleValue(), epsilon);
    assertEquals("MOEAD_SettingsTest", 20, ((Integer) algorithm.getInputParameter("T")).intValue());
    assertEquals("MOEAD_SettingsTest", 2, ((Integer) algorithm.getInputParameter("nr")).intValue());

    assertEquals("MOEAD_SettingsTest", 1.0, CR, epsilon);
    assertEquals("MOEAD_SettingsTest", 0.5, F, epsilon);
    assertEquals("MOEAD_SettingsTest", 20.0, dim, epsilon);
    assertEquals("MOEAD_SettingsTest", 1.0 / problem.getNumberOfVariables(), pm, epsilon);

    assertTrue("MOEAD_SettingsTest", experimentDirectory.exists());
  }
}
