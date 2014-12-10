package org.uma.jmetal.algorithm.singleobjective.geneticalgorithm;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ajnebro on 10/12/14.
 */
public class GenerationalGeneticAlgorithmBuilder {
  /**
   * Builder class
   */
    private Problem problem;
    private int maxIterations;
    private int populationSize;
    private CrossoverOperator crossoverOperator;
    private MutationOperator mutationOperator;
    private SelectionOperator selectionOperator;
    private SolutionListEvaluator evaluator;

    /**
     * Builder constructor
     */
    public GenerationalGeneticAlgorithmBuilder(Problem problem) {
      this.problem = problem;
      maxIterations = 250;
      populationSize = 100;
      evaluator = new SequentialSolutionListEvaluator();
    }

    public GenerationalGeneticAlgorithmBuilder setMaxIterations(int maxIterations) {
      this.maxIterations = maxIterations;

      return this;
    }

    public GenerationalGeneticAlgorithmBuilder setPopulationSize(int populationSize) {
      this.populationSize = populationSize;

      return this;
    }

    public GenerationalGeneticAlgorithmBuilder setCrossoverOperator(CrossoverOperator crossoverOperator) {
      this.crossoverOperator = crossoverOperator;

      return this;
    }

    public GenerationalGeneticAlgorithmBuilder setMutationOperator(MutationOperator mutationOperator) {
      this.mutationOperator = mutationOperator;

      return this;
    }

    public GenerationalGeneticAlgorithmBuilder setSelectionOperator(SelectionOperator selectionOperator) {
      this.selectionOperator = selectionOperator;

      return this;
    }

    public GenerationalGeneticAlgorithmBuilder setSolutionListEvaluator(SolutionListEvaluator evaluator) {
      this.evaluator = evaluator;

      return this;
    }

    public GenerationalGeneticAlgorithm build() {
      return new GenerationalGeneticAlgorithm(problem, maxIterations, populationSize,
          crossoverOperator, mutationOperator, selectionOperator, evaluator);
    }
}
