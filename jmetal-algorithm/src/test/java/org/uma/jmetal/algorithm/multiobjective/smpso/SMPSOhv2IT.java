package org.uma.jmetal.algorithm.multiobjective.smpso;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.WFGHypervolume;

public class SMPSOhv2IT {
  private Algorithm<List<DoubleSolution>> algorithm;
  private BoundedArchive<DoubleSolution> archive;

  @Before
  public void setup() {
    archive = new HypervolumeArchive<DoubleSolution>(100, new WFGHypervolume<DoubleSolution>());
  }

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    DoubleProblem problem = new ZDT4();

    algorithm = new SMPSOBuilder(problem, archive).build();

    algorithm.run();

    var population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT4, and SMPSO, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(population.size() >= 98);
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    DoubleProblem problem = new ZDT4();

    algorithm = new SMPSOBuilder(problem, new CrowdingDistanceArchive<>(100)).build();
    algorithm.run();

    var population = algorithm.getResult();

    QualityIndicator hypervolume =
            new org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT4.csv", ","));

    // Rationale: the default problem is ZDT4, and SMPSO, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.64

    var hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.64);
  }
}
