package org.uma.jmetal.algorithm.multiobjective.moead.util;

import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Utilities methods to used by MOEA/D
 */
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
    double sum = 0;
    for (int n = 0; n < dim; n++) {
      sum += (vector1[n] - vector2[n]) * (vector1[n] - vector2[n]);
    }
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
    JMetalRandom randomGenerator = JMetalRandom.getInstance() ;
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
}
