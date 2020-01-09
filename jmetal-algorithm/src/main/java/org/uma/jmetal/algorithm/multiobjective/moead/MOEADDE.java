package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.replacement.impl.MOEADReplacement;
import org.uma.jmetal.component.selection.impl.PopulationAndNeighborhoodMatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.variation.impl.DifferentialCrossoverVariation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class MOEADDE extends MOEAD<DoubleSolution> {
  
  /** Constructor */
  public MOEADDE(
      Problem<DoubleSolution> problem,
      int populationSize,
      InitialSolutionsCreation<DoubleSolution> initialSolutionsCreation,
      DifferentialCrossoverVariation variation,
      PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution> selection,
      MOEADReplacement replacement,
      Termination termination) {
    super(problem, populationSize, initialSolutionsCreation, variation, selection, replacement, termination) ;
  }

}
