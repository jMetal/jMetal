package org.uma.jmetal.util.restartstrategy.impl;

import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.restartstrategy.RemoveSolutionsStrategy;

import java.util.List;

/**
 * Created by antonio on 6/06/17.
 */
public class RemoveNSolutionsAccordingToTheHypervolumeContribution<S extends Solution<?>> implements RemoveSolutionsStrategy<S> {
  private int numberOfSolutionsToDelete ;

  public RemoveNSolutionsAccordingToTheHypervolumeContribution(int numberOfSolutionsToDelete) {
    this.numberOfSolutionsToDelete = numberOfSolutionsToDelete ;
  }

  @Override
  public int remove(List<S> solutionList, DynamicProblem<S, ?> problem) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (problem == null) {
      throw new JMetalException("The problem is null") ;
    }
    int numberOfSolutions = solutionList.size() - numberOfSolutionsToDelete;
    if(numberOfSolutions < 0){
      numberOfSolutions = solutionList.size();
    }
    HypervolumeArchive<S> archive = new HypervolumeArchive<>(numberOfSolutions,  new PISAHypervolume<>()) ;
    for (S solution: solutionList) {
      archive.add(solution) ;
    }
    solutionList.clear();

    for (S solution: archive.getSolutionList()) {
      solutionList.add(solution) ;
    }

    return numberOfSolutions ;
  }
}
