package org.uma.jmetal.util.neighborhood.impl;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.neighborhood.Neighborhood;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class implements a neighborhood based on the weight vectors of MOEA/D
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class WeightVectorNeighborhood<S> implements Neighborhood<S> {
  private int numberOfWeightVectors;
  private int weightVectorSize;
  private int[][] neighborhood;
  private double[][] weightVector;
  private int neighborSize;

  public WeightVectorNeighborhood(int numberOfWeightVectors, int neighborSize) {
    this.numberOfWeightVectors = numberOfWeightVectors;
    this.weightVectorSize = 2;
    this.neighborSize = neighborSize;

    this.neighborhood = new int[numberOfWeightVectors][neighborSize];
    this.weightVector = new double[numberOfWeightVectors][weightVectorSize];

    for (int n = 0; n < numberOfWeightVectors; n++) {
      double a = 1.0 * n / (numberOfWeightVectors - 1);
      weightVector[n][0] = a;
      weightVector[n][1] = 1 - a;
    }

    initializeNeighborhood();
  }

  public WeightVectorNeighborhood(int numberOfWeightVectors, int weightVectorSize, int neighborSize, String vectorFileName) throws FileNotFoundException {
    this.numberOfWeightVectors = numberOfWeightVectors;
    this.weightVectorSize = weightVectorSize;
    this.neighborSize = neighborSize;

    this.neighborhood = new int[numberOfWeightVectors][neighborSize];
    this.weightVector = new double[numberOfWeightVectors][weightVectorSize];

    readWeightsFromFile(vectorFileName);

    initializeNeighborhood();
  }

  private void readWeightsFromFile(String vectorFileName) throws FileNotFoundException {
    //try {
    InputStream inputStream;
    inputStream = getClass().getResourceAsStream(vectorFileName);
    if (null == inputStream) {
      inputStream = new FileInputStream(vectorFileName);
    }
    InputStreamReader isr = new InputStreamReader(inputStream);

    try(BufferedReader br = new BufferedReader(isr)) {
      int i = 0;
      int j;
      String aux = br.readLine();
      while (aux != null) {
        StringTokenizer st = new StringTokenizer(aux);
        j = 0;
        while (st.hasMoreTokens()) {
          double value = new Double(st.nextToken());
          weightVector[i][j] = value;
          j++;
        }
        aux = br.readLine();
        i++;
      }
    } catch (IOException e) {
      throw new JMetalException("readWeightsFromFile: failed when reading for file: "
              + vectorFileName, e);
    }

  }

  private void initializeNeighborhood() {
    EuclideanDistance euclideanDistance = new EuclideanDistance();
    double[] x = new double[numberOfWeightVectors];
    int[] idx = new int[numberOfWeightVectors];

    for (int i = 0; i < numberOfWeightVectors; i++) {
      // calculate the distances based on weight vectors
      for (int j = 0; j < numberOfWeightVectors; j++) {
        x[j] = euclideanDistance.compute(weightVector[i], weightVector[j]);
        idx[j] = j;
      }

      // find 'niche' nearest neighboring subproblems
      minFastSort(x, idx, numberOfWeightVectors, neighborSize);

      System.arraycopy(idx, 0, neighborhood[i], 0, neighborSize);
    }
  }

  private void minFastSort(double x[], int idx[], int n, int m) {
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

  @Override
  public List<S> getNeighbors(List<S> solutionList, int solutionIndex) {
    List<S> neighbourSolutions = new ArrayList<>();

    for (int neighborIndex : neighborhood[solutionIndex]) {
      neighbourSolutions.add(solutionList.get(neighborIndex));
    }
    return neighbourSolutions;
  }

  public int getNumberOfWeightVectors() {
    return numberOfWeightVectors;
  }

  public int getWeightVectorSize() {
    return weightVectorSize;
  }

  public int[][] getNeighborhood() {
    return neighborhood;
  }

  public double[][] getWeightVector() {
    return weightVector;
  }

  public int getNeighborSize() {
    return neighborSize;
  }
}
