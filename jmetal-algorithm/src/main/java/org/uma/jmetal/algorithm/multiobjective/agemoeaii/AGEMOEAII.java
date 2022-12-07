package org.uma.jmetal.algorithm.multiobjective.agemoeaii;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.agemoea.AGEMOEA;
import org.uma.jmetal.algorithm.multiobjective.agemoea.AGEMOEABuilder;
import org.uma.jmetal.algorithm.multiobjective.agemoeaii.util.AGEMOEA2EnvironmentalSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 * Implementation of AGE-MOEA-II (Adaptive GEometry-based Many-objective Evolutionary Algorithm II).
 *
 * Reference: A. Panichella, "An Improved Pareto Front Modeling Algorithm for Large-scale Many-Objective Optimization",
 * Proceedings of the Genetic and Evolutionary Computation Conference (GECCO). 2022.
 *
 * @author Annibale Panichella
 */
public class AGEMOEAII<S extends Solution<?>> extends AGEMOEA<S> {
  protected SolutionListEvaluator<S> evaluator ;

  /** Constructor */
  public AGEMOEAII(AGEMOEAIIBuilder<S> builder) { // can be created from the AGEMOEAIIBuilder within the same package
    super((AGEMOEABuilder<S>) builder) ;
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
    List<S> matingPopulation = new ArrayList<>(population.size()) ;
    for (int i = 0; i < getMaxPopulationSize(); i++) {
      S solution = selectionOperator.execute(population);
      matingPopulation.add((S) solution.copy()) ;
    }

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


    AGEMOEA2EnvironmentalSelection<S> environmentalSelection;
    environmentalSelection = new AGEMOEA2EnvironmentalSelection(getProblem().numberOfObjectives());

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
    return "AGE-MOEA-II" ;
  }

  @Override public String getDescription() {
    return "Adaptive Geometry Estimation based MOEA II" ;
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
