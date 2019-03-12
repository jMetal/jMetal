package org.uma.jmetal.auto.algorithm;

import org.uma.jmetal.auto.component.createinitialsolutions.CreateInitialSolutions;
import org.uma.jmetal.auto.component.evaluation.Evaluation;
import org.uma.jmetal.auto.util.observable.impl.DefaultObservable;
import org.uma.jmetal.auto.component.replacement.Replacement;
import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.component.termination.Termination;
import org.uma.jmetal.auto.component.variation.Variation;
import org.uma.jmetal.solution.Solution;

import java.util.HashMap;
import java.util.List;

public class EvolutionaryAlgorithm<S extends Solution<?>> extends Metaheuristic<S> {
  private Evaluation<S> evaluation;
  private CreateInitialSolutions<S> createInitialSolutionList;
  private Termination termination;
  private MatingPoolSelection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  protected long initTime;
  protected long totalComputingTime;
  protected int evaluations;

  private final String name ;

  public EvolutionaryAlgorithm(
          String name,
      Evaluation<S> evaluation,
      CreateInitialSolutions<S> createInitialSolutionList,
      Termination termination,
      MatingPoolSelection<S> selection,
      Variation<S> variation,
      Replacement<S> replacement) {
    this.name = name ;
    this.evaluation = evaluation;
    this.createInitialSolutionList = createInitialSolutionList;
    this.termination = termination;
    this.selection = selection;
    this.variation = variation;
    this.replacement = replacement;

    this.observable = new DefaultObservable<>("Evolutionary Algorithm") ;
  }

  @Override
  protected List<S> createInitialSolutionSet() {
    return createInitialSolutionList.create();
  }

  @Override
  protected List<S> evaluateSolutions(List<S> solutions) {
    return evaluation.evaluate(solutions);
  }

  @Override
  protected void initProgress() {
    evaluations = solutions.size() ;

    attributes = new HashMap<>() ;
    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", solutions);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());
  }

  @Override
  protected void updateProgress() {
    evaluations += variation.getOffspringPopulationSize() ;

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", solutions);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());

    observable.setChanged();
    observable.notifyObservers(attributes);
  }

  @Override
  protected boolean stoppingConditionIsMet() {
    return termination.isMet(attributes);
  }

  @Override
  protected void step() {
    List<S> matingSolutions = selection.select(solutions) ;
    List<S> offspringPopulation = variation.variate(solutions, matingSolutions) ;
    offspringPopulation = evaluateSolutions(offspringPopulation) ;

    solutions = replacement.replace(solutions, offspringPopulation) ;
  }

  @Override
  public void run() {
    initTime = System.currentTimeMillis();
    super.run();
    totalComputingTime = getCurrentComputingTime();
  }

  public long getCurrentComputingTime() {
    return System.currentTimeMillis() - initTime;
  }

  public int getEvaluations() {
    return evaluations;
  }

  public long getTotalComputingTime() {
    return totalComputingTime;
  }

  @Override
  public List<S> getResult() {
    return solutions;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return "Evolutionary algorithm";
  }
}
