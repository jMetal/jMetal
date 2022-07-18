package org.uma.jmetal.algorithm.multiobjective.smpso;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.ConstrEx;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class SMPSOIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    DoubleProblem problem = new ZDT4();

    algorithm = new SMPSOBuilder(problem, new CrowdingDistanceArchive<DoubleSolution>(100)).build();

    JMetalRandom.getInstance().setSeed(1);

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
            new PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT4.csv", ","));

    // Rationale: the default problem is ZDT4, and SMPSO, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.64

    var hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.64);
  }

  @Test
  public void shouldTheAlgorithmReturnAGoodQualityFrontWhenSolvingAConstrainedProblem()
      throws Exception {
    var problem = new ConstrEx();

    algorithm = new SMPSOBuilder(problem, new CrowdingDistanceArchive<DoubleSolution>(100)).build();

    algorithm.run();
    var population = algorithm.getResult() ;

    var referenceFrontFileName = "../resources/referenceFrontsCSV/ConstrEx.csv" ;

    var referenceFront = VectorUtils.readVectors(referenceFrontFileName, ",") ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    // Rationale: the default problem is ConstrEx, and PESA-II, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.7

    var normalizedFront =
            NormalizeUtils.normalize(
                    SolutionListUtils.getMatrixWithObjectiveValues(population),
                    NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                    NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var hv = hypervolume.compute(normalizedFront);

    assertTrue(population.size() >= 98);
    assertTrue(hv > 0.77);
  }
}
