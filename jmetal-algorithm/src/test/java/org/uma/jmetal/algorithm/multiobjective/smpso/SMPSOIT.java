package org.uma.jmetal.algorithm.multiobjective.smpso;

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
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.impl.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.uma.jmetal.util.AbstractAlgorithmRunner.printFinalSolutionSet;

public class SMPSOIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    DoubleProblem problem = new ZDT4();

    algorithm = new SMPSOBuilder(problem, new CrowdingDistanceArchive<DoubleSolution>(100)).build();

    JMetalRandom.getInstance().setSeed(1);

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

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

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator hypervolume =
            new PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT4.csv", ","));

    // Rationale: the default problem is ZDT4, and SMPSO, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.64

    double hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.64);
  }

  @Test
  public void shouldTheAlgorithmReturnAGoodQualityFrontWhenSolvingAConstrainedProblem()
      throws Exception {
    ConstrEx problem = new ConstrEx();

    algorithm = new SMPSOBuilder(problem, new CrowdingDistanceArchive<DoubleSolution>(100)).build();

    algorithm.run();
    List<DoubleSolution> population = algorithm.getResult() ;

    String referenceFrontFileName = "../resources/referenceFrontsCSV/ConstrEx.csv" ;

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFileName, ",") ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    // Rationale: the default problem is ConstrEx, and PESA-II, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.7

    double[][] normalizedFront =
            NormalizeUtils.normalize(
                    SolutionListUtils.getMatrixWithObjectiveValues(population),
                    NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                    NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    double hv = hypervolume.compute(normalizedFront);

    assertTrue(population.size() >= 98);
    assertTrue(hv > 0.77);
  }
}
