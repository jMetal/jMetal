package org.uma.jmetal.problem.multiobjective.dtlz;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Class aimed at generating a reference Pareto front por problem {@link DTLZ2Minus}
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerateReferenceFrontForProblemDTLZ2Minus {
  public static void main(String[] args) throws JMetalException {

    DoubleProblem problem = new DTLZ2Minus() ;
    List<DoubleSolution> solutions = new ArrayList<>() ;
    int numberOfPointsToGenerate = 5000 ;
    for (int i = 0; i < numberOfPointsToGenerate; i++) {
      DoubleSolution solution = problem.createSolution() ;
      for (int j = 2; j < problem.numberOfVariables(); j++) {
        solution.variables().set(j, 1.0) ;
      }

      solution = problem.evaluate(solution) ;
      solutions.add(solution) ;
    }

    int numberOfPointsToSelect = 1000 ;
    var finalSolutions = SolutionListUtils.distanceBasedSubsetSelection(solutions, numberOfPointsToSelect);

    new SolutionListOutput(finalSolutions)
        .setFunFileOutputContext(new DefaultFileOutputContext("DTLZ2Minus.csv", ","))
        .print();

  }
}
