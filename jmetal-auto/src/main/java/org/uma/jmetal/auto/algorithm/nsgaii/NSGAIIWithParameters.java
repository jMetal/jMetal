package org.uma.jmetal.auto.algorithm.nsgaii;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.component.evaluation.Evaluation;
import org.uma.jmetal.auto.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.auto.component.initialsolutionscreation.InitialSolutionsCreation;
import org.uma.jmetal.auto.component.replacement.Replacement;
import org.uma.jmetal.auto.component.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.component.termination.Termination;
import org.uma.jmetal.auto.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.auto.component.variation.Variation;
import org.uma.jmetal.auto.parameter.IntegerParameter;
import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.auto.parameter.RealParameter;
import org.uma.jmetal.auto.parameter.catalogue.*;
import org.uma.jmetal.auto.parameter.irace.NSGAIIiraceParameterFile;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.auto.util.observer.impl.ExternalArchiveObserver;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.auto.util.ranking.impl.DominanceRanking;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.*;

public class NSGAIIWithParameters {
  public List<Parameter<?>> autoConfigurableParameterList = new ArrayList<>();
  public List<Parameter<?>> parameterList = new ArrayList<>();

  private ProblemNameParameter<DoubleSolution> problemNameParameter;
  private ReferenceFrontFilenameParameter referenceFrontFilename;
  private IntegerParameter maximumNumberOfEvaluationsParameter ;
  private AlgorithmResultParameter algorithmResultParameter;
  private PopulationSizeParameter populationSizeParameter;
  private PopulationSizeWithArchive populationSizeWithArchiveParameter;
  private OffspringPopulationSizeParameter offspringPopulationSizeParameter;
  private CreateInitialSolutionsParameter createInitialSolutionsParameter;
  private SelectionParameter selectionParameter;
  private VariationParameter variationParameter;

  public void parseParameters(String[] args) {
    problemNameParameter = new ProblemNameParameter<>(args);
    referenceFrontFilename = new ReferenceFrontFilenameParameter(args);
    maximumNumberOfEvaluationsParameter = new IntegerParameter("maximumNumberOfEvaluations", args, 1, 10000000) ;

    parameterList.add(problemNameParameter) ;
    parameterList.add(referenceFrontFilename) ;
    parameterList.add(maximumNumberOfEvaluationsParameter) ;

    for (Parameter<?> parameter : parameterList) {
      parameter.parse().check();
    }

    algorithmResultParameter =
        new AlgorithmResultParameter(args, Arrays.asList("externalArchive", "population"));
    populationSizeParameter = new PopulationSizeParameter(args);
    populationSizeWithArchiveParameter =
        new PopulationSizeWithArchive(args, Arrays.asList(10, 20, 50, 100, 200));
    algorithmResultParameter.addSpecificParameter("population", populationSizeParameter);
    algorithmResultParameter.addSpecificParameter("externalArchive", populationSizeWithArchiveParameter);

    offspringPopulationSizeParameter = new OffspringPopulationSizeParameter(args, Arrays.asList(1, 10, 50, 100));

    createInitialSolutionsParameter =
        new CreateInitialSolutionsParameter(
            args, Arrays.asList("random", "latinHypercubeSampling", "scatterSearch"));

    selectionParameter = new SelectionParameter(args, Arrays.asList("tournament", "random"));
    IntegerParameter selectionTournamentSize =
        new IntegerParameter("selectionTournamentSize", args, 2, 10);
    selectionParameter.addSpecificParameter("tournament", selectionTournamentSize);

    CrossoverParameter crossover = new CrossoverParameter(args, Arrays.asList("SBX", "BLX_ALPHA"));
    ProbabilityParameter crossoverProbability = new ProbabilityParameter("crossoverProbability", args);
    crossover.addGlobalParameter(crossoverProbability);
    RepairDoubleSolutionStrategyParameter crossoverRepairStrategy =
        new RepairDoubleSolutionStrategyParameter("crossoverRepairStrategy",
            args,  Arrays.asList("random", "round", "bounds"));
    crossover.addGlobalParameter(crossoverRepairStrategy);

    RealParameter distributionIndex =
        new RealParameter("sbxDistributionIndex", args,5.0, 400.0);
    crossover.addSpecificParameter("SBX", distributionIndex);

    RealParameter alpha = new RealParameter("blxAlphaCrossoverAlphaValue", args, 0.0, 1.0);
    crossover.addSpecificParameter("BLX_ALPHA", alpha);

    MutationParameter mutation =
        new MutationParameter(args, Arrays.asList("uniform", "polynomial"));
    ProbabilityParameter mutationProbability = new ProbabilityParameter("mutationProbability", args);
    mutation.addGlobalParameter(mutationProbability);
    RepairDoubleSolutionStrategyParameter mutationRepairStrategy =
        new RepairDoubleSolutionStrategyParameter("mutationRepairStrategy",
            args,  Arrays.asList("random", "round", "bounds"));
    mutation.addGlobalParameter(mutationRepairStrategy);

    RealParameter distributionIndexForMutation =
        new RealParameter("polynomialMutationDistributionIndex", args, 5.0, 400.0);
    mutation.addSpecificParameter("polynomial", distributionIndexForMutation);

    RealParameter uniformMutationPerturbation =
        new RealParameter("uniformMutationPerturbation", args, 0.0, 1.0);
    mutation.addSpecificParameter("uniform", uniformMutationPerturbation);

    variationParameter =
        new VariationParameter(args, Arrays.asList("crossoverAndMutationVariation"));
    variationParameter.addGlobalParameter(crossover);
    variationParameter.addGlobalParameter(mutation);

    autoConfigurableParameterList.add(algorithmResultParameter);
    autoConfigurableParameterList.add(offspringPopulationSizeParameter);
    autoConfigurableParameterList.add(createInitialSolutionsParameter);
    autoConfigurableParameterList.add(variationParameter);
    autoConfigurableParameterList.add(selectionParameter);

    for (Parameter<?> parameter : autoConfigurableParameterList) {
      parameter.parse().check();
    }
  }

