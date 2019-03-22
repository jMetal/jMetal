package org.uma.jmetal.auto.algorithm.nsgaii;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.util.observer.Observer;
import org.uma.jmetal.auto.util.observer.impl.ExternalArchiveObserver;
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
import java.util.Collection;
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
        "org.uma.jmetal.problem.multiobjective.wfg.WFG6",
        "--referenceFront",
        "WFG6.2D.pf",
        "--algorithmResult", "externalArchive",
        "--populationSizeWithArchive", "20",
        //"--algorithmResult", "population",
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

    System.out.println("Evals: " + autoNSGAII.getEvaluations()) ;

    if (autoNSGAII.getEvaluation().getObservable().numberOfRegisteredObservers() == 1) {
      Collection<Observer<DoubleSolution>> observers = autoNSGAII.getEvaluation().getObservable().getObservers() ;
      ExternalArchiveObserver<DoubleSolution> externalArchiveObserver = (ExternalArchiveObserver<DoubleSolution>) observers.toArray()[0];
      autoNSGAII.setSolutions(externalArchiveObserver.getArchive().getSolutionList());
    }

    Front referenceFront = new ArrayFront(configurator.getReferenceParetoFront());
    FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
    Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront);
    Front normalizedFront = frontNormalizer.normalize(new ArrayFront(autoNSGAII.getResult()));
    List<PointSolution> normalizedPopulation =
        FrontUtils.convertFrontToSolutionList(normalizedFront);

    double referenceFrontHV =
        new PISAHypervolume<PointSolution>(normalizedReferenceFront)
            .evaluate(FrontUtils.convertFrontToSolutionList(normalizedReferenceFront));
    System.out.println("Front size: " + normalizedPopulation.size()) ;
    double obtainedFrontHV =
        new PISAHypervolume<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation);
    // System.out.println(obtainedFrontHV);
    System.out.println((referenceFrontHV - obtainedFrontHV) / referenceFrontHV);

    AlgorithmDefaultOutputData.generateMultiObjectiveAlgorithmOutputData(
    autoNSGAII.getResult(), autoNSGAII.getTotalComputingTime());
  }
}
