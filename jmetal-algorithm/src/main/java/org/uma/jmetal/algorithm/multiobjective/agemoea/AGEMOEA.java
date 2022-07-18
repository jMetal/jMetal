package org.uma.jmetal.algorithm.multiobjective.agemoea;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.agemoea.util.AGEMOEAEnvironmentalSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 * Implementation of AGE-MOEA (Adaptive GEometry-based Many-objective Evolutionary Algorithm).
 *
 * Reference: A. Panichella, "An adaptive evolutionary algorithm based on non-Euclidean geometry for
 * many-objective optimization", Proceedings of the Genetic and Evolutionary Computation Conference (GECCO). 2019.
 *
 * @author Annibale Panichella
 */
public class AGEMOEA<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
  protected int iterations ;
  protected int maxIterations ;

  protected SolutionListEvaluator<S> evaluator ;

  /** Constructor */
  public AGEMOEA(AGEMOEABuilder<S> builder) { // can be created from the NSGAIIIBuilder within the same package
    super(builder.getProblem()) ;
    maxIterations = builder.getMaxIterations() ;
    crossoverOperator =  builder.getCrossoverOperator() ;
    mutationOperator  =  builder.getMutationOperator() ;
    selectionOperator =  builder.getSelectionOperator() ;
    evaluator = builder.getEvaluator() ;
    maxPopulationSize = builder.getPopulationSize();
  }

  @Override
  protected void initProgress() {
    iterations = 1 ;
  }

  @Override
  protected void updateProgress() {
    iterations++ ;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override
  protected List<S> evaluatePopulation(List<S> population) {
    population = evaluator.evaluate(population, getProblem()) ;

    return population ;
  }

  @Override
  protected List<S> selection(List<S> population) {
    List<S> matingPopulation = IntStream.range(0, getMaxPopulationSize()).mapToObj(i -> selectionOperator.execute(population)).map(solution -> (S) solution.copy()).collect(Collectors.toCollection(() -> new ArrayList<>(population.size())));

      return matingPopulation;
  }

  @Override
  protected List<S> reproduction(List<S> matingPool) {
    int numberOfParents = crossoverOperator.getNumberOfRequiredParents();

    checkNumberOfParents(matingPool, numberOfParents);

    List<S> offspringPopulation = new ArrayList<>(maxPopulationSize);
    for (int i = 0; i < matingPool.size(); i += numberOfParents) {
      List<S> parents = new ArrayList<>(numberOfParents);
      for (int j = 0; j < numberOfParents; j++) {
        parents.add(matingPool.get(i + j));
      }

      List<S> offspring = crossoverOperator.execute(parents);

      for (S s : offspring) {
        mutationOperator.execute(s);
        offspringPopulation.add(s);
        if (offspringPopulation.size() >= maxPopulationSize) {
          break;
        }
      }
    }
    return offspringPopulation;
  }

  @Override
  protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population) ;
    jointPopulation.addAll(offspringPopulation) ;


    AGEMOEAEnvironmentalSelection<S> environmentalSelection;
    environmentalSelection = new AGEMOEAEnvironmentalSelection(getProblem().getNumberOfObjectives());

    return environmentalSelection.execute(jointPopulation, maxPopulationSize);
  }

  @Override
  public List<S> getResult() {
    return getNonDominatedSolutions(getPopulation()) ;
  }

  protected List<S> getNonDominatedSolutions(List<S> solutionList) {
    return SolutionListUtils.getNonDominatedSolutions(solutionList) ;
  }

  @Override public String getName() {
    return "AGE-MOEA" ;
  }

  @Override public String getDescription() {
    return "Adaptive Geometry Estimation based MOEA" ;
  }

  @Override
  public void run() {
    List<S> offspringPopulation;
    List<S> matingPopulation;

    population = createInitialPopulation();
    population = evaluatePopulation(population);

    // compute the survival score for the initial population
    population = replacement(population, new ArrayList<>());
    initProgress();
    while (!isStoppingConditionReached()) {
      matingPopulation = selection(population);
      offspringPopulation = reproduction(matingPopulation);
      offspringPopulation = evaluatePopulation(offspringPopulation);
      population = replacement(population, offspringPopulation);
      updateProgress();
    }
  }

}
