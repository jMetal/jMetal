package org.uma.jmetal.algorithm.multiobjective.fame;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.SpatialSpreadDeviationSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class FAMEIT {

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() {
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

    problem = new ZDT1() ;
    selection = new SpatialSpreadDeviationSelection<DoubleSolution>(5);

    int populationSize=25 ;
    int archiveSize=100 ;
    int maxEvaluations=25000;

    algorithm = new FAME(problem,
            populationSize,
            archiveSize,
            maxEvaluations,
            selection,
            new SequentialSolutionListEvaluator()) ;

    algorithm.run();

    /*
    Rationale: the default problem is ZDT1, and FAME, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(algorithm.getResult().size() >= 99) ;
    JMetalRandom.getInstance().setSeed(System.currentTimeMillis());
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

    problem = new ZDT1();
    selection = new SpatialSpreadDeviationSelection<DoubleSolution>(5);

    int populationSize = 25;
    int archiveSize = 100;
    int maxEvaluations = 25000;

    algorithm =
        new FAME(
            problem,
            populationSize,
            archiveSize,
            maxEvaluations,
            selection,
            new SequentialSolutionListEvaluator());

    algorithm.run();

    QualityIndicator<List<DoubleSolution>, Double> hypervolume =
        new PISAHypervolume<>("/referenceFronts/ZDT1.pf");

    // Rationale: the default problem is ZDT1, and GDE3, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.66

    double hv = (Double) hypervolume.evaluate(algorithm.getResult());

    assertTrue(hv > 0.66);
    JMetalRandom.getInstance().setSeed(System.currentTimeMillis());
  }
}
