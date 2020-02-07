package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionaryAlgorithm;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class implementing a generic MOEA/D algorithm using an external archive. The archive is updated
 * with the evaluated solutions and the solution * list it contains is returned as algorithm
 * result.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOEADWithArchive<S extends Solution<?>> extends MOEAD<S> {
  private Archive<S> archive ;
  private int numberOfSolutionsToTakeFromTheArchive ;

  /** Constructor */
  public MOEADWithArchive(
      Problem<S> problem,
      int populationSize,
      InitialSolutionsCreation<S> initialSolutionsCreation,
      Variation<S> variation,
      MatingPoolSelection<S> selection,
      Replacement<S> replacement,
      Termination termination,
      Archive<S> archive, int numberOfSolutionsToTakeFromTheArchive ) {

    super(problem, populationSize, initialSolutionsCreation, variation, selection, replacement, termination) ;

    this.archive = archive ;
    this.numberOfSolutionsToTakeFromTheArchive = numberOfSolutionsToTakeFromTheArchive ;
  }

  @Override
  protected List<S> evaluatePopulation(List<S> population) {
    List<S> evaluatedSolutionList = super.evaluatePopulation(population);
    for (S solution : evaluatedSolutionList) {
      archive.add(solution);
    }

    return evaluatedSolutionList;
  }

  @Override
  public List<S> getResult() {
    return SolutionListUtils.distanceBasedSubsetSelection(
            archive.getSolutionList(), numberOfSolutionsToTakeFromTheArchive);
  }

  public Archive<S> getArchive() {
    return archive ;
  }
}
