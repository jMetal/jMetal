package org.uma.jmetal.problem.singleobjective;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.sequenceproblem.impl.CharSequenceProblem;
import org.uma.jmetal.solution.sequencesolution.impl.CharSequenceSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.stream.IntStream;

/** This problem consists in finding a string matching a target string. */
@SuppressWarnings("serial")
public class StringMatching extends CharSequenceProblem {
  private String targetString;
  private final char @NotNull [] alphabet;

  public StringMatching(@NotNull String targetString, @NotNull String alphabet) {
    this.targetString = targetString;
    this.alphabet = alphabet.toCharArray();

    setNumberOfVariables(targetString.length());
    setNumberOfObjectives(1);
    setNumberOfConstraints(0);
    setName("String Match");
  }

  public StringMatching(@NotNull String targetString) {
    this(
        targetString,
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890, .-;:_!\"#%&/()=?@${[]}");
  }

  @Override
  public int getLength() {
    return targetString.length();
  }

  @Override
  public CharSequenceSolution evaluate(@NotNull CharSequenceSolution solution) {
    Check.that(solution.getLength() == targetString.length(), "The solution has an invalid length");
    var count = 0L;
    var bound = targetString.length();
      for (var i = 0; i < bound; i++) {
          if (targetString.charAt(i) != solution.variables().get(i)) {
              count++;
          }
      }
    var counter = (int) count;

      // counter += Math.abs(targetString.charAt(i) - solution.variables().get(i)) ;

      solution.objectives()[0] = counter;

    return solution ;
  }

  @Override
  public @NotNull CharSequenceSolution createSolution() {
    @NotNull CharSequenceSolution solution = new CharSequenceSolution(targetString.length(), getNumberOfObjectives()) ;
    for (var i = 0; i < targetString.length(); i++) {
      solution.variables().set(i, alphabet[JMetalRandom.getInstance().nextInt(0, alphabet.length-1)]);
    }

    return solution ;
  }

  public char[] getAlphabet() {
    return alphabet;
  }
}
