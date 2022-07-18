package org.uma.jmetal.algorithm.multiobjective.omopso;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.mutation.impl.NonUniformMutation;
import org.uma.jmetal.operator.mutation.impl.UniformMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Integration tests for algorithm OMOPSO
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class OMOPSOIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    DoubleProblem problem = new ZDT1();

    var mutationProbability = 1.0 / problem.getNumberOfVariables();

    algorithm =
        new OMOPSOBuilder(problem, new SequentialSolutionListEvaluator<DoubleSolution>())
            .setMaxIterations(250)
            .setSwarmSize(100)
            .setEta(0.0075)
            .setUniformMutation(new UniformMutation(mutationProbability, 0.5))
            .setNonUniformMutation(new NonUniformMutation(mutationProbability, 0.5, 250))
            .build();

    algorithm.run();

    var population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT1, and OMOPSO, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(population.size() >= 98);
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    DoubleProblem problem = new ZDT1();

    var mutationProbability = 1.0 / problem.getNumberOfVariables();

    algorithm =
        new OMOPSOBuilder(problem, new SequentialSolutionListEvaluator<DoubleSolution>())
            .setMaxIterations(250)
            .setSwarmSize(100)
            .setEta(0.0075)
            .setUniformMutation(new UniformMutation(mutationProbability, 0.5))
            .setNonUniformMutation(new NonUniformMutation(mutationProbability, 0.5, 250))
            .build();

    algorithm.run();

    var population = algorithm.getResult();

    QualityIndicator hypervolume =
            new PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ","));

    // Rationale: the default problem is ZDT1, and OMOPSO, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.64

    var hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.64);
  }
}
