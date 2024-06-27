package org.uma.jmetal.algorithm.multiobjective.dmopso;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;

/**
 * Integration tests for algorithm DMOPSO
 *
 * @author Antonio J. Nebro 
 */
public class DMOPSOIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    DoubleProblem problem = new ZDT1();

    algorithm =
        new DMOPSO(
            problem,
            100,
            250,
            0.0,
            0.1,
            0.0,
            1.0,
            1.5,
            2.5,
            1.5,
            2.5,
            0.1,
            0.4,
            -1.0,
            -1.0,
            DMOPSO.FunctionType.TCHE,
            "",
            2);

    algorithm.run();

    List<DoubleSolution> population = algorithm.result();

    /*
    Rationale: the default problem is ZDT1, and dMOPSO, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(population.size() >= 98);
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    DoubleProblem problem = new ZDT1();

    algorithm =
        new DMOPSO(
            problem,
            100,
            250,
            0.0,
            0.1,
            0.0,
            1.0,
            1.5,
            2.5,
            1.5,
            2.5,
            0.1,
            0.4,
            -1.0,
            -1.0,
            DMOPSO.FunctionType.TCHE,
            "",
            2);

    algorithm.run();

    List<DoubleSolution> population = algorithm.result();

    QualityIndicator hypervolume =
            new PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ","));

    // Rationale: the default problem is ZDT1, and AbYSS, configured with standard settings,
    // should return find a front with a hypervolume value higher than 0.22

    double hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.64);
  }
}
