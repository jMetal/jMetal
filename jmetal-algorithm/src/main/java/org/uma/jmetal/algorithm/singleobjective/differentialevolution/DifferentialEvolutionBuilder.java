package org.uma.jmetal.algorithm.singleobjective.differentialevolution;

import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Created by ajnebro on 25/11/14.
 * Builder class
 */
public class DifferentialEvolutionBuilder {
  private DoubleProblem problem ;
  private int populationSize;
  private int maxEvaluations;
  private DifferentialEvolutionCrossover crossoverOperator ;
  private DifferentialEvolutionSelection selectionOperator ;
  private SolutionListEvaluator evaluator ;

  public DifferentialEvolutionBuilder(DoubleProblem problem) {
    this.problem = problem ;
    this.populationSize = 100 ;
    this.maxEvaluations = 20000 ;
    this.crossoverOperator = new DifferentialEvolutionCrossover(0.5, 0.5, "rand/1/bin") ;
    this.selectionOperator = new DifferentialEvolutionSelection() ;
    this.evaluator = new SequentialSolutionListEvaluator() ;
  }

  public DifferentialEvolutionBuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize ;

    return this ;
  }

  public DifferentialEvolutionBuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations ;

    return this ;
  }

  public DifferentialEvolutionBuilder setCrossover (DifferentialEvolutionCrossover crossover) {
    this.crossoverOperator = crossover ;

    return this ;
  }

  public DifferentialEvolutionBuilder setSelection (DifferentialEvolutionSelection selection) {
    this.selectionOperator = selection ;

    return this ;
  }

  public DifferentialEvolutionBuilder setEvaluator (SolutionListEvaluator evaluator) {
    this.evaluator = evaluator ;

    return this ;
  }

  public DifferentialEvolution build() {
    return new DifferentialEvolution(problem, maxEvaluations, populationSize, crossoverOperator, selectionOperator, evaluator) ;
  }
}

