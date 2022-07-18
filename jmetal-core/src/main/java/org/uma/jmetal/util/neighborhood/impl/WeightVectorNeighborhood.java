package org.uma.jmetal.util.neighborhood.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.neighborhood.Neighborhood;

/**
 * This class implements a neighborhood based on the weight vectors of MOEA/D
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class WeightVectorNeighborhood<S extends Solution<?>> implements Neighborhood<S> {
  private int numberOfWeightVectors;
  private int weightVectorSize;
  private int[][] neighborhood;
  private double[][] weightVector;
  private int neighborhoodSize;

  public WeightVectorNeighborhood(int numberOfWeightVectors, int neighborhoodSize) {
    this.numberOfWeightVectors = numberOfWeightVectors;
    this.weightVectorSize = 2;
    this.neighborhoodSize = neighborhoodSize;

    this.neighborhood = new int[numberOfWeightVectors][neighborhoodSize];
    this.weightVector = new double[numberOfWeightVectors][weightVectorSize];

    for (int n = 0; n < numberOfWeightVectors; n++) {
      double a = 1.0 * n / (numberOfWeightVectors - 1);
      weightVector[n][0] = a;
      weightVector[n][1] = 1 - a;
    }

    initializeNeighborhood();
  }

  public WeightVectorNeighborhood(
      int numberOfWeightVectors, int weightVectorSize, int neighborhoodSize, String vectorDirectoryName)
      throws FileNotFoundException {
    this.numberOfWeightVectors = numberOfWeightVectors;
    this.weightVectorSize = weightVectorSize;
    this.neighborhoodSize = neighborhoodSize;



    this.neighborhood = new int[numberOfWeightVectors][neighborhoodSize];
    this.weightVector = new double[numberOfWeightVectors][weightVectorSize];

    String weightVectorFileName = vectorDirectoryName + "/W"+weightVectorSize+"D_" + numberOfWeightVectors +".dat" ;
    readWeightsFromFile(weightVectorFileName) ;

    initializeNeighborhood();
  }

  private void readWeightsFromFile(String vectorFileName) throws FileNotFoundException {
    InputStream inputStream;
    inputStream = getClass().getResourceAsStream(vectorFileName);
    if (null == inputStream) {
      inputStream = new FileInputStream(vectorFileName);
    }
    InputStreamReader isr = new InputStreamReader(inputStream);

    try (BufferedReader br = new BufferedReader(isr)) {
      int i = 0;
      int j;
      String aux = br.readLine();
      while (aux != null) {
        StringTokenizer st = new StringTokenizer(aux);
        j = 0;
        while (st.hasMoreTokens()) {
          double value = Double.valueOf(st.nextToken());
          weightVector[i][j] = value;
          j++;
        }
        aux = br.readLine();
        i++;
      }
    } catch (IOException e) {
      throw new JMetalException(
          "readWeightsFromFile: failed when reading for file: " + vectorFileName, e);
    }
  }

  private void initializeNeighborhood() {
    EuclideanDistance euclideanDistance = new EuclideanDistance();
    double[] x = new double[numberOfWeightVectors];
    int @NotNull [] idx = new int[numberOfWeightVectors];

    for (int i = 0; i < numberOfWeightVectors; i++) {
      // calculate the distances based on weight vectors
      for (int j = 0; j < numberOfWeightVectors; j++) {
        x[j] = euclideanDistance.compute(weightVector[i], weightVector[j]);
        idx[j] = j;
      }

      // find 'niche' nearest neighboring subproblems
      minFastSort(x, idx, numberOfWeightVectors, neighborhoodSize);

      System.arraycopy(idx, 0, neighborhood[i], 0, neighborhoodSize);
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
      for (int i : neighborhood[solutionIndex]) {
          S s = solutionList.get(i);
          neighbourSolutions.add(s);
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

  public int neighborhoodSize() {
    return neighborhoodSize;
  }
}
