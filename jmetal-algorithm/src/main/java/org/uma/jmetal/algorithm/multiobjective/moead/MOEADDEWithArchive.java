package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.archive.Archive;

import java.util.List;

/**
 * This class is intended to provide an implementation of the MOEA/D-DE algorithm using an external
 * archive. The archive is updated * with the evaluated solutions and the a subset of solution list
 * it contains is returned as * algorithm result. *
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOEADDEWithArchive extends MOEADDE {
  private Archive<DoubleSolution> archive;

  /**
   * Constructor with the parameters used in the paper describing MOEA/D-DE.
   *
   * @param problem
   * @param populationSize
   * @param f
   * @param cr
   * @param neighborhoodSelectionProbability
   * @param maximumNumberOfReplacedSolutions
   * @param neighborhoodSize
   * @param termination
   */
  public MOEADDEWithArchive(
      Problem<DoubleSolution> problem,
      int populationSize,
      double cr,
      double f,
      AggregativeFunction aggregativeFunction,
      double neighborhoodSelectionProbability,
      int maximumNumberOfReplacedSolutions,
      int neighborhoodSize,
      String weightVectorDirectory,
      Termination termination,
      Archive<DoubleSolution> archive) {
    super(
        problem,
        populationSize,
        cr,
        f,
        aggregativeFunction,
        neighborhoodSelectionProbability,
        maximumNumberOfReplacedSolutions,
        neighborhoodSize,
        weightVectorDirectory,
        termination);

    this.archive = archive;
  }

  @Override
  protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
    List<DoubleSolution> evaluatedSolutionList = super.evaluatePopulation(population);
    for (DoubleSolution solution : evaluatedSolutionList) {
      archive.add(solution);
    }

    return evaluatedSolutionList;
  }

  @Override
  public List<DoubleSolution> getResult() {
    return archive.getSolutionList();
  }

  public Archive<DoubleSolution> getArchive() {
    return archive;
  }

  public List<DoubleSolution> getPopulation() {
    return population;
  }
}
