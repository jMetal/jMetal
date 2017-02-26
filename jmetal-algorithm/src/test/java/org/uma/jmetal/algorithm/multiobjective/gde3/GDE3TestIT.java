package org.uma.jmetal.algorithm.multiobjective.gde3;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by ajnebro on 3/11/15.
 */
public class GDE3TestIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() throws Exception {
    DoubleProblem problem = new ZDT1() ;
    JMetalRandom.getInstance().setSeed(1446505566148L);
    algorithm = new GDE3Builder(problem)
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .build() ;

    new AlgorithmRunner.Executor(algorithm).execute();

    List<DoubleSolution> population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT4, and GDE3, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(population.size() >= 99) ;
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    DoubleProblem problem = new ZDT1() ;

    JMetalRandom.getInstance().setSeed(1446505566148L);
    algorithm = new GDE3Builder(problem)
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .build() ;

    new AlgorithmRunner.Executor(algorithm).execute();

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator<List<DoubleSolution>, Double> hypervolume = new PISAHypervolume<>("/referenceFronts/ZDT4.pf") ;

    // Rationale: the default problem is ZDT1, and GDE3, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.66

    double hv = (Double)hypervolume.evaluate(population) ;

    assertTrue(hv > 0.66) ;
  }
}