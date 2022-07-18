package org.uma.jmetal.algorithm.multiobjective.gde3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.impl.AbstractDifferentialEvolution;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.operator.selection.impl.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/** This class implements the GDE3 algorithm */
public class GDE3 extends AbstractDifferentialEvolution<List<DoubleSolution>> {
  protected int maxEvaluations;
  protected int evaluations;
  private int maxPopulationSize;

  protected Comparator<DoubleSolution> dominanceComparator;

  protected Ranking<DoubleSolution> ranking;
  protected DensityEstimator<DoubleSolution> crowdingDistance;

  protected SolutionListEvaluator<DoubleSolution> evaluator;

  /** Constructor */
  public GDE3(
      DoubleProblem problem,
      int populationSize,
      int maxEvaluations,
      DifferentialEvolutionSelection selection,
      DifferentialEvolutionCrossover crossover,
      SolutionListEvaluator<DoubleSolution> evaluator) {
    setProblem(problem);
    setMaxPopulationSize(populationSize);
    this.maxEvaluations = maxEvaluations;
    this.crossoverOperator = crossover;
    this.selectionOperator = selection;

    dominanceComparator = new DominanceWithConstraintsComparator<>();
    ranking = new FastNonDominatedSortRanking<>();
    crowdingDistance = new CrowdingDistanceDensityEstimator<>();

    this.evaluator = evaluator;
  }

  public void setMaxPopulationSize(int maxPopulationSize) {
    this.maxPopulationSize = maxPopulationSize;
  }

  public int getMaxPopulationSize() {
    return maxPopulationSize;
  }

  @Override
  protected void initProgress() {
    evaluations = getMaxPopulationSize();
  }

  @Override
  protected void updateProgress() {
    evaluations += getMaxPopulationSize();
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override
  protected List<DoubleSolution> createInitialPopulation() {
      @NotNull List<DoubleSolution> population = new ArrayList<>(getMaxPopulationSize());
    var bound = getMaxPopulationSize();
      for (var i = 0; i < bound; i++) {
        var solution = getProblem().createSolution();
          population.add(solution);
      }
      return population;
  }

  /**
   * Evaluate population method
   *
   * @param population The list of solutions to be evaluated
   * @return A list of evaluated solutions
   */
  @Override
  protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
    return evaluator.evaluate(population, getProblem());
  }

  @Override
  protected List<DoubleSolution> selection(List<DoubleSolution> population) {
    List<DoubleSolution> matingPopulation = new LinkedList<>();
    for (var i = 0; i < getMaxPopulationSize(); i++) {
      // Obtain parents. Two parameters are required: the population and the
      //                 index of the current individual
      selectionOperator.setIndex(i);
      var parents = selectionOperator.execute(population);

      matingPopulation.addAll(parents);
    }

    return matingPopulation;
  }

  @Override
  protected List<DoubleSolution> reproduction(List<DoubleSolution> matingPopulation) {
    List<DoubleSolution> offspringPopulation = new ArrayList<>();

    for (var i = 0; i < getMaxPopulationSize(); i++) {
      crossoverOperator.setCurrentSolution(getPopulation().get(i));
      List<DoubleSolution> parents = new ArrayList<>(3);
      for (var j = 0; j < 3; j++) {
        parents.add(matingPopulation.get(0));
        matingPopulation.remove(0);
      }

      crossoverOperator.setCurrentSolution(getPopulation().get(i));
      var children = crossoverOperator.execute(parents);

      offspringPopulation.add(children.get(0));
    }

    return offspringPopulation;
  }

  @Override
  protected List<DoubleSolution> replacement(
      List<DoubleSolution> population, List<DoubleSolution> offspringPopulation) {
    List<DoubleSolution> tmpList = new ArrayList<>();
    for (var i = 0; i < getMaxPopulationSize(); i++) {
      // Dominance test
      var child = offspringPopulation.get(i);
      var result = dominanceComparator.compare(population.get(i), child);
      if (result == -1) {
        // Solution i dominates child
        tmpList.add(population.get(i));
      } else if (result == 1) {
        // child dominates
        tmpList.add(child);
      } else {
        // the two solutions are non-dominated
        tmpList.add(child);
        tmpList.add(population.get(i));
      }
    }
    Ranking<DoubleSolution> ranking =  new FastNonDominatedSortRanking<>(dominanceComparator);
    ranking.compute(tmpList) ;

    var rankingAndCrowdingSelection = new RankingAndCrowdingSelection<DoubleSolution>(getMaxPopulationSize(), dominanceComparator);

    return rankingAndCrowdingSelection.execute(tmpList) ;
  }

  @Override
  public List<DoubleSolution> getResult() {
    return getNonDominatedSolutions(getPopulation());
  }

  protected List<DoubleSolution> getNonDominatedSolutions(List<DoubleSolution> solutionList) {
    return SolutionListUtils.getNonDominatedSolutions(solutionList);
  }

  @Override
  public String getName() {
    return "GDE3";
  }

  @Override
  public @NotNull String getDescription() {
    return "Generalized Differential Evolution version 3";
  }
}
