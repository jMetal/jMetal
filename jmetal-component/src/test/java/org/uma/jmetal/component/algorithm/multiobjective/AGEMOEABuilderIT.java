package org.uma.jmetal.component.algorithm.multiobjective;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEAEnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

class AGEMOEABuilderIT {

  @Test
  void ageMoeaScoresTheInitialPopulationBeforeTheFirstSelection() {
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    List<DoubleSolution> initialPopulation = createPopulation(problem);
    AtomicBoolean initialPopulationWasScored = new AtomicBoolean(false);

    Selection<DoubleSolution> selection = population -> {
      initialPopulationWasScored.set(
          population.stream()
              .allMatch(
                  solution ->
                      solution.attributes()
                          .containsKey(AGEMOEAEnvironmentalSelection.getAttributeId())));

      List<DoubleSolution> matingPool = new ArrayList<>(2);
      for (int i = 0; i < 2; i++) {
        matingPool.add((DoubleSolution) population.get(i).copy());
      }

      return matingPool;
    };

    Variation<DoubleSolution> variation = copyVariation(2);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    EvolutionaryAlgorithm<DoubleSolution> algorithm =
        new AGEMOEABuilder<>(
            problem,
            initialPopulation.size(),
            variation.offspringPopulationSize(),
            crossover,
            mutation,
            AGEMOEABuilder.Variant.AGEMOEA)
            .setCreateInitialPopulation(() -> initialPopulation)
            .setSelection(selection)
            .setVariation(variation)
            .setTermination(new TerminationByEvaluations(initialPopulation.size() + 2))
            .build();

    algorithm.run();

    assertTrue(initialPopulationWasScored.get());
  }

  @Test
  void ageMoeaAllowsConfiguringTheTournamentSizeWithoutReplacingTheSelectionComponent() {
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    List<DoubleSolution> initialPopulation = createPopulation(problem);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    EvolutionaryAlgorithm<DoubleSolution> algorithm =
        new AGEMOEABuilder<>(
            problem,
            initialPopulation.size(),
            2,
            crossover,
            mutation,
            AGEMOEABuilder.Variant.AGEMOEA)
            .setCreateInitialPopulation(() -> initialPopulation)
            .setTournamentSize(initialPopulation.size() + 1)
            .setTermination(new TerminationByEvaluations(initialPopulation.size() + 2))
            .build();

    assertThrows(InvalidConditionException.class, algorithm::run);
  }

  @Test
  void ageMoeaUsesTheConfiguredEnvironmentalSelectionForInitialScoringAndDefaultReplacement() {
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    List<DoubleSolution> initialPopulation = createPopulation(problem);
    AtomicInteger executionCounter = new AtomicInteger();

    Selection<DoubleSolution> selection = population -> {
      List<DoubleSolution> matingPool = new ArrayList<>(2);
      for (int i = 0; i < 2; i++) {
        matingPool.add((DoubleSolution) population.get(i).copy());
      }

      return matingPool;
    };

    Variation<DoubleSolution> variation = copyVariation(2);
    var environmentalSelection = new CountingEnvironmentalSelection(problem.numberOfObjectives(),
        executionCounter);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    EvolutionaryAlgorithm<DoubleSolution> algorithm =
        new AGEMOEABuilder<>(
            problem,
            initialPopulation.size(),
            variation.offspringPopulationSize(),
            crossover,
            mutation,
            AGEMOEABuilder.Variant.AGEMOEA)
            .setCreateInitialPopulation(() -> initialPopulation)
            .setSelection(selection)
            .setVariation(variation)
            .setEnvironmentalSelection(environmentalSelection)
            .setTermination(new TerminationByEvaluations(initialPopulation.size() + 2))
            .build();

    algorithm.run();

    assertEquals(2, executionCounter.get());
  }

  @Test
  void ageMoeaAllowsProvidingACustomReplacementComponent() {
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    List<DoubleSolution> initialPopulation = createPopulation(problem);
    AtomicBoolean replacementWasInvoked = new AtomicBoolean(false);

    Selection<DoubleSolution> selection = population -> {
      List<DoubleSolution> matingPool = new ArrayList<>(2);
      for (int i = 0; i < 2; i++) {
        matingPool.add((DoubleSolution) population.get(i).copy());
      }

      return matingPool;
    };

    Variation<DoubleSolution> variation = copyVariation(2);
    Replacement<DoubleSolution> replacement = (population, offspringPopulation) -> {
      replacementWasInvoked.set(true);
      return population;
    };

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    EvolutionaryAlgorithm<DoubleSolution> algorithm =
        new AGEMOEABuilder<>(
            problem,
            initialPopulation.size(),
            variation.offspringPopulationSize(),
            crossover,
            mutation,
            AGEMOEABuilder.Variant.AGEMOEA)
            .setCreateInitialPopulation(() -> initialPopulation)
            .setSelection(selection)
            .setVariation(variation)
            .setReplacement(replacement)
            .setTermination(new TerminationByEvaluations(initialPopulation.size() + 2))
            .build();

    algorithm.run();

    assertTrue(replacementWasInvoked.get());
  }

  private static List<DoubleSolution> createPopulation(DoubleProblem problem) {
    return List.of(
        solutionWithObjectives(problem, 1.0, 0.0),
        solutionWithObjectives(problem, 0.0, 1.0),
        solutionWithObjectives(problem, 0.5, 0.5),
        solutionWithObjectives(problem, 0.2, 0.8));
  }

  private static DoubleSolution solutionWithObjectives(
      DoubleProblem problem, double firstObjective, double secondObjective) {
    DoubleSolution solution = problem.createSolution();
    solution.objectives()[0] = firstObjective;
    solution.objectives()[1] = secondObjective;

    return solution;
  }

  private static Variation<DoubleSolution> copyVariation(int populationSize) {
    return new Variation<>() {
      @Override
      public List<DoubleSolution> variate(
          List<DoubleSolution> solutionList, List<DoubleSolution> matingPool) {
        List<DoubleSolution> offspringPopulation = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
          offspringPopulation.add((DoubleSolution) matingPool.get(i).copy());
        }

        return offspringPopulation;
      }

      @Override
      public int matingPoolSize() {
        return populationSize;
      }

      @Override
      public int offspringPopulationSize() {
        return populationSize;
      }
    };
  }

  private static class CountingEnvironmentalSelection
      extends AGEMOEAEnvironmentalSelection<DoubleSolution> {
    private final AtomicInteger executionCounter;

    CountingEnvironmentalSelection(int numberOfObjectives, AtomicInteger executionCounter) {
      super(numberOfObjectives);
      this.executionCounter = executionCounter;
    }

    @Override
    public List<DoubleSolution> execute(
        List<DoubleSolution> solutionList, int solutionsToSelect) {
      executionCounter.incrementAndGet();

      return super.execute(solutionList, solutionsToSelect);
    }
  }
}
