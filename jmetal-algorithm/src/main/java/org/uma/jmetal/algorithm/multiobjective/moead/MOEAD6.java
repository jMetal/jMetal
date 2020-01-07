package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionaryAlgorithm;
import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.component.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.initialsolutioncreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.component.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.NaryRandomSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class MOEAD6<S extends Solution<?>> extends AbstractEvolutionaryAlgorithm<S, List<S>>
    implements ObservableEntity {
  protected enum NeighborType {NEIGHBOR, POPULATION}

  private int evaluations;
  private int populationSize;
  private int offspringPopulationSize;

  private int neighborSize;
  private double neighborhoodSelectionProbability;
  private int maximumNumberOfReplacedSolutions;

  private AggregativeFunction aggregativeFunction;
  private SequenceGenerator<Integer> sequenceGenerator ;

  private WeightVectorNeighborhood<S> weightVectorNeighborhood;

  private SelectionOperator<List<S>, List<S>> selectionOperator;  private CrossoverOperator<S> crossoverOperator;
  private MutationOperator<S> mutationOperator;

  private int currentSubProblem;
  private NeighborType neighborType;

  private InitialSolutionsCreation<S> initialSolutionsCreation;
  private Termination termination;
  private Evaluation<S> evaluation ;
  private Replacement<S> replacement;
  private Variation<S> variation;
  private MatingPoolSelection<S> selection;

  private long startTime;
  private long totalComputingTime;

  private Map<String, Object> algorithmStatusData;
  private Observable<Map<String, Object>> observable;

  /** Constructor */
  public MOEAD6(
      Problem<S> problem,
      int populationSize,
      int neighborSize,
      double neighborhoodSelectionProbability,
      int maximumNumberOfReplacedSolutions,
      AggregativeFunction aggregativeFunction,
      SequenceGenerator<Integer> sequenceGenerator,
      Termination termination) {

    this.populationSize = populationSize;
    this.problem = problem;

    this.neighborSize = neighborSize ;
    this.neighborhoodSelectionProbability = neighborhoodSelectionProbability ;
    this.maximumNumberOfReplacedSolutions = maximumNumberOfReplacedSolutions ;

    this.aggregativeFunction = aggregativeFunction ;
    this.sequenceGenerator = sequenceGenerator ;

    this.weightVectorNeighborhood = new WeightVectorNeighborhood<>(populationSize, neighborSize) ;

    selectionOperator = new NaryRandomSelection<>(2);
    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;

    this.initialSolutionsCreation = new RandomSolutionsCreation<>(problem, populationSize);

    this.replacement = null ;

    this.variation =
        new CrossoverAndMutationVariation<>(
            offspringPopulationSize, crossoverOperator, mutationOperator);

    this.selection = null ;

    this.termination = termination;

    this.evaluation = new SequentialEvaluation<>() ;

    this.offspringPopulationSize = 1;

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

    for (S solution: population) {
      aggregativeFunction.update(solution.getObjectives());
    }

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
  protected List<S> createInitialPopulation() {
    return initialSolutionsCreation.create();
  }

  @Override
  protected List<S> evaluatePopulation(List<S> population) {
    return  evaluation.evaluate(population, getProblem());
  }

  /**
   * This method iteratively applies a {@link SelectionOperator} to the population to fill the
   * mating pool population.
   *
   * @param population
   * @return The mating pool population
   */
  @Override
  protected List<S> selection(List<S> population) {
    currentSubProblem = sequenceGenerator.getValue() ;
    neighborType = chooseNeighborType() ;

    List<S> matingPool;
    if (neighborType.equals(NeighborType.NEIGHBOR)) {
      matingPool = selectionOperator
              .execute(weightVectorNeighborhood.getNeighbors(population, currentSubProblem));
    } else {
      matingPool = selectionOperator.execute(population);
    }

    matingPool.add(population.get(currentSubProblem));

    return matingPool;  }

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
  protected List<S> reproduction(List<S> matingPool) {
    return variation.variate(population, matingPool);
  }

  @Override
  protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    List<S> newPopulation = replacement.replace(population, offspringPopulation);

    return newPopulation;
  }

  @Override
  public List<S> getResult() {
    return SolutionListUtils.getNonDominatedSolutions(getPopulation());
  }

  protected NeighborType chooseNeighborType() {
    double rnd = JMetalRandom.getInstance().nextDouble();
    NeighborType neighborType;

    if (rnd < neighborhoodSelectionProbability) {
      neighborType = NeighborType.NEIGHBOR;
    } else {
      neighborType = NeighborType.POPULATION;
    }
    return neighborType;
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

  public MOEAD6<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation ;

    return this;
  }

  public MOEAD6<S> setInitialSolutionsCreation(
      InitialSolutionsCreation<S> initialSolutionsCreation) {
    this.initialSolutionsCreation = initialSolutionsCreation;

    return this;
  }
}
