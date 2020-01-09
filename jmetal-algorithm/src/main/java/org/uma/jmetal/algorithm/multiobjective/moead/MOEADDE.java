package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionaryAlgorithm;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.initialsolutioncreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.replacement.impl.MOEADReplacement;
import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.selection.impl.PopulationAndNeighborhoodMatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.component.variation.impl.DifferentialCrossoverVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class MOEADDE extends AbstractEvolutionaryAlgorithm<DoubleSolution, List<DoubleSolution>>
    implements ObservableEntity {

  private int evaluations;
  private int populationSize;
  private int offspringPopulationSize;

  private InitialSolutionsCreation<DoubleSolution> initialSolutionsCreation;
  private Termination termination;
  private Evaluation<DoubleSolution> evaluation;
  private Replacement<DoubleSolution> replacement;
  private Variation<DoubleSolution> variation;
  private MatingPoolSelection<DoubleSolution> selection;

  private long startTime;
  private long totalComputingTime;

  private Map<String, Object> algorithmStatusData;
  private Observable<Map<String, Object>> observable;

  /** Constructor */
  public MOEADDE(
      Problem<DoubleSolution> problem,
      int populationSize,
      DifferentialCrossoverVariation variation,
      PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution> selection,
      MOEADReplacement replacement,
      Termination termination) {

    this.populationSize = populationSize;
    this.problem = problem;

    this.offspringPopulationSize = 1;

    this.variation = variation;
    this.selection = selection;
    this.initialSolutionsCreation = new RandomSolutionsCreation<>(problem, populationSize);
    this.replacement = replacement ;
    this.termination = termination ;
    this.evaluation = new SequentialEvaluation<>();

    this.algorithmStatusData = new HashMap<>();

    this.observable = new DefaultObservable<>("MOEA/D algorithm");
  }

  @Override
  public void run() {
    startTime = System.currentTimeMillis();
    super.run();
    totalComputingTime = System.currentTimeMillis() - startTime;
  }

  @Override
  protected void initProgress() {
    evaluations = populationSize;

    /*
    for (DoubleSolution solution : population) {
      aggregativeFunction.update(solution.getObjectives());
    }
*/
    algorithmStatusData.put("EVALUATIONS", evaluations);
    algorithmStatusData.put("POPULATION", population);
    algorithmStatusData.put("COMPUTING_TIME", System.currentTimeMillis() - startTime);

    observable.setChanged();
    observable.notifyObservers(algorithmStatusData);
  }

  @Override
  protected void updateProgress() {
    evaluations += offspringPopulationSize;
    algorithmStatusData.put("EVALUATIONS", evaluations);
    algorithmStatusData.put("POPULATION", population);
    algorithmStatusData.put("COMPUTING_TIME", System.currentTimeMillis() - startTime);

    observable.setChanged();
    observable.notifyObservers(algorithmStatusData);
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return termination.isMet(algorithmStatusData);
  }

  @Override
  protected List<DoubleSolution> createInitialPopulation() {
    return initialSolutionsCreation.create();
  }

  @Override
  protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
    return evaluation.evaluate(population, getProblem());
  }

  /**
   * This method iteratively applies a {@link SelectionOperator} to the population to fill the
   * mating pool population.
   *
   * @param population
   * @return The mating pool population
   */
  @Override
  protected List<DoubleSolution> selection(List<DoubleSolution> population) {
    List<DoubleSolution> matingPool = selection.select(population);

    Check.that(
        matingPool.size() == variation.getMatingPoolSize(),
        "The mating pool size is "
            + matingPool.size()
            + " instead of "
            + variation.getMatingPoolSize());

    return matingPool;
  }

  /**
   * This methods iteratively applies a {@link CrossoverOperator} a {@link MutationOperator} to the
   * population to create the offspring population. The population size must be divisible by the
   * number of parents required by the {@link CrossoverOperator}; this way, the needed parents are
   * taken sequentially from the population.
   *
   * <p>The number of solutions returned by the {@link CrossoverOperator} must be equal to the
   * offspringPopulationSize state variable
   *
   * @param matingPool
   * @return The new created offspring population
   */
  @Override
  protected List<DoubleSolution> reproduction(List<DoubleSolution> matingPool) {
    return variation.variate(population, matingPool);
  }

  @Override
  protected List<DoubleSolution> replacement(
      List<DoubleSolution> population, List<DoubleSolution> offspringPopulation) {
    return replacement.replace(population, offspringPopulation) ;
  }

  @Override
  public List<DoubleSolution> getResult() {
    return population;
  }

  @Override
  public String getName() {
    return "MOEA/D";
  }

  @Override
  public String getDescription() {
    return "MOEA/D";
  }

  public Map<String, Object> getAlgorithmStatusData() {
    return algorithmStatusData;
  }

  @Override
  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }

  public long getTotalComputingTime() {
    return totalComputingTime;
  }

  public long getEvaluations() {
    return evaluations;
  }

  public MOEADDE setEvaluation(Evaluation<DoubleSolution> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public MOEADDE setInitialSolutionsCreation(
      InitialSolutionsCreation<DoubleSolution> initialSolutionsCreation) {
    this.initialSolutionsCreation = initialSolutionsCreation;

    return this;
  }
}
