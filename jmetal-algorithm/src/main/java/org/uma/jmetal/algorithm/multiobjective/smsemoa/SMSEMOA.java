package org.uma.jmetal.algorithm.multiobjective.smsemoa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class SMSEMOA<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
  protected final int maxEvaluations;
  protected final double offset ;

  protected int evaluations;

  private Hypervolume<S> hypervolume;
  protected Comparator<S> dominanceComparator ;

  /**
   * Constructor
   */
  public SMSEMOA(Problem<S> problem, int maxEvaluations, int populationSize, double offset,
                 CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                 SelectionOperator<List<S>, S> selectionOperator, Comparator<S> dominanceComparator, Hypervolume<S> hypervolumeImplementation) {
    super(problem) ;
    this.maxEvaluations = maxEvaluations;
    setMaxPopulationSize(populationSize);

    this.offset = offset ;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;
    this.dominanceComparator = dominanceComparator ;
    this.hypervolume = hypervolumeImplementation ;
  }

  @Override protected void initProgress() {
    evaluations = getMaxPopulationSize() ;
  }

  @Override protected void updateProgress() {
    evaluations++ ;
  }

  @Override protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations ;
  }

  @Override protected List<S> evaluatePopulation(@NotNull List<S> population) {
    for (S solution : population) {
      getProblem().evaluate(solution);
    }
    return population ;
  }

  @Override protected List<S> selection(List<S> population) {
      List<S> matingPopulation = new ArrayList<>(2);
      for (int i = 0; i < 2; i++) {
          S execute = selectionOperator.execute(population);
          matingPopulation.add(execute);
      }

      return matingPopulation;
  }

  @Override protected @NotNull List<S> reproduction(List<S> population) {
    List<S> offspringPopulation = new ArrayList<>(1);

    List<S> parents = new ArrayList<>(2);
    parents.add(population.get(0));
    parents.add(population.get(1));

    List<S> offspring = crossoverOperator.execute(parents);

    mutationOperator.execute(offspring.get(0));

    offspringPopulation.add(offspring.get(0));
    return offspringPopulation;
  }

  @Override protected List<S> replacement(@NotNull List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    @NotNull Ranking<S> ranking = new MergeNonDominatedSortRanking<>();
    ranking.compute(jointPopulation) ;

    List<S> lastSubfront = ranking.getSubFront(ranking.getNumberOfSubFronts()-1) ;

    lastSubfront = hypervolume.computeHypervolumeContribution(lastSubfront, jointPopulation) ;

    List<S> resultPopulation = IntStream.range(0, ranking.getNumberOfSubFronts() - 1).mapToObj(i -> ranking.getSubFront(i).stream()).flatMap(Function.identity()).collect(Collectors.toList());

      for (int i = 0; i < lastSubfront.size()-1; i++) {
      resultPopulation.add(lastSubfront.get(i)) ;
    }

    return resultPopulation ;
  }

  @Override public List<S> getResult() {
    return getPopulation();
  }

  @Override public @NotNull String getName() {
    return "SMSEMOA" ;
  }

  @Override public @NotNull String getDescription() {
    return "S metric selection EMOA" ;
  }
}