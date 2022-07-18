package org.uma.jmetal.util.restartstrategy.impl;

import java.util.List;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.restartstrategy.RemoveSolutionsStrategy;

/**
 * Created by antonio on 6/06/17.
 */
public class RemoveNSolutionsAccordingToTheCrowdingDistance<S extends Solution<?>> implements RemoveSolutionsStrategy<S> {
  private int numberOfSolutionsToDelete ;

  public RemoveNSolutionsAccordingToTheCrowdingDistance(int numberOfSolutionsToDelete) {
    this.numberOfSolutionsToDelete = numberOfSolutionsToDelete ;
  }

  @Override
  public int remove(List<S> solutionList, DynamicProblem<S, ?> problem) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (problem == null) {
      throw new JMetalException("The problem is null") ;
    }
    var numberOfSolutions = solutionList.size() - numberOfSolutionsToDelete;
    if(numberOfSolutions < 0){
      numberOfSolutions = solutionList.size();
    }
    var archive = new CrowdingDistanceArchive<S>(numberOfSolutions) ;
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
