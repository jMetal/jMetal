package test.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.cMOEAD_Settings;
import jmetal.operators.crossover.DifferentialEvolutionCrossover;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.problems.Fonseca;
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
 * Date: 13/06/13
 * Time: 22:43
 * To change this template use File | Settings | File Templates.
 */
public class cMOEAD_SettingsTest {

  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("cMOEAD.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void testSettings() throws JMException {
    double epsilon = 0.000000000000001 ;
    Settings cMOEADSettings = new cMOEAD_Settings("Fonseca");
    Algorithm algorithm = cMOEADSettings.configure() ;
    Problem problem = new Fonseca("Real") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;
    String dataDirectory = (String) algorithm.getInputParameter("dataDirectory");
    System.out.println(dataDirectory);
    //File experimentDirectory = new File(dataDirectory) ;
    String experimentDirectoryName = ClassLoader.getSystemResource(dataDirectory).getPath();

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    Assert.assertEquals("cMOEAD_SettingsTest", 300, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    Assert.assertEquals("cMOEAD_SettingsTest", 150000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());

    Assert.assertEquals("cMOEAD_SettingsTest", 0.9, ((Double)algorithm.getInputParameter("delta")).doubleValue(), epsilon) ;
    Assert.assertEquals("cMOEAD_SettingsTest", 20, ((Integer) algorithm.getInputParameter("T")).intValue());
    Assert.assertEquals("cMOEAD_SettingsTest", 2,   ((Integer)algorithm.getInputParameter("nr")).intValue());

    Assert.assertEquals("cMOEAD_SettingsTest", 1.0, CR, epsilon);
    Assert.assertEquals("cMOEAD_SettingsTest", 0.5, F, epsilon);
    Assert.assertEquals("cMOEAD_SettingsTest", 20.0, dim, epsilon);
    Assert.assertEquals("cMOEAD_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);

    assertNotNull("cMOEAD_SettingsTest", experimentDirectoryName);
  }

  @Test
  public void testSettings2() throws JMException {
    double epsilon = 0.000000000000001 ;
    Settings cMOEADSettings = new cMOEAD_Settings("Fonseca");
    Algorithm algorithm = cMOEADSettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;
    String dataDirectory = (String) algorithm.getInputParameter("dataDirectory");
    System.out.println(dataDirectory);
    //File experimentDirectory = new File(dataDirectory) ;
    String experimentDirectoryName = ClassLoader.getSystemResource(dataDirectory).getPath();

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    Assert.assertEquals("cMOEAD_SettingsTest", 300, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    Assert.assertEquals("cMOEAD_SettingsTest", 150000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());

    Assert.assertEquals("cMOEAD_SettingsTest", 0.9, ((Double)algorithm.getInputParameter("delta")).doubleValue(), epsilon) ;
    Assert.assertEquals("cMOEAD_SettingsTest", 20, ((Integer) algorithm.getInputParameter("T")).intValue());
    Assert.assertEquals("cMOEAD_SettingsTest", 2,   ((Integer)algorithm.getInputParameter("nr")).intValue());

    Assert.assertEquals("cMOEAD_SettingsTest", 1.0, CR, epsilon);
    Assert.assertEquals("cMOEAD_SettingsTest", 0.5, F, epsilon);
    Assert.assertEquals("cMOEAD_SettingsTest", 20.0, dim, epsilon);
    Assert.assertEquals("cMOEAD_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);

    assertNotNull("cMOEAD_SettingsTest", experimentDirectoryName);
  }
}
