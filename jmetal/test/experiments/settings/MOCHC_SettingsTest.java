package jmetal.test.experiments.settings;

import experiments.settings.jMetalHome;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.MOCHC_Settings;
import jmetal.operators.crossover.HUXCrossover;
import jmetal.operators.mutation.BitFlipMutation;
import jmetal.operators.selection.RankingAndCrowdingSelection;
import jmetal.problems.ZDT.ZDT5;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 20/06/13
 * Time: 22:21
 * To change this template use File | Settings | File Templates.
 */
public class MOCHC_SettingsTest {

  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(jMetalHome.jMetalHomeConfDir+"/MOCHC.conf"));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings mochcSettings = new MOCHC_Settings("ZDT5");
    Algorithm algorithm = mochcSettings.configure() ;
    Problem problem = new ZDT5("Binary") ;

    HUXCrossover crossover = (HUXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    RankingAndCrowdingSelection rankingAndCrowdingSelection =  (RankingAndCrowdingSelection)algorithm.getOperator("newGenerationSelection") ;
    Problem problem2 = (Problem) rankingAndCrowdingSelection.getParameter("problem");
    BitFlipMutation mutation = (BitFlipMutation)algorithm.getOperator("cataclysmicMutation") ;
    double pm = (Double)mutation.getParameter("probability") ;

    assertEquals("MOCHC_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("MOCHC_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    assertEquals("MOCHC_SettingsTest", 0.25, ((Double)algorithm.getInputParameter("initialConvergenceCount")).doubleValue(), epsilon);
    assertEquals("MOCHC_SettingsTest", 3, ((Integer)algorithm.getInputParameter("convergenceValue")).intValue());
    assertEquals("MOCHC_SettingsTest", 0.05, ((Double)algorithm.getInputParameter("preservedPopulation")).doubleValue(), epsilon);

    assertEquals("MOCHC_SettingsTest", 1.0, pc, epsilon);
    assertTrue("MOCHC_SettingsTest", problem.getName().equals("ZDT5"));

    assertEquals("MOCHC_SettingsTest", 0.35, pm, epsilon);
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings mochcSettings = new MOCHC_Settings("ZDT5");
    Algorithm algorithm = mochcSettings.configure(configuration_) ;
    Problem problem = new ZDT5("Binary") ;

    HUXCrossover crossover = (HUXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    RankingAndCrowdingSelection rankingAndCrowdingSelection =  (RankingAndCrowdingSelection)algorithm.getOperator("newGenerationSelection") ;
    Problem problem2 = (Problem) rankingAndCrowdingSelection.getParameter("problem");
    BitFlipMutation mutation = (BitFlipMutation)algorithm.getOperator("cataclysmicMutation") ;
    double pm = (Double)mutation.getParameter("probability") ;

    assertEquals("MOCHC_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("MOCHC_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    assertEquals("MOCHC_SettingsTest", 0.25, ((Double)algorithm.getInputParameter("initialConvergenceCount")).doubleValue(), epsilon);
    assertEquals("MOCHC_SettingsTest", 3, ((Integer)algorithm.getInputParameter("convergenceValue")).intValue());
    assertEquals("MOCHC_SettingsTest", 0.05, ((Double)algorithm.getInputParameter("preservedPopulation")).doubleValue(), epsilon);

    assertEquals("MOCHC_SettingsTest", 1.0, pc, epsilon);
    assertTrue("MOCHC_SettingsTest", problem.getName().equals("ZDT5"));

    assertEquals("MOCHC_SettingsTest", 0.35, pm, epsilon);
  }
}
