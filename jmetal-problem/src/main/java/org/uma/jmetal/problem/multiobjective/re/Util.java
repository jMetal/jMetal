package org.uma.jmetal.problem.multiobjective.re;

/**
 * Stuff required by some RE problems
 *
 * @author Antonio J. Nebro
 */
public class Util {
  public static double getClosestValue(double[] targetArray, double compValue) {
    double closestValue = targetArray[0];
    double minDiffValue = Math.abs(targetArray[0] - compValue);
    double tmpDiffValue = 0;

    for (int i = 1; i < targetArray.length; i++) {
      tmpDiffValue = Math.abs(targetArray[i] - compValue);
      if (tmpDiffValue < minDiffValue) {
        minDiffValue = tmpDiffValue;
        closestValue = targetArray[i];
      }
    }

    return closestValue;
  }
}
