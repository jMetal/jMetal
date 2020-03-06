package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.archive.Archive;

import java.util.List;

/**
 * Class implementing a generic MOEA/D algorithm using an external archive. The archive is updated
 * with the evaluated solutions and the a subset of solution list it contains is returned as
 * algorithm result.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOEADWithArchive<S extends Solution<?>> extends MOEAD<S> {
  private Archive<S> archive;

  /** Constructor */
  public MOEADWithArchive(
      Problem<S> problem,
      int populationSize,
      MutationOperator<S> mutationOperator,
      CrossoverOperator<S> crossoverOperator,
      AggregativeFunction aggregativeFunction,
      double neighborhoodSelectionProbability,
      int maximumNumberOfReplacedSolutions,
      int neighborhoodSize,
      String weightVectorDirectory,
      Termination termination,
      Archive<S> archive) {

    super(
        problem,
        populationSize,
        mutationOperator,
        crossoverOperator,
        aggregativeFunction,
        neighborhoodSelectionProbability,
        maximumNumberOfReplacedSolutions,
        neighborhoodSize,
        weightVectorDirectory,
        termination);

    this.archive = archive;
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
    return archive.getSolutionList();
  }

  public Archive<S> getArchive() {
    return archive;
  }

  public List<S> getPopulation() {
    return population;
  }
}
