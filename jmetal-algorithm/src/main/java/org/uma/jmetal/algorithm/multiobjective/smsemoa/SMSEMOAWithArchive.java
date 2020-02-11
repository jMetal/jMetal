package org.uma.jmetal.algorithm.multiobjective.smsemoa;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.util.SMSEMOAReplacement;
import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.component.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.Archive;

import java.util.List;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class SMSEMOAWithArchive<S extends Solution<?>> extends SMSEMOA<S> {
  private Archive<S> archive;
  private int numberOfSolutionsToTakeFromTheArchive;

  /** Constructor */
  public SMSEMOAWithArchive(
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination,
      Hypervolume<S> hypervolume,
      Ranking<S> ranking,
      Archive<S> archive,
      int numberOfSolutionsToTakeFromTheArchive) {
    super(
        problem,
        populationSize,
        crossoverOperator,
        mutationOperator,
        termination,
        hypervolume,
        ranking);
    this.archive = archive;
    this.numberOfSolutionsToTakeFromTheArchive = numberOfSolutionsToTakeFromTheArchive;
  }

  /**
   * Constructor
   *
   * @param problem
   * @param populationSize
   * @param crossoverOperator
   * @param mutationOperator
   * @param termination
   * @param archive
   */
  public SMSEMOAWithArchive(
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination,
      Archive<S> archive,
      int numberOfSolutionsToTakeFromTheArchive) {
    this(
        problem,
        populationSize,
        crossoverOperator,
        mutationOperator,
        termination,
        new PISAHypervolume<>(),
        new FastNonDominatedSortRanking<>(),
        archive,
        numberOfSolutionsToTakeFromTheArchive);
  }

  @Override
  protected List<S> evaluatePopulation(List<S> solutionList) {
    List<S> evaluatedSolutionList = super.evaluatePopulation(solutionList);
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
    return archive;
  }
}
