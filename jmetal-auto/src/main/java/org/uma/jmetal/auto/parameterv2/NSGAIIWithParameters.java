package org.uma.jmetal.auto.parameterv2;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.component.initialsolutionscreation.InitialSolutionsCreation;
import org.uma.jmetal.auto.component.variation.Variation;
import org.uma.jmetal.auto.parameterv2.param.Parameter;
import org.uma.jmetal.auto.parameterv2.param.RealParameter;
import org.uma.jmetal.auto.parameterv2.param.catalogue.*;
import org.uma.jmetal.auto.parameterv2.param.irace.NSGAIIiraceParameterFile;
import org.uma.jmetal.auto.util.observer.impl.ExternalArchiveObserver;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;

import java.util.*;

public class NSGAIIWithParameters {
  public List<Parameter<?>> configurableParameterList = new ArrayList<>();
  public Map<String, Parameter<?>> parameterMap = new HashMap<>();

  private ProblemName<DoubleSolution> problemName ;
  private ReferenceFrontFilename referenceFrontFilename ;
  private AlgorithmResult algorithmResult;
  private PopulationSize populationSize;
  private PopulationSizeWithArchive populationSizeWithArchive;
  private OffspringPopulationSize offspringPopulationSize;
  private CreateInitialSolutions createInitialSolutions;
  private Selection selection ;
  private VariationParameter variationParameter ;

  public void parseParameters(String[] args) {
    problemName = new ProblemName<>(args) ;
    problemName.parse() ;

    referenceFrontFilename = new ReferenceFrontFilename(args) ;
    referenceFrontFilename.parse() ;

    algorithmResult = new AlgorithmResult(args, Arrays.asList("externalArchive", "population"));
    populationSize = new PopulationSize(args);
    populationSizeWithArchive =
        new PopulationSizeWithArchive(args, Arrays.asList(10, 20, 50, 100, 200));
    algorithmResult.addSpecificParameter("population", populationSize);
    algorithmResult.addSpecificParameter("externalArchive", populationSizeWithArchive);

    offspringPopulationSize = new OffspringPopulationSize(args, Arrays.asList(1, 10, 50, 100));

    createInitialSolutions =
        new CreateInitialSolutions(
            args, Arrays.asList("random", "latinHypercubeSampling", "scatterSearch"));

    selection = new Selection(args, Arrays.asList("tournament", "random"));
    SelectionTournamentSize selectionTournamentSize = new SelectionTournamentSize(args, 2, 10);
    selection.addSpecificParameter("tournament", selectionTournamentSize);

    CrossoverParameter crossover = new CrossoverParameter(args, Arrays.asList("SBX", "BLX_ALPHA"));
    Probability crossoverProbability = new Probability(args, "crossoverProbability");
    crossover.addGlobalParameter(crossoverProbability);
    RepairDoubleSolutionStrategyParameter crossoverRepairStrategy =
        new RepairDoubleSolutionStrategyParameter(
            args, "crossoverRepairStrategy", Arrays.asList("random", "round", "bounds"));
    crossover.addGlobalParameter(crossoverRepairStrategy);

    RealParameter distributionIndex =
        new DistributionIndex(args, "sbxDistributionIndex", 5.0, 400.0);
    crossover.addSpecificParameter("SBX", distributionIndex);

    RealValueInRange alpha = new RealValueInRange(args, "blxAlphaCrossoverAlphaValue", 0.0, 1.0);
    crossover.addSpecificParameter("BLX_ALPHA", alpha);

    MutationParameter mutation = new MutationParameter(args, Arrays.asList("uniform", "polynomial"));
    Probability mutationProbability = new Probability(args, "mutationProbability");
    mutation.addGlobalParameter(mutationProbability);
    RepairDoubleSolutionStrategyParameter mutationRepairStrategy =
        new RepairDoubleSolutionStrategyParameter(
            args, "mutationRepairStrategy", Arrays.asList("random", "round", "bounds"));
    mutation.addGlobalParameter(mutationRepairStrategy);

    RealParameter distributionIndexForMutation =
        new DistributionIndex(args, "polynomialMutationDistributionIndex", 5.0, 400.0);
    mutation.addSpecificParameter("polynomial", distributionIndexForMutation);

    RealParameter uniformMutationPerturbation =
        new RealValueInRange(args, "uniformMutationPerturbation", 0.0, 1.0);
    mutation.addSpecificParameter("uniform", uniformMutationPerturbation);

    variationParameter = new VariationParameter(args, Arrays.asList("crossoverAndMutationVariation"));
    variationParameter.addGlobalParameter(crossover);
    variationParameter.addGlobalParameter(mutation);

    configurableParameterList.add(algorithmResult);
    configurableParameterList.add(offspringPopulationSize);
    configurableParameterList.add(createInitialSolutions);
    configurableParameterList.add(variationParameter);
    configurableParameterList.add(selection);

    for (Parameter<?> parameter : configurableParameterList) {
      parameter.parse().check();
    }

    parameterMap.put(algorithmResult.getName(), algorithmResult);
    parameterMap.put(populationSize.getName(), populationSize);
    parameterMap.put(populationSizeWithArchive.getName(), populationSizeWithArchive);
  }

  /**
   * Creates an instance of NSGA-II from the parsed parameters
   * @return
   */
  EvolutionaryAlgorithm<DoubleSolution> create() {
    DoubleProblem problem = (DoubleProblem)problemName.getProblem() ;

    ExternalArchiveObserver<DoubleSolution> boundedArchiveObserver = null;

    if (algorithmResult.getValue() == "externalArchive") {
      boundedArchiveObserver =
          new ExternalArchiveObserver<>(new CrowdingDistanceArchive<>(populationSize.getValue()));
      populationSize.setValue(populationSizeWithArchive.getValue());
    }

    InitialSolutionsCreation<DoubleSolution> initialSolutionsCreation  = createInitialSolutions.getParameter(problem, populationSize.getValue()) ;

    Variation<DoubleSolution> variation = (Variation<DoubleSolution>)variationParameter.getParameter(offspringPopulationSize.getValue()) ;

    return null;
  }

  public static void print(List<Parameter<?>> parameterList) {
    parameterList.forEach(item -> System.out.println(item));
  }

  public static void main(String[] args) {
    String[] parameters =
        ("--populationSize 100 "
                + "--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
                + "--referenceFrontFileName ZDT1.pf "
                + "--maximumNumberOfIterations 25000 "
                + "--algorithmResult population "
                + "--populationSize 100 "
                + "--offspringPopulationSize 100 "
                + "--createInitialSolutions random "
                + "--variation crossoverAndMutationVariation "
                + "--selection tournament "
                + "--selectionTournamentSize 4 "
                + "--crossover SBX "
                + "--crossoverProbability 0.9 "
                + "--crossoverRepairStrategy bounds "
                + "--sbxDistributionIndex 20.0 "
                + "--mutation polynomial "
                + "--mutationProbability 0.01 "
                + "--mutationRepairStrategy bounds "
                + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    NSGAIIWithParameters nsgaiiWithParameters = new NSGAIIWithParameters();
    nsgaiiWithParameters.parseParameters(parameters);
    List<Parameter<?>> parameterList = nsgaiiWithParameters.configurableParameterList;
    nsgaiiWithParameters.print(parameterList);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = nsgaiiWithParameters.create();

    NSGAIIiraceParameterFile nsgaiiiraceParameterFile = new NSGAIIiraceParameterFile();
    nsgaiiiraceParameterFile.generateConfigurationFile(parameterList);
  }
}
