package org.uma.jmetal.auto.algorithm.nsgaii;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmDefaultOutputData;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;
import picocli.CommandLine;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class to configure NSGA-II.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AutoNSGAII {

  public static void main(String[] args) throws FileNotFoundException {

    String[] arguments = {
      "--problemName",
      "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2",
      "--referenceFront",
      "DTLZ2.2D.pf",
      "--crossover",
      "BLX_ALPHA",
      "--crossoverProbability",
      "0.9983",
      "--crossoverRepairStrategy",
      "round",
      "--blxAlphaCrossoverAlphaValue",
      "0.7648",
      "--mutation",
      "polynomial",
      "--mutationProbability",
      "0.0078",
      "--mutationRepairStrategy",
      "round",
      "--uniformMutationPerturbation",
      "0.7294",
      "--selection",
      "tournament",
      "--selectionTournamentSize",
      "9",
      "--offspringPopulationSize",
      "1",
      "--variation",
      "rankingAndCrowding",
      "--createInitialSolutions",
      "random"
    };

    AutoNSGAIIConfigurator configurator =
        CommandLine.populateCommand(new AutoNSGAIIConfigurator(), arguments);

    EvolutionaryAlgorithm<DoubleSolution> autoNSGAII = configurator.configureAndGetAlgorithm();
    autoNSGAII.run();

    Front referenceFront = new ArrayFront(configurator.getReferenceParetoFront());
    FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
    Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront);
    Front normalizedFront = frontNormalizer.normalize(new ArrayFront(autoNSGAII.getResult()));
    List<PointSolution> normalizedPopulation =
        FrontUtils.convertFrontToSolutionList(normalizedFront);

    double referenceFrontHV =
        new PISAHypervolume<PointSolution>(normalizedReferenceFront)
            .evaluate(FrontUtils.convertFrontToSolutionList(normalizedReferenceFront));
    double obtainedFrontHV =
        new PISAHypervolume<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation);
    // System.out.println(obtainedFrontHV);
    System.out.println((referenceFrontHV - obtainedFrontHV) / referenceFrontHV);

    AlgorithmDefaultOutputData.generateMultiObjectiveAlgorithmOutputData(
    autoNSGAII.getResult(), autoNSGAII.getTotalComputingTime());
  }
}
