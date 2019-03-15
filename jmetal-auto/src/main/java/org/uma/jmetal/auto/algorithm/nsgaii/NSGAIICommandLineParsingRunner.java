package org.uma.jmetal.auto.algorithm.nsgaii;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
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
public class NSGAIICommandLineParsingRunner {

  public static void main(String[] args) throws FileNotFoundException {
/*
    String[] arguments = {
      "--problemName",
      "org.uma.jmetal.problem.multiobjective.wfg.WFG1",
      "--referenceFront",
      "WFG1.2D.pf",
      "--createInitialSolutions",
      "random",
      "--offspringPopulationSize",
      "4",
      "--selection",
      "random",
      "--selectionTournamentSize",
      "2",
      "--crossover",
      "BLX_ALPHA",
      "--crossoverProbability",
      "0.6825",
      "--mutation",
      "uniform",
      "--mutationProbability",
      "0.4079",
      "--variation",
      "rankingAndCrowding",
      "--mutationRepairStrategy",
      "bounds",
      "--crossoverRepairStrategy",
      "bounds",
        "--blxAlphaCrossoverAlphaValue", "0.8082", "--uniformMutationPerturbation", "0.7294"
    };
*/
    AutoNSGAIIConfigurator configurator = CommandLine.populateCommand(new AutoNSGAIIConfigurator(), args);

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
    //System.out.println(obtainedFrontHV);
    System.out.println((referenceFrontHV - obtainedFrontHV) / referenceFrontHV);

    // AlgorithmDefaultOutputData.generateMultiObjectiveAlgorithmOutputData(
    //    autoNSGAII.getResult(), autoNSGAII.getTotalComputingTime());
  }
}
