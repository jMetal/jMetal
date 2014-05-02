package jmetal.test.experiments.settings;

import experiments.settings.jMetalHome;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.AbYSS_Settings;
import jmetal.operators.crossover.SBXCrossover;
import jmetal.operators.localSearch.MutationLocalSearch;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.problems.Fonseca;
import jmetal.util.JMException;
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
 * Date: 12/06/13
 * Time: 07:48
 * To change this template use File | Settings | File Templates.
 */
public class AbYSS_SettingsTest {

  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(jMetalHome.jMetalHomeConfDir+"/AbYSS.conf"));
    configuration_.load(isr);
  }

  @Test
  public void testSettings() throws JMException {
    double epsilon = 0.000000000000001 ;
    Settings abyssSettings = new AbYSS_Settings("Fonseca");
    Algorithm algorithm = abyssSettings.configure() ;
    Problem problem = new Fonseca("Real") ;
    SBXCrossover crossover = (SBXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    double dic = (Double)crossover.getParameter("distributionIndex") ;
    MutationLocalSearch improvement = (MutationLocalSearch)algorithm.getOperator("improvement") ;
    int improvementRounds = (Integer)improvement.getParameter("improvementRounds") ;
    PolynomialMutation mutation = (PolynomialMutation)improvement.getParameter("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;

    assertEquals("AbYSS_SettingsTest", 20, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("AbYSS_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    assertEquals("AbYSS_SettingsTest", 10, ((Integer)algorithm.getInputParameter("refSet1Size")).intValue());
    assertEquals("AbYSS_SettingsTest", 10, ((Integer)algorithm.getInputParameter("refSet2Size")).intValue());
    assertEquals("AbYSS_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());

    assertEquals("AbYSS_SettingsTest", 1.0, pc, epsilon);
    assertEquals("AbYSS_SettingsTest", 20.0, dic, epsilon);

    assertEquals("AbYSS_SettingsTest", 1, improvementRounds);
    assertEquals("AbYSS_SettingsTest", 20.0, dim, epsilon);
    assertEquals("AbYSS_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
  }

  @Test
  public void testSettings2() throws JMException {
    double epsilon = 0.000000000000001 ;
    Settings abyssSettings = new AbYSS_Settings("Fonseca");
    Algorithm algorithm = abyssSettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real") ;
    SBXCrossover crossover = (SBXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    double dic = (Double)crossover.getParameter("distributionIndex") ;
    MutationLocalSearch improvement = (MutationLocalSearch)algorithm.getOperator("improvement") ;
    int improvementRounds = (Integer)improvement.getParameter("improvementRounds") ;
    PolynomialMutation mutation = (PolynomialMutation)improvement.getParameter("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;

    assertEquals("AbYSS_SettingsTest", 20, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("AbYSS_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    assertEquals("AbYSS_SettingsTest", 10, ((Integer)algorithm.getInputParameter("refSet1Size")).intValue());
    assertEquals("AbYSS_SettingsTest", 10, ((Integer)algorithm.getInputParameter("refSet2Size")).intValue());
    assertEquals("AbYSS_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());

    assertEquals("AbYSS_SettingsTest", 1.0, pc, epsilon);
    assertEquals("AbYSS_SettingsTest", 20.0, dic, epsilon);

    assertEquals("AbYSS_SettingsTest", 1, improvementRounds);
    assertEquals("AbYSS_SettingsTest", 20.0, dim, epsilon);
    assertEquals("AbYSS_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
  }
}