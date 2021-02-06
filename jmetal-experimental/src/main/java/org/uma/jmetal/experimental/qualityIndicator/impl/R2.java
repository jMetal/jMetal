package org.uma.jmetal.experimental.qualityIndicator.impl;

import org.uma.jmetal.experimental.qualityIndicator.QualityIndicator;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.errorchecking.Check;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * TODO: Add comments here
 */
@SuppressWarnings("serial")
public class R2 extends QualityIndicator {
  private final double[][] lambda;

  /**
   * Creates a new instance of the R2 indicator for a problem with
   * two objectives and 100 lambda vectors
   */
  public R2(double[][] referenceFront) {
    // by default it creates an R2 indicator for a two dimensions problem and
    // uses only 100 weight vectors for the R2 computation
    this(100, referenceFront);
  }


  /**
   * Creates a new instance of the R2 indicator for a problem with
   * two objectives and 100 lambda vectors
   */
  public R2() {
    // by default it creates an R2 indicator for a two dimensions problem and
    // uses only 100 weight vectors for the R2 computation
    this(100);
  }

  /**
   * Creates a new instance of the R2 indicator for a problem with
   * two objectives and N lambda vectors
   */
  public R2(int nVectors) {
    this(nVectors, null);
  }

  /**
   * Constructor
   * Creates a new instance of the R2 indicator for nDimensiosn
   * It loads the weight vectors from the file fileName
   */
  public R2(String file, double[][] referenceParetoFront) throws java.io.IOException {
    this(readWeightsFrom(file), referenceParetoFront);
  }

  /**
   * Creates a new instance of the R2 indicator for a problem with
   * two objectives and N lambda vectors
   */
  public R2(int nVectors, double[][] referenceParetoFront) {
    // by default it creates an R2 indicator for a two dimensions problem and
    // uses only <code>nVectors</code> weight vectors for the R2 computation
    this(generateWeights(nVectors), referenceParetoFront);
  }

  private R2(double[][] lambda, double[][] referenceFront) {
    // by default it creates an R2 indicator for a two dimensions problem and
    // uses only <code>nVectors</code> weight vectors for the R2 computation
    super(referenceFront);
    this.lambda = lambda;
  }

  private static double[][] generateWeights(int nVectors) {
    double[][] lambda = new double[nVectors][2];
    for (int n = 0; n < nVectors; n++) {
      double a = 1.0 * n / (nVectors - 1);
      lambda[n][0] = a;
      lambda[n][1] = 1 - a;
    }
    return lambda;
  }

  private static double[][] readWeightsFrom(String file) throws java.io.IOException {
    FileInputStream fis = new FileInputStream(file);
    InputStreamReader isr = new InputStreamReader(fis);
    try (BufferedReader br = new BufferedReader(isr)) {

      String line = br.readLine();
      double[][] lambda;
      if (line == null) {
        lambda = null;
      } else {
        int numberOfObjectives = (new StringTokenizer(line)).countTokens();
        int numberOfVectors = (int) br.lines().count();

        lambda = new double[numberOfVectors][numberOfObjectives];

        int index = 0;
        while (line != null) {
          StringTokenizer st = new StringTokenizer(line);
          for (int i = 0; i < numberOfObjectives; i++)
            lambda[index][i] = new Double(st.nextToken());
          index++;
          line = br.readLine();
        }
      }
      return lambda;
    }
  }


  /**
   * Constructor
   * Creates a new instance of the R2 indicator for nDimensiosn
   * It loads the weight vectors from the file fileName
   */
  public R2(String file) throws java.io.IOException {
    this(file, null);
  }

  @Override
  public double compute(double[][] front) {
    Check.notNull(front);
    return r2(front);
  }

  @Override
  public String getDescription() {
    return "R2 quality indicator";
  }

  @Override
  public String getName() {
    return "R2";
  }

  public double r2(double[][] front) {
    if (this.referenceFront != null) {
      // STEP 1. Obtain the maximum and minimum values of the Pareto front
      double[] minimumValues = NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront);
      double[] maximumValues = NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront);

      // STEP 2. Get the normalized front
      front = NormalizeUtils.normalize(front, minimumValues, maximumValues);
    }

    int numberOfObjectives = front[0].length;

    // STEP 3. compute all the matrix of Tschebyscheff values if it is null
    double[][] matrix = new double[front.length][lambda.length];
    for (int i = 0; i < front.length; i++) {
      for (int j = 0; j < lambda.length; j++) {
        matrix[i][j] = lambda[j][0] * Math.abs(front[i][0]);
        for (int n = 1; n < numberOfObjectives; n++) {
          matrix[i][j] = Math.max(matrix[i][j],
                  lambda[j][n] * Math.abs(front[i][n]));
        }
      }
    }

    double sum = 0.0;
    for (int i = 0; i < lambda.length; i++) {
      double tmp = matrix[0][i];
      for (int j = 1; j < front.length; j++) {
        tmp = Math.min(tmp, matrix[j][i]);
      }
      sum += tmp;
    }
    return sum / (double) lambda.length;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true;
  }
}
