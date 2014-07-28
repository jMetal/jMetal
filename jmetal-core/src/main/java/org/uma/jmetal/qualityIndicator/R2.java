//  R2.java
//
//  Author:
//       Juan J. Durillo <juanjo.durillo@gmail.com>
//
//  Copyright (c) 2013 Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.qualityIndicator;

import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.qualityIndicator.util.MetricsUtil;
import org.uma.jmetal.util.Configuration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class R2 implements QualityIndicator {
  private static final String NAME = "R2" ;

  private MetricsUtil utils;
  double[][] matrix = null;
  double[][] lambda = null;
  int nObj = 0;

  /**
   * Constructor Creates a new instance of the R2 indicator for a problem with
   * two objectives and 100 lambda vectors
   */
  public R2() {
    utils = new MetricsUtil();

    // by default it creates an R2 indicator for a two dimensions probllem and
    // uses only 100 weight vectors for the R2 computation
    nObj = 2;
    // generating the weights
    lambda = new double[100][2];
    for (int n = 0; n < 100; n++) {
      double a = 1.0 * n / (100 - 1);
      lambda[n][0] = a;
      lambda[n][1] = 1 - a;
    }
  }

  /**
   * Constructor Creates a new instance of the R2 indicator for a problem with
   * two objectives and N lambda vectors
   */
  public R2(int nVectors) {
    utils = new org.uma.jmetal.qualityIndicator.util.MetricsUtil();

    // by default it creates an R2 indicator for a two dimensions probllem and
    // uses only <code>nVectors</code> weight vectors for the R2 computation
    nObj = 2;
    // generating the weights
    lambda = new double[nVectors][2];
    for (int n = 0; n < nVectors; n++) {
      double a = 1.0 * n / (nVectors - 1);
      lambda[n][0] = a;
      lambda[n][1] = 1 - a;
    }
  } // R2

  /**
   * Constructor Creates a new instance of the R2 indicator for nDimensiosn It
   * loads the weight vectors from the file fileName
   */
  public R2(int nObj, String file) {
    utils = new org.uma.jmetal.qualityIndicator.util.MetricsUtil();
    // A file is indicated, the weights are taken from there

    // by default it creates an R2 indicator for a two dimensions probllem and
    // uses only <code>nVectors</code> weight vectors for the R2 computation
    this.nObj = nObj;
    // generating the weights

    // reading weights
    try {
      // Open the file
      FileInputStream fis = new FileInputStream(file);
      InputStreamReader isr = new InputStreamReader(fis);
      BufferedReader br = new BufferedReader(isr);

      int numberOfObjectives = 0;
      int i = 0;
      int j = 0;
      String aux = br.readLine();
      LinkedList<double[]> list = new LinkedList<double[]>();
      while (aux != null) {
        StringTokenizer st = new StringTokenizer(aux);
        j = 0;
        numberOfObjectives = st.countTokens();
        double[] vector = new double[nObj];
        while (st.hasMoreTokens()) {
          double value = new Double(st.nextToken());
          vector[j++] = value;
        }
        list.add(vector);
        aux = br.readLine();
      }
      br.close();

      // convert the LinkedList into a vector
      lambda = new double[list.size()][];
      int index = 0;
      for (double[] aList : list) {
        lambda[index++] = aList;
      }
    } catch (Exception e) {
      Configuration.logger.log(Level.SEVERE,
        "initializeUniformWeight: failed when reading for file: " + file, e);
    }
  }

  /**
   * Returns the R2 indicator value of a given front
   */
  public double R2Without(double[][] approximation, double[][] paretoFront,
    int index) {

    /**
     * Stores the maximum values of true Pareto front.
     */
    double[] maximumValue;

    /**
     * Stores the minimum values of the true Pareto front.
     */
    double[] minimumValue;

    /**
     * Stores the normalized front.
     */
    double[][] normalizedApproximation;

    /**
     * Stores the normalized true Pareto front.
     */
    double[][] normalizedParetoFront;

    // STEP 1. Obtain the maximum and minimum values of the Pareto front
    maximumValue = utils.getMaximumValues(paretoFront, nObj);
    minimumValue = utils.getMinimumValues(paretoFront, nObj);

    // STEP 2. Get the normalized front and true Pareto fronts
    normalizedApproximation = utils.getNormalizedFront(approximation,
      maximumValue, minimumValue);
    normalizedParetoFront = utils.getNormalizedFront(paretoFront,
      maximumValue, minimumValue);

    // STEP 3. compute all the matrix of tchebicheff values if it is null
    matrix = new double[approximation.length][lambda.length];
    for (int i = 0; i < approximation.length; i++) {
      for (int j = 0; j < lambda.length; j++) {
        matrix[i][j] = lambda[j][0] * Math.abs(normalizedApproximation[i][0]);
        for (int n = 1; n < nObj; n++) {
          matrix[i][j] = Math.max(matrix[i][j],
            lambda[j][n] * Math.abs(normalizedApproximation[i][n]));
        }
      }
    }

    // STEP45. Compute the R2 value withouth the point
    double sumWithout = 0.0;
    for (int i = 0; i < lambda.length; i++) {
      double tmp;
      if (index != 0) {
        tmp = matrix[0][i];
      } else {
        tmp = matrix[1][i];
      }
      for (int j = 0; j < approximation.length; j++) {
        if (j != index) {
          tmp = Math.min(tmp, matrix[j][i]);
        }
      }
      sumWithout += tmp;
    }

    // STEP 5. Return the R2 value
    return sumWithout / (double) lambda.length;
  }

  /**
   * Returns the element contributing the most to the R2 indicator
   */
  public int getBest(double[][] approximation, double[][] paretoFront) {
    int indexBest = -1;
    double value = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < approximation.length; i++) {
      double aux = this.R2Without(approximation, paretoFront, i);
      if (aux > value) {
        indexBest = i;
        value = aux;
      }
    }

    return indexBest;
  }

  /**
   * Returns the element contributing the less to the R2
   */
  public int getWorst(double[][] approximation, double[][] paretoFront) {
    int indexWorst = -1;
    double value = Double.POSITIVE_INFINITY;

    for (int i = 0; i < approximation.length; i++) {
      double aux = this.R2Without(approximation, paretoFront, i);
      if (aux < value) {
        indexWorst = i;
        value = aux;
      }
    }

    return indexWorst;
  }

  /**
   * Returns the element contributing the most to the R2
   */
  public int getBest(SolutionSet set) {
    double[][] approximationFront = set.writeObjectivesToMatrix();
    double[][] trueFront = set.writeObjectivesToMatrix();

    return this.getBest(approximationFront, trueFront);
  }

  /**
   * Returns the element contributing the less to the R2
   */
  public int getWorst(SolutionSet set) {
    double[][] approximationFront = set.writeObjectivesToMatrix();
    double[][] trueFront = set.writeObjectivesToMatrix();

    return this.getWorst(approximationFront, trueFront);
  }

  /**
   * Returns the element contributing the most to the R2 indicator
   */
  public int[] getNBest(double[][] approximation, double[][] paretoFront, int N) {
    int[] indexBests = new int[approximation.length];
    double[] values = new double[approximation.length];

    for (int i = 0; i < approximation.length; i++) {
      values[i] = this.R2Without(approximation, paretoFront, i);
      indexBests[i] = i;
    }

    // sorting the values and index_bests
    for (int i = 0; i < approximation.length; i++) {
      for (int j = i; j < approximation.length; j++) {
        if (values[j] < values[i]) {
          double aux = values[j];
          values[j] = values[i];
          values[i] = aux;

          int aux_index = indexBests[j];
          indexBests[j] = indexBests[i];
          indexBests[i] = aux_index;
        }
      }
    }

    int[] res = new int[N];
    System.arraycopy(indexBests, 0, res, 0, N);

    return res;
  }

  /**
   * Returns the indexes of the N best solutions according to this indicator
   */
  public int[] getNBest(SolutionSet set, int n) {
    double[][] approximationFront = set.writeObjectivesToMatrix();
    double[][] trueFront = set.writeObjectivesToMatrix();

    return this.getNBest(approximationFront, trueFront, n);
  }

  /**
   * Returns the R2 indicator value of a given front
   *
   */
  public double r2(double[][] approximation, double[][] paretoFront) {
    double[] maximumValue;
    double[] minimumValue;
    double[][] normalizedApproximation;
    double[][] normalizedParetoFront;

    int numberOfObjectives = approximation[0].length ;

    // STEP 1. Obtain the maximum and minimum values of the Pareto front
    maximumValue = utils.getMaximumValues(paretoFront, numberOfObjectives);
    minimumValue = utils.getMinimumValues(paretoFront, numberOfObjectives);

    // STEP 2. Get the normalized front and true Pareto fronts
    normalizedApproximation = utils.getNormalizedFront(approximation,
      maximumValue, minimumValue);
    normalizedParetoFront = utils.getNormalizedFront(paretoFront,
      maximumValue, minimumValue);

    // STEP 3. compute all the matrix of tchebicheff values if it is null
    matrix = new double[approximation.length][lambda.length];
    for (int i = 0; i < approximation.length; i++) {
      for (int j = 0; j < lambda.length; j++) {
        matrix[i][j] = lambda[j][0] * Math.abs(normalizedApproximation[i][0]);
        for (int n = 1; n < numberOfObjectives; n++) {
          matrix[i][j] = Math.max(matrix[i][j],
            lambda[j][n] * Math.abs(normalizedApproximation[i][n]));
        }
      }
    }

    // STEP 4. The matrix is not null. Compute the R2 value
    double sum = 0.0;
    for (int i = 0; i < lambda.length; i++) {
      double tmp = matrix[0][i];
      for (int j = 1; j < approximation.length; j++) {
        tmp = Math.min(tmp, matrix[j][i]);
      }
      sum += tmp;
    }

    // STEP 5. Return the R2 value
    return sum / (double) lambda.length;
  }

  /**
   * Returns the R2 indicator of a given population, using as a reference point
   * 0, 0. Normalization is using taking into account the population itself
   */
  public double r2(SolutionSet set) {
    double[][] approximationFront = set.writeObjectivesToMatrix();
    double[][] trueFront = set.writeObjectivesToMatrix();

    return this.r2(approximationFront, trueFront);
  }

  /**
   * Returns the R2 indicator value of a given front
   */
  public double R2Without(SolutionSet set, int index) {
    double[][] approximationFront = set.writeObjectivesToMatrix();
    double[][] trueFront = set.writeObjectivesToMatrix();
    return this.r2(approximationFront, trueFront);
  }

  @Override
  public double execute(double[][] paretoFrontApproximation, double[][] paretoTrueFront) {
    return r2(paretoFrontApproximation, paretoTrueFront);
  }

  @Override public String getName() {
    return NAME;
  }
}
