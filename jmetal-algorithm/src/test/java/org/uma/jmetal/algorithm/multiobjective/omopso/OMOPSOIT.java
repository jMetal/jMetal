package org.uma.jmetal.algorithm.multiobjective.omopso;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.impl.mutation.NonUniformMutation;
import org.uma.jmetal.operator.impl.mutation.UniformMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Integration tests for algorithm OMOPSO
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class OMOPSOIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() throws Exception {
    DoubleProblem problem = new ZDT1() ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;

    algorithm = new OMOPSOBuilder(problem, new SequentialSolutionListEvaluator<DoubleSolution>())
        .setMaxIterations(250)
        .setSwarmSize(100)
        .setUniformMutation(new UniformMutation(mutationProbability, 0.5))
        .setNonUniformMutation(new NonUniformMutation(mutationProbability, 0.5, 250))
        .build();

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT1, and OMOPSO, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(population.size() >= 98) ;
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    DoubleProblem problem = new ZDT1() ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;

    algorithm = new OMOPSOBuilder(problem, new SequentialSolutionListEvaluator<DoubleSolution>())
        .setMaxIterations(250)
        .setSwarmSize(100)
        .setUniformMutation(new UniformMutation(mutationProbability, 0.5))
        .setNonUniformMutation(new NonUniformMutation(mutationProbability, 0.5, 250))
        .build();

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator<List<DoubleSolution>, Double> hypervolume = new PISAHypervolume<>("/referenceFronts/ZDT4.pf") ;

    // Rationale: the default problem is ZDT1, and OMOPSO, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.64

    double hv = (Double)hypervolume.evaluate(population) ;

    assertTrue(hv > 0.64) ;
  }
}