  /**
   * Creates an instance of NSGA-II from the parsed parameters
   *
   * @return
   */
  EvolutionaryAlgorithm<DoubleSolution> create() {
    DoubleProblem problem = (DoubleProblem) problemNameParameter.getProblem();

    ExternalArchiveObserver<DoubleSolution> boundedArchiveObserver = null;

    if (algorithmResultParameter.getValue() == "externalArchive") {
      boundedArchiveObserver =
          new ExternalArchiveObserver<>(
              new CrowdingDistanceArchive<>(populationSizeParameter.getValue()));
      populationSizeParameter.setValue(populationSizeWithArchiveParameter.getValue());
    }

    Ranking<DoubleSolution> ranking = new DominanceRanking<>(new DominanceComparator<>());
    DensityEstimator<DoubleSolution> densityEstimator = new CrowdingDistanceDensityEstimator<>();
    MultiComparator<DoubleSolution> rankingAndCrowdingComparator =
        new MultiComparator<DoubleSolution>(
            Arrays.asList(
                ranking.getSolutionComparator(), densityEstimator.getSolutionComparator()));

    InitialSolutionsCreation<DoubleSolution> initialSolutionsCreation =
        createInitialSolutionsParameter.getParameter(problem, populationSizeParameter.getValue());
    Variation<DoubleSolution> variation =
        (Variation<DoubleSolution>)
            variationParameter.getParameter(offspringPopulationSizeParameter.getValue());
    MatingPoolSelection<DoubleSolution> selection =
        (MatingPoolSelection<DoubleSolution>)
            selectionParameter.getParameter(
                variation.getMatingPoolSize(), rankingAndCrowdingComparator);

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);

    Replacement<DoubleSolution> replacement =
        new RankingAndDensityEstimatorReplacement<>(ranking, densityEstimator);

    Termination termination = new TerminationByEvaluations(maximumNumberOfEvaluationsParameter.getValue());

    class NSGAII extends EvolutionaryAlgorithm<DoubleSolution> {
      private ExternalArchiveObserver<DoubleSolution> archiveObserver;

      public NSGAII(
          String name,
          Evaluation<DoubleSolution> evaluation,
          InitialSolutionsCreation<DoubleSolution> createInitialSolutionList,
          Termination termination,
          MatingPoolSelection<DoubleSolution> selection,
          Variation<DoubleSolution> variation,
          Replacement<DoubleSolution> replacement,
          ExternalArchiveObserver<DoubleSolution> archiveObserver) {
        super(
            name,
            evaluation,
            createInitialSolutionList,
            termination,
            selection,
            variation,
            replacement);
        this.archiveObserver = archiveObserver;
      }

      @Override
      public List<DoubleSolution> getResult() {
        if (archiveObserver != null) {
          return archiveObserver.getArchive().getSolutionList();
        } else {
          return population;
        }
      }
    }

    NSGAII nsgaii =
        new NSGAII(
            "NSGAII",
            evaluation,
            initialSolutionsCreation,
            termination,
            selection,
            variation,
            replacement,
            boundedArchiveObserver);

    return nsgaii;
  }

  public static void print(List<Parameter<?>> parameterList) {
    parameterList.forEach(item -> System.out.println(item));
  }

  public static void main(String[] args) {
    String[] parameters =
        ("--populationSizeParameter 100 "
                + "--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
                + "--referenceFrontFileName ZDT1.pf "
                + "--maximumNumberOfEvaluations 25000 "
                + "--algorithmResult population "
                + "--populationSize 100 "
                + "--offspringPopulationSize 100 "
                + "--createInitialSolutions random "
                + "--variation crossoverAndMutationVariation "
                + "--selection tournament "
                + "--selectionTournamentSize 2 "
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

    nsgaiiWithParameters.print(nsgaiiWithParameters.parameterList);
    nsgaiiWithParameters.print(nsgaiiWithParameters.autoConfigurableParameterList);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = nsgaiiWithParameters.create();
    nsgaII.run();

    new SolutionListOutput(nsgaII.getResult())
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    NSGAIIiraceParameterFile nsgaiiiraceParameterFile = new NSGAIIiraceParameterFile();
    nsgaiiiraceParameterFile.generateConfigurationFile(nsgaiiWithParameters.autoConfigurableParameterList);
  }
}
