package jmetal.test.experiments.settings;

import experiments.settings.jMetalHome;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.CellDE_Settings;
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

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 13/06/13
 * Time: 22:07
 * To change this template use File | Settings | File Templates.
 */
public class CellDE_SettingsTest  {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(jMetalHome.jMetalHomeConfDir+"/CellDE.conf"));
    configuration_.load(isr);
  }

  @Test
  public void testSettings() throws JMException {
    double epsilon = 0.000000000000001 ;
    Settings cellDESettings = new CellDE_Settings("Fonseca");
    Algorithm algorithm = cellDESettings.configure() ;
    Problem problem = new Fonseca("Real") ;

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    assertEquals("CellDE_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("CellDE_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    assertEquals("CellDE_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());
    assertEquals("CellDE_SettingsTest", 20, ((Integer)algorithm.getInputParameter("archiveFeedBack")).intValue());

    assertEquals("CellDE_SettingsTest", 0.5, CR, epsilon);
    assertEquals("CellDE_SettingsTest", 0.5, F, epsilon);
  }

  @Test
  public void testSettings2() throws JMException {
    double epsilon = 0.000000000000001 ;
    Settings cellDESettings = new CellDE_Settings("Fonseca");
    Algorithm algorithm = cellDESettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real") ;

    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    assertEquals("CellDE_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("CellDE_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    assertEquals("CellDE_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());
    assertEquals("CellDE_SettingsTest", 20, ((Integer)algorithm.getInputParameter("archiveFeedBack")).intValue());

    assertEquals("CellDE_SettingsTest", 0.5, CR, epsilon);
    assertEquals("CellDE_SettingsTest", 0.5, F, epsilon);
  }
}
