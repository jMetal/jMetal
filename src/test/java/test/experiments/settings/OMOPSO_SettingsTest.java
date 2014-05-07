package test.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.OMOPSO_Settings;
import jmetal.operators.mutation.NonUniformMutation;
import jmetal.operators.mutation.UniformMutation;
import jmetal.problems.Fonseca;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 26/06/13
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */
public class OMOPSO_SettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("OMOPSO.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings omopsoSettings = new OMOPSO_Settings("Fonseca");
    Algorithm algorithm = omopsoSettings.configure() ;
    Problem problem = new Fonseca("Real");

    UniformMutation uniformMutation = (UniformMutation)algorithm.getOperator("uniformMutation")  ;
    NonUniformMutation nonUniformMutation = (NonUniformMutation)algorithm.getOperator("nonUniformMutation")  ;

    Assert.assertEquals("OMOPSO_SettingsTest", 100, ((Integer)algorithm.getInputParameter("swarmSize")).intValue());
    Assert.assertEquals("OMOPSO_SettingsTest", 250, ((Integer)algorithm.getInputParameter("maxIterations")).intValue());
    Assert.assertEquals("OMOPSO_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());

    Assert.assertEquals("OMOPSO_SettingsTest", 1.0/problem.getNumberOfVariables(), (Double)uniformMutation.getParameter("probability") , epsilon);
    Assert.assertEquals("OMOPSO_SettingsTest", 0.5, (Double)uniformMutation.getParameter("perturbation") , epsilon);
    Assert.assertEquals("OMOPSO_SettingsTest", 250, ((Integer)nonUniformMutation.getParameter("maxIterations")).intValue());
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings omopsoSettings = new OMOPSO_Settings("Fonseca");
    Algorithm algorithm = omopsoSettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real");

    UniformMutation uniformMutation = (UniformMutation)algorithm.getOperator("uniformMutation")  ;
    NonUniformMutation nonUniformMutation = (NonUniformMutation)algorithm.getOperator("nonUniformMutation")  ;

    Assert.assertEquals("OMOPSO_SettingsTest", 100, ((Integer)algorithm.getInputParameter("swarmSize")).intValue());
    Assert.assertEquals("OMOPSO_SettingsTest", 250, ((Integer)algorithm.getInputParameter("maxIterations")).intValue());
    Assert.assertEquals("OMOPSO_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());

    Assert.assertEquals("OMOPSO_SettingsTest", 1.0/problem.getNumberOfVariables(), (Double)uniformMutation.getParameter("probability") , epsilon);
    Assert.assertEquals("OMOPSO_SettingsTest", 0.5, (Double)uniformMutation.getParameter("perturbation") , epsilon);
    Assert.assertEquals("OMOPSO_SettingsTest", 250, ((Integer)nonUniformMutation.getParameter("maxIterations")).intValue());
  }
}
