package org.uma.jmetal.algorithm.multiobjective.moead.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/** Utilities methods to used by MOEA/D */
public class MOEADUtils {

  /**
   * Quick sort procedure (ascending order)
   *
   * @param array
   * @param idx
   * @param from
   * @param to
   */
  public static void quickSort(double[] array, int[] idx, int from, int to) {
    if (from < to) {
      double temp = array[to];
      int tempIdx = idx[to];
      int i = from - 1;
      for (int j = from; j < to; j++) {
        if (array[j] <= temp) {
          i++;
          double tempValue = array[j];
          array[j] = array[i];
          array[i] = tempValue;
          int tempIndex = idx[j];
          idx[j] = idx[i];
          idx[i] = tempIndex;
        }
      }
      array[to] = array[i + 1];
      array[i + 1] = temp;
      idx[to] = idx[i + 1];
      idx[i + 1] = tempIdx;
      quickSort(array, idx, from, i);
      quickSort(array, idx, i + 1, to);
    }
  }

  public static double distVector(double[] vector1, double[] vector2) {
    int dim = vector1.length;
    double sum = IntStream.range(0, dim).mapToDouble(n -> (vector1[n] - vector2[n]) * (vector1[n] - vector2[n])).sum();
      return Math.sqrt(sum);
  }

  public static void minFastSort(double x[], int idx[], int n, int m) {
    for (int i = 0; i < m; i++) {
      for (int j = i + 1; j < n; j++) {
        if (x[i] > x[j]) {
          double temp = x[i];
          x[i] = x[j];
          x[j] = temp;
          int id = idx[i];
          idx[i] = idx[j];
          idx[j] = id;
        }
      }
    }
  }

  public static void randomPermutation(int[] perm, int size) {
    JMetalRandom randomGenerator = JMetalRandom.getInstance();
    int[] index = new int[size];
    boolean[] flag = new boolean[size];

    for (int n = 0; n < size; n++) {
      index[n] = n;
      flag[n] = true;
    }

    int num = 0;
    while (num < size) {
      int start = randomGenerator.nextInt(0, size - 1);
      while (true) {
        if (flag[start]) {
          perm[num] = index[start];
          flag[start] = false;
          num++;
          break;
        }
        if (start == (size - 1)) {
          start = 0;
        } else {
          start++;
        }
      }
    }
  }

  /**
   * This method selects a subset of evenly spread solutions from a solution list. The
   * implementation is based on the method described in the MOEA/D-DRA paper.
   *
   * @param solutionList
   * @param newSolutionListSize
   * @param <S>
   * @return
   */
  public static <S extends Solution<?>> List<S> getSubsetOfEvenlyDistributedSolutions(
      List<S> solutionList, int newSolutionListSize) {
    List<S> resultSolutionList = new ArrayList<>(newSolutionListSize);
    if (solutionList == null) {
      throw new JMetalException("The solution list is null");
    }

    if (solutionList.size() > 0) {
      int numberOfObjectives = solutionList.get(0).objectives().length;
      if (numberOfObjectives == 2) {
        twoObjectivesCase(solutionList, resultSolutionList, newSolutionListSize);
      } else {
        moreThanTwoObjectivesCase(solutionList, resultSolutionList, newSolutionListSize);
      }
    }

    return resultSolutionList;
  }

  private static <S extends Solution<?>> void twoObjectivesCase(
      List<S> solutionList, List<S> resultSolutionList, int newSolutionListSize) {
    double[][] lambda = new double[newSolutionListSize][2];

    // Compute the mombi2-weights.weight vectors
    for (int i = 0; i < newSolutionListSize; i++) {
      double a = 1.0 * i / (newSolutionListSize - 1);
      lambda[i][0] = a;
      lambda[i][1] = 1 - a;
    }

    IdealPoint idealPoint = new IdealPoint(2);
    solutionList.stream().forEach(solution -> idealPoint.update(solution.objectives()));

    // Select the best solution for each mombi2-weights.weight vector
    for (int i = 0; i < newSolutionListSize; i++) {
      S currentBest = solutionList.get(0);
      double value = scalarizingFitnessFunction(currentBest, lambda[i], idealPoint);
      for (int j = 1; j < solutionList.size(); j++) {
        double aux =
            scalarizingFitnessFunction(
                solutionList.get(j),
                lambda[i],
                idealPoint); // we are looking for the best for the mombi2-weights.weight i
        if (aux < value) { // solution in position j is better!
          value = aux;
          currentBest = solutionList.get(j);
        }
      }
      @SuppressWarnings("unchecked")
      S copy = (S) currentBest.copy();
      resultSolutionList.add(copy);
    }
  }

  public static void shuffle(int sequence[]) {
    JMetalRandom randomGenerator = JMetalRandom.getInstance();
    int[] index = new int[sequence.length];
    boolean[] flag = new boolean[sequence.length];
    System.arraycopy(sequence, 0, index, 0, sequence.length);

    for (int n = 0; n < sequence.length; n++) {
      flag[n] = true;
    }

    int num = 0;
    while (num < sequence.length) {
      int start = randomGenerator.nextInt(0, sequence.length - 1);
      while (true) {
        if (flag[start]) {
          sequence[num] = index[start];
          flag[start] = false;
          num++;
          break;
        }
        if (start == (sequence.length - 1)) {
          start = 0;
        } else {
          start++;
        }
      }
    }
  }

  private static <S extends Solution<?>> void moreThanTwoObjectivesCase(
      List<S> solutionList, List<S> resultSolutionList, int newSolutionListSize) {

    Distance<S, List<S>> distance =
        new EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace<>();

    int randomIndex = JMetalRandom.getInstance().nextInt(0, solutionList.size() - 1);

    // create a list containing all the solutions but the selected one (only references to them)
    List<S> candidate;
    resultSolutionList.add(solutionList.get(randomIndex));

      candidate = IntStream.range(0, solutionList.size()).filter(i -> i != randomIndex).mapToObj(solutionList::get).collect(Collectors.toList());

    while (resultSolutionList.size() < newSolutionListSize) {
      int index = 0;
      S selected = candidate.get(0); // it should be a next! (n <= population size!)
      double aux = distance.compute(selected, solutionList);
      int i = 1;
      while (i < candidate.size()) {
        S nextCandidate = candidate.get(i);
        double distanceValue = distance.compute(nextCandidate, solutionList);
        if (aux < distanceValue) {
          index = i;
          aux = distanceValue;
        }
        i++;
      }

      // add the selected to res and remove from candidate list
      S removedSolution = candidate.remove(index);
      @SuppressWarnings("unchecked")
      S copy = (S) removedSolution.copy();
      resultSolutionList.add(copy);
    }
  }

  private static <S extends Solution<?>> double scalarizingFitnessFunction(
      S currentBest, double[] lambda, IdealPoint idealPoint) {

    double maxFun = -1.0e+30;

    for (int n = 0; n < idealPoint.getDimension(); n++) {
      double diff = Math.abs(currentBest.objectives()[n] - idealPoint.getValue(n));

      double functionValue;
      if (lambda[n] == 0) {
        functionValue = 0.0001 * diff;
      } else {
        functionValue = diff * lambda[n];
      }
      if (functionValue > maxFun) {
        maxFun = functionValue;
      }
    }

    return maxFun;
  }
}
