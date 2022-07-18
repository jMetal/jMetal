package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.sequenceproblem.impl.CharSequenceProblem;
import org.uma.jmetal.solution.sequencesolution.impl.CharSequenceSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.stream.IntStream;

/** This problem consists in finding a string matching a target string. */
@SuppressWarnings("serial")
public class StringMatching extends CharSequenceProblem {
  private String targetString;
  private final char[] alphabet;

  public StringMatching(String targetString, String alphabet) {
    this.targetString = targetString;
    this.alphabet = alphabet.toCharArray();

    setNumberOfVariables(targetString.length());
    setNumberOfObjectives(1);
    setNumberOfConstraints(0);
    setName("String Match");
  }

  public StringMatching(String targetString) {
    this(
        targetString,
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890, .-;:_!\"#%&/()=?@${[]}");
  }

  @Override
  public int getLength() {
    return targetString.length();
  }

  @Override
  public CharSequenceSolution evaluate(CharSequenceSolution solution) {
    Check.that(solution.getLength() == targetString.length(), "The solution has an invalid length");
    int counter = (int) IntStream.range(0, targetString.length()).filter(i -> targetString.charAt(i) != solution.variables().get(i)).count();

      // counter += Math.abs(targetString.charAt(i) - solution.variables().get(i)) ;

      solution.objectives()[0] = counter;

    return solution ;
  }

  @Override
  public CharSequenceSolution createSolution() {
    CharSequenceSolution solution = new CharSequenceSolution(targetString.length(), getNumberOfObjectives()) ;
    for (int i = 0 ; i < targetString.length(); i++) {
      solution.variables().set(i, alphabet[JMetalRandom.getInstance().nextInt(0, alphabet.length-1)]);
    }

    return solution ;
  }

  public char[] getAlphabet() {
    return alphabet;
  }
}
