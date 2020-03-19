package org.uma.jmetal.algorithm.multiobjective.moead;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.problem.multiobjective.lz09.LZ09F2;
import org.uma.jmetal.problem.multiobjective.lz09.LZ09F6;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.aggregativefunction.impl.Tschebyscheff;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class MOEADDEIT {

  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() {
    LZ09F2 problem = new LZ09F2();

    int populationSize = 300;

    double cr = 1.0;
    double f = 0.5;

    double neighborhoodSelectionProbability = 0.9;
    int neighborhoodSize = 20;
    int maximumNumberOfReplacedSolutions = 2;
    int maximumNumberOfFunctionEvaluations = 150000;

    AggregativeFunction aggregativeFunction = new Tschebyscheff();

    algorithm =
        new MOEADDE(
            problem,
            populationSize,
            cr,
            f,
            aggregativeFunction,
            neighborhoodSelectionProbability,
            maximumNumberOfReplacedSolutions,
            neighborhoodSize,
            "../resources/weightVectorFiles/moead",
            new TerminationByEvaluations(maximumNumberOfFunctionEvaluations));

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    assertTrue(population.size() == 300);
  }

  @Test
  public void shouldTheHypervolumeHaveAMinimumValueWhenSolvingTheLZ09F2Instance()
      throws FileNotFoundException {
    LZ09F2 problem = new LZ09F2();

    int populationSize = 300;

    double cr = 1.0;
    double f = 0.5;

    double neighborhoodSelectionProbability = 0.9;
    int neighborhoodSize = 20;
    int maximumNumberOfReplacedSolutions = 2;
    int maximumNumberOfFunctionEvaluations = 150000;

    AggregativeFunction aggregativeFunction = new Tschebyscheff();

    algorithm =
        new MOEADDE(
            problem,
            populationSize,
            cr,
            f,
            aggregativeFunction,
            neighborhoodSelectionProbability,
            maximumNumberOfReplacedSolutions,
            neighborhoodSize,
            "../resources/weightVectorFiles/moead",
            new TerminationByEvaluations(maximumNumberOfFunctionEvaluations));

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator<List<DoubleSolution>, Double> hypervolume =
            new PISAHypervolume<>("../resources/referenceFrontsCSV/LZ09_F2.pf");

    // Rationale: the default problem is LZ09F2", and MOEA/D-DE, configured with standard settings,
    // should return find a front with a hypervolume value higher than 0.95
    double hv = hypervolume.evaluate(population);

    assertTrue(hv > 0.65);
  }

  @Test
  public void shouldTheHypervolumeHaveAMinimumValueWhenSolvingTheLZ09F6Instance() throws Exception {
    LZ09F6 problem = new LZ09F6();

    int populationSize = 300;

    double cr = 1.0;
    double f = 0.5;

    double neighborhoodSelectionProbability = 0.9;
    int neighborhoodSize = 20;
    int maximumNumberOfReplacedSolutions = 2;
    int maximumNumberOfFunctionEvaluations = 150000;

    AggregativeFunction aggregativeFunction = new Tschebyscheff();

    algorithm =
        new MOEADDE(
            problem,
            populationSize,
            cr,
            f,
            aggregativeFunction,
            neighborhoodSelectionProbability,
            maximumNumberOfReplacedSolutions,
            neighborhoodSize,
            "../resources/weightVectorFiles/moead",
            new TerminationByEvaluations(maximumNumberOfFunctionEvaluations));

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator<List<DoubleSolution>, Double> hypervolume =
        new PISAHypervolume<>("../resources/referenceFrontsCSV/LZ09_F6.pf");

    // Rationale: the default problem is LZ09F6", and MOEA/D, configured with standard settings,
    // should
    // return find a front with a hypervolume value higher than 0.35
    double hv = hypervolume.evaluate(population);

    assertTrue(hv > 0.35);

    JMetalRandom.getInstance().setSeed(System.currentTimeMillis());
  }
}
