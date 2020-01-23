package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.sequenceproblem.impl.CharSequenceProblem;
import org.uma.jmetal.solution.sequencesolution.impl.CharSequenceSolution;
import org.uma.jmetal.util.checking.Check;

/**
 * This problem consists in finding a string matching a target string.
 */
public class StringMatching extends CharSequenceProblem {
  private String targetString;

  public StringMatching(String targetString) {
    this.targetString = targetString;

    setNumberOfVariables(targetString.length());
    setNumberOfObjectives(1);
    setNumberOfConstraints(0);
    setName("String Match");
  }

  @Override
  public int getLength() {
    return targetString.length();
  }

  @Override
  public void evaluate(CharSequenceSolution solution) {
    Check.that(solution.getLength() == targetString.length(), "The solution has an invalid length");
    int counter = 0;


    for (int i = 0; i < targetString.length(); i++) {
      if (targetString.charAt(i) != solution.getVariable(i)) {
        counter++;
        //counter += Math.abs(targetString.charAt(i) - solution.getVariable(i)) ;
      }
    }

    solution.setObjective(0, counter);
  }
}
