package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionaryAlgorithm;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.initialsolutioncreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.replacement.Replacement;
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
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;
import org.uma.jmetal.util.neighborhood.Neighborhood.NeighborType ;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class MOEADDE extends AbstractEvolutionaryAlgorithm<DoubleSolution, List<DoubleSolution>>
    implements ObservableEntity {

  private int evaluations;
  private int populationSize;
  private int offspringPopulationSize;
  private int maximumNumberOfReplacedSolutions;

  private AggregativeFunction aggregativeFunction;
  private SequenceGenerator<Integer> sequenceGenerator;

  private WeightVectorNeighborhood<DoubleSolution> weightVectorNeighborhood;

  private int currentSubProblemId;
  private NeighborType neighborType;

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
      int maximumNumberOfReplacedSolutions,
      AggregativeFunction aggregativeFunction,
      SequenceGenerator<Integer> sequenceGenerator,
      WeightVectorNeighborhood<DoubleSolution> neighborhood,
      DifferentialCrossoverVariation variation,
      PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution> selection,
      Termination termination) {

    this.populationSize = populationSize;
    this.problem = problem;

    this.maximumNumberOfReplacedSolutions = maximumNumberOfReplacedSolutions;

    this.offspringPopulationSize = 1;

    this.aggregativeFunction = aggregativeFunction;
    this.sequenceGenerator = sequenceGenerator;

    this.weightVectorNeighborhood = neighborhood ;

    this.variation = variation;

    this.selection = selection ;

    this.initialSolutionsCreation = new RandomSolutionsCreation<>(problem, populationSize);

    this.replacement = null;

    // this.selection = null;

    this.termination = termination;

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

    for (DoubleSolution solution : population) {
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

    currentSubProblemId = sequenceGenerator.getValue();

    /*
    neighborType = chooseNeighborType();

    List<DoubleSolution> matingPool;
    if (neighborType.equals(MOEADDE.NeighborType.NEIGHBOR)) {
      matingPool =
          selectionOperator.execute(
              weightVectorNeighborhood.getNeighbors(population, currentSubProblemId));
    } else {
      matingPool = selectionOperator.execute(population);
    }

    matingPool.add(population.get(currentSubProblemId));

    Check.that(
        matingPool.size() == variation.getMatingPoolSize(),
        "The mating pool size is "
            + matingPool.size()
            + " instead of "
            + variation.getMatingPoolSize());

    return matingPool;
     */

    List<DoubleSolution> matingPool = selection.select(population) ;
    neighborType = ((PopulationAndNeighborhoodMatingPoolSelection)selection).getNeighborType() ;

    return matingPool ;
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
    // List<S> newPopulation = replacement.replace(population, offspringPopulation);

    DoubleSolution newSolution = offspringPopulation.get(0);
    aggregativeFunction.update(newSolution.getObjectives());

    List<DoubleSolution> newPopulation =
        updateCurrentSubProblemNeighborhood(newSolution, population);

    sequenceGenerator.generateNext();

    return newPopulation;
  }

  @Override
  public List<DoubleSolution> getResult() {
    return population;
  }

  protected boolean maxReplacementLimitAchieved(int numberOfReplaceSolutions) {
    return numberOfReplaceSolutions >= maximumNumberOfReplacedSolutions;
  }

  protected List<DoubleSolution> updateCurrentSubProblemNeighborhood(
      DoubleSolution newSolution, List<DoubleSolution> population) {
    IntegerPermutationGenerator randomPermutation =
        new IntegerPermutationGenerator(
            neighborType.equals(NeighborType.NEIGHBOR) ? weightVectorNeighborhood.neighborhoodSize() : populationSize);

    int replacements = 0;

    for (int i = 0;
        i < randomPermutation.getSequenceLength() && !maxReplacementLimitAchieved(replacements);
        i++) {
      int k;
      if (neighborType.equals(NeighborType.NEIGHBOR)) {
        k =
            weightVectorNeighborhood
                .getNeighborhood()[currentSubProblemId][randomPermutation.getValue()];
      } else {
        k = randomPermutation.getValue();
      }
      randomPermutation.generateNext();

      double f1 =
          aggregativeFunction.compute(
              population.get(k).getObjectives(), weightVectorNeighborhood.getWeightVector()[k]);
      double f2 =
          aggregativeFunction.compute(
              newSolution.getObjectives(), weightVectorNeighborhood.getWeightVector()[k]);

      if (f2 < f1) {
        population.set(k, (DoubleSolution) newSolution.copy());
        replacements++;
      }
    }
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
