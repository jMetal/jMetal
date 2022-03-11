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
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

    int archiveSize = 100;
    int evaluations = 25000;

    problem = new ZDT1();

    var crossover = new NullCrossover<DoubleSolution>();
    var mutation = new NullMutation<DoubleSolution>();
    selection = new HVTournamentSelection(5);
    algorithm = new MicroFAME<>(problem, evaluations, archiveSize, crossover, mutation, selection);

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
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

    int archiveSize = 100;
    int evaluations = 25000;

    problem = new ZDT1();

    var crossover = new NullCrossover<DoubleSolution>();
    var mutation = new NullMutation<DoubleSolution>();
    selection = new HVTournamentSelection(5);
    algorithm = new MicroFAME<>(problem, evaluations, archiveSize, crossover, mutation, selection);

    algorithm.run();

    QualityIndicator hypervolume =
            new PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ","));

    // Rationale: the default problem is ZDT1, and AbYSS, configured with standard settings,
    // should return find a front with a hypervolume value higher than 0.22

    double hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(algorithm.getResult()));

    assertTrue(hv > 0.65);

    JMetalRandom.getInstance().setSeed(System.currentTimeMillis());
  }
}
