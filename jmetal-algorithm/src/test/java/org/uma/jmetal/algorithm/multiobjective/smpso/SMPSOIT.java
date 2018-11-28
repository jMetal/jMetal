package org.uma.jmetal.algorithm.multiobjective.smpso;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.ConstrEx;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class SMPSOIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() throws Exception {
    DoubleProblem problem = new ZDT4() ;

    algorithm = new SMPSOBuilder(problem, new CrowdingDistanceArchive<DoubleSolution>(100)).build() ;

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT4, and SMPSO, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(population.size() >= 98) ;
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    DoubleProblem problem = new ZDT4() ;

    algorithm = new SMPSOBuilder(problem, new CrowdingDistanceArchive<DoubleSolution>(100)).build() ;
    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator<List<DoubleSolution>, Double> hypervolume = new PISAHypervolume<>("/referenceFronts/ZDT4.pf") ;

    // Rationale: the default problem is ZDT4, and SMPSO, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.64

    double hv = (Double)hypervolume.evaluate(population) ;

    assertTrue(hv > 0.64) ;
  }

  @Test
  public void shouldTheAlgorithmReturnAGoodQualityFrontWhenSolvingAConstrainedProblem() throws Exception {
    ConstrEx problem = new ConstrEx() ;

    algorithm = new SMPSOBuilder(problem, new CrowdingDistanceArchive<DoubleSolution>(100)).build() ;

    new AlgorithmRunner.Executor(algorithm).execute() ;

    List<DoubleSolution> population = algorithm.getResult() ;

    String referenceFrontFileName = "/referenceFronts/ConstrEx.pf" ;

    Front referenceFront = new ArrayFront(referenceFrontFileName);
    FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront) ;

    Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront) ;
    Front normalizedFront = frontNormalizer.normalize(new ArrayFront(population)) ;
    List<PointSolution> normalizedPopulation = FrontUtils
        .convertFrontToSolutionList(normalizedFront) ;

    double hv = new PISAHypervolume<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation) ;

    assertTrue(population.size() >= 98) ;
    assertTrue(hv > 0.77) ;
  }
}
