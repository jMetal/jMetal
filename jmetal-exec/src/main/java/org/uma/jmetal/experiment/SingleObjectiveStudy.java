package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.algorithm.singleobjective.differentialevolution.DifferentialEvolutionBuilder;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.problem.singleobjective.Griewank;
import org.uma.jmetal.problem.singleobjective.Rastrigin;
import org.uma.jmetal.problem.singleobjective.Rosenbrock;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example of experimental study based on solving the ZDT1 problem but using five different number
 * of variables. This can be interesting to study the behaviour of the algorithms when solving an
 * scalable problem (in the number of variables). The used algorithms are NSGA-II, SPEA2 and SMPSO.
 *
 * <p>This experiment assumes that the reference Pareto front is of problem ZDT1 is known and that
 * there is a file called ZDT1.pf containing it.
 *
 * <p>Six quality indicators are used for performance assessment.
 *
 * <p>The steps to carry out the experiment are: 1. Configure the experiment 2. Execute the
 * algorithms 3. Generate the reference Pareto fronts 4. Compute the quality indicators 5. Generate
 * Latex tables reporting means and medians 6. Generate Latex tables with the result of applying the
 * Wilcoxon Rank Sum Test 7. Generate Latex tables with the ranking obtained by applying the
 * Friedman test 8. Generate R scripts to obtain boxplots
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SingleObjectiveStudy {

  private static final int INDEPENDENT_RUNS = 10;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Needed arguments: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new Sphere(10)));
    problemList.add(new ExperimentProblem<>(new Rosenbrock(10)));
    problemList.add(new ExperimentProblem<>(new Rastrigin(10)));
    problemList.add(new ExperimentProblem<>(new Griewank(10)));

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
        configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("SingleObjectiveStudy")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory("/pareto_fronts")
            .setIndicatorList(Arrays.asList(new Fitness<>()))
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(8)
            .build();

    new ExecuteAlgorithms<>(experiment).run();

    new ComputeQualityIndicators<>(experiment).runSingleObjectiveIndicators();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(2).setColumns(2).run();
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}. The {@link
   * ExperimentAlgorithm} has an optional tag component, that can be set as it is shown in this
   * example, where four variants of a same algorithm are defined.
   */
  static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
      List<ExperimentProblem<DoubleSolution>> problemList) {

    class WrapperAlgorithm implements Algorithm<List<DoubleSolution>> {

      List<DoubleSolution> result;
      Algorithm<DoubleSolution> algorithm;

      WrapperAlgorithm(Algorithm<DoubleSolution> algorithm) {
        this.algorithm = algorithm;
      }

      @Override
      public void run() {
        algorithm.run();
        result = new ArrayList<>(1);
        result.add(algorithm.getResult());
      }

      @Override
      public List<DoubleSolution> getResult() {
        return result;
      }

      @Override
      public String getName() {
        return algorithm.getName();
      }

      @Override
      public String getDescription() {
        return algorithm.getDescription();
      }
    }

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
    for (int run = 0; run < INDEPENDENT_RUNS; run++) {

      for (int i = 0; i < problemList.size(); i++) {
        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
        MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(
                1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 20.0);
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection =
            new BinaryTournamentSelection<DoubleSolution>();

        Algorithm<DoubleSolution> algorithm =
            new GeneticAlgorithmBuilder<>(problemList.get(i).getProblem(), crossover, mutation)
                .setPopulationSize(100)
                .setMaxEvaluations(250000)
                .setSelectionOperator(selection)
                .build();

        algorithms.add(
            new ExperimentAlgorithm<>(new WrapperAlgorithm(algorithm), problemList.get(i), run));
      }

      for (int i = 0; i < problemList.size(); i++) {
        Algorithm<DoubleSolution> algorithm;
        DifferentialEvolutionSelection selection;
        DifferentialEvolutionCrossover crossover;

        crossover = new DifferentialEvolutionCrossover(0.5, 0.5, "rand/1/bin");
        selection = new DifferentialEvolutionSelection();

        algorithm =
            new DifferentialEvolutionBuilder((DoubleProblem) problemList.get(i).getProblem())
                .setCrossover(crossover)
                .setSelection(selection)
                .setMaxEvaluations(250000)
                .setPopulationSize(100)
                .build();

        algorithms.add(
            new ExperimentAlgorithm<>(new WrapperAlgorithm(algorithm), problemList.get(i), run));
      }
    }
    return algorithms;
  }
}
