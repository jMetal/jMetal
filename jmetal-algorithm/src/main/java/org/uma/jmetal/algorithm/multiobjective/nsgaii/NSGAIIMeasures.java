package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.legacy.front.Front;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.measure.Measurable;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.CountingMeasure;
import org.uma.jmetal.util.measure.impl.DurationMeasure;
import org.uma.jmetal.util.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NSGAIIMeasures<S extends Solution<?>> extends NSGAII<S> implements Measurable {
  protected CountingMeasure evaluations ;
  protected DurationMeasure durationMeasure ;
  protected SimpleMeasureManager measureManager ;

  protected BasicMeasure<List<S>> solutionListMeasure ;
  protected BasicMeasure<Integer> numberOfNonDominatedSolutionsInPopulation ;
  protected BasicMeasure<Double> hypervolumeValue ;

  protected Front referenceFront ;

  /**
   * Constructor
   */
  public NSGAIIMeasures(Problem<S> problem, int maxIterations, int populationSize,
                        int matingPoolSize, int offspringPopulationSize,
                        CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                        SelectionOperator<List<S>, S> selectionOperator, Comparator<S> dominanceComparator, SolutionListEvaluator<S> evaluator) {
    super(problem, maxIterations, populationSize, matingPoolSize, offspringPopulationSize,
            crossoverOperator, mutationOperator, selectionOperator, dominanceComparator, evaluator) ;

    referenceFront = new ArrayFront() ;

    initMeasures() ;
  }

  @Override protected void initProgress() {
    evaluations.reset(getMaxPopulationSize());
  }

  @Override protected void updateProgress() {
    evaluations.increment(getMaxPopulationSize());

    solutionListMeasure.push(getPopulation());

    if (referenceFront.getNumberOfPoints() > 0) {
      hypervolumeValue.push(
              new PISAHypervolume<S>(referenceFront).evaluate(
                  SolutionListUtils.getNonDominatedSolutions(getPopulation())));
    }
  }

  @Override protected boolean isStoppingConditionReached() {
    return evaluations.get() >= maxEvaluations;
  }

  @Override
  public void run() {
    durationMeasure.reset();
    durationMeasure.start();
    super.run();
    durationMeasure.stop();
  }

  /* Measures code */
  private void initMeasures() {
    durationMeasure = new DurationMeasure() ;
    evaluations = new CountingMeasure(0) ;
    numberOfNonDominatedSolutionsInPopulation = new BasicMeasure<>() ;
    solutionListMeasure = new BasicMeasure<>() ;
    hypervolumeValue = new BasicMeasure<>() ;

    measureManager = new SimpleMeasureManager() ;
    measureManager.setPullMeasure("currentExecutionTime", durationMeasure);
    measureManager.setPullMeasure("currentEvaluation", evaluations);
    measureManager.setPullMeasure("numberOfNonDominatedSolutionsInPopulation",
        numberOfNonDominatedSolutionsInPopulation);

    measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
    measureManager.setPushMeasure("currentEvaluation", evaluations);
    measureManager.setPushMeasure("hypervolume", hypervolumeValue);
  }

  @Override
  public MeasureManager getMeasureManager() {
    return measureManager ;
  }

  @Override protected List<S> replacement(@NotNull List<S> population,
                                          List<S> offspringPopulation) {
    var pop = super.replacement(population, offspringPopulation) ;

    @NotNull Ranking<S> ranking = new FastNonDominatedSortRanking<>(dominanceComparator);
    ranking.compute(population);

    numberOfNonDominatedSolutionsInPopulation.set(ranking.getSubFront(0).size());

    return pop;
  }

  public CountingMeasure getEvaluations() {
    return evaluations;
  }

  @Override public String getName() {
    return "NSGAIIM" ;
  }

  @Override public String getDescription() {
    return "Nondominated Sorting Genetic Algorithm version II. Version using measures" ;
  }

  public void setReferenceFront(Front referenceFront) {
    this.referenceFront = referenceFront ;
  }
}
