package org.uma.jmetal.util.restartstrategy.impl;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.restartstrategy.RemoveSolutionsStrategy;

/**
 * Created by antonio on 6/06/17.
 */
public class RemoveNSolutionsAccordingToTheHypervolumeContribution<S extends Solution<?>> implements RemoveSolutionsStrategy<S> {
  private int numberOfSolutionsToDelete ;

  public RemoveNSolutionsAccordingToTheHypervolumeContribution(int numberOfSolutionsToDelete) {
    this.numberOfSolutionsToDelete = numberOfSolutionsToDelete ;
  }

  @Override
  public int remove(@Nullable List<S> solutionList, DynamicProblem<S, ?> problem) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (problem == null) {
      throw new JMetalException("The problem is null") ;
    }
    var numberOfSolutions = solutionList.size() - numberOfSolutionsToDelete;
    if(numberOfSolutions < 0){
      numberOfSolutions = solutionList.size();
    }
    var archive = new HypervolumeArchive<S>(numberOfSolutions,  new PISAHypervolume<>()) ;
    for (var solution: solutionList) {
      archive.add(solution) ;
    }
    solutionList.clear();

    for (var solution: archive.getSolutionList()) {
      solutionList.add(solution) ;
    }

    return numberOfSolutions ;
  }
}
