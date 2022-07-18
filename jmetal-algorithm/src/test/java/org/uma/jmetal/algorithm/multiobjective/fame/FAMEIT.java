package org.uma.jmetal.algorithm.multiobjective.fame;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.SpatialSpreadDeviationSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class FAMEIT {

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() {

      Problem<DoubleSolution> problem = new ZDT1();
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new SpatialSpreadDeviationSelection<DoubleSolution>(5);

    var populationSize = 25;
    var archiveSize = 100;
    var maxEvaluations = 25000;

      Algorithm<List<DoubleSolution>> algorithm = new FAME<>(
              problem,
              populationSize,
              archiveSize,
              maxEvaluations,
              selection,
              new SequentialSolutionListEvaluator<>());

    algorithm.run();

    /*
    Rationale: the default problem is ZDT1, and FAME, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(algorithm.getResult().size() >= 99);
    JMetalRandom.getInstance().setSeed(System.currentTimeMillis());
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {

      Problem<DoubleSolution> problem = new ZDT1();
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new SpatialSpreadDeviationSelection<DoubleSolution>(5);

    var populationSize = 25;
    var archiveSize = 100;
    var maxEvaluations = 25000;

      Algorithm<List<DoubleSolution>> algorithm = new FAME<>(
              problem,
              populationSize,
              archiveSize,
              maxEvaluations,
              selection,
              new SequentialSolutionListEvaluator<>());

    algorithm.run();

    QualityIndicator hypervolume =
            new PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ","));

    // Rationale: the default problem is ZDT1, and AbYSS, configured with standard settings,
    // should return find a front with a hypervolume value higher than 0.22

    var hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(algorithm.getResult()));

    assertTrue(hv > 0.65);

    JMetalRandom.getInstance().setSeed(System.currentTimeMillis());
  }
}
