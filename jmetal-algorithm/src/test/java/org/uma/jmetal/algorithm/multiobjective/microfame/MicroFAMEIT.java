package org.uma.jmetal.algorithm.multiobjective.microfame;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.microfame.util.HVTournamentSelection;
import org.uma.jmetal.operator.crossover.impl.NullCrossover;
import org.uma.jmetal.operator.mutation.impl.NullMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class MicroFAMEIT {

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() {

    var archiveSize = 100;
    var evaluations = 25000;

      Problem<DoubleSolution> problem = new ZDT1();

    var crossover = new NullCrossover<DoubleSolution>();
    var mutation = new NullMutation<DoubleSolution>();
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new HVTournamentSelection(5);
      Algorithm<List<DoubleSolution>> algorithm = new MicroFAME<>(problem, evaluations, archiveSize, crossover, mutation, selection);

    algorithm.run();

    /*
    Rationale: the default problem is ZDT1, and MicroFAME, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(algorithm.getResult().size() >= 98);
    JMetalRandom.getInstance().setSeed(System.currentTimeMillis());
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {

    var archiveSize = 100;
    var evaluations = 25000;

      Problem<DoubleSolution> problem = new ZDT1();

    var crossover = new NullCrossover<DoubleSolution>();
    var mutation = new NullMutation<DoubleSolution>();
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new HVTournamentSelection(5);
      Algorithm<List<DoubleSolution>> algorithm = new MicroFAME<>(problem, evaluations, archiveSize, crossover, mutation, selection);

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
