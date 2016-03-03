package org.uma.jmetal.algorithm.multiobjective.wasfga.util;

import org.uma.jmetal.util.JMetalException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author Rubén Saborido Infantes
 * This class offers different methods to manipulate weight vectors.
 */
public class WeightVector {
  public enum NORMALIZE {TRUE, FALSE} ;

  /**
   * Generate uniform weight vectors in two dimension
   * @param epsilon Distance between each component of the weight vector
   * @param numberOfWeights Number of weight vectors to generate
   * @return A set of weight vectors
   */
  public static double[][] initUniformWeights2D(double epsilon, int numberOfWeights) {
    double[][] weights = new double[numberOfWeights][2];

    int indexOfWeight;
    double w, jump;

    jump = (1-(2*epsilon))/(numberOfWeights-1);
    indexOfWeight = 0;

    w = epsilon;

    //while(w <= (1-epsilon))
    while (indexOfWeight < numberOfWeights)
    {
      weights[indexOfWeight][0] = w;
      weights[indexOfWeight][1] = 1-w;

      w = w + jump;

      indexOfWeight = indexOfWeight+1;
    }

    return weights;
  }

  /**
   * Read a set of weight vector from a file
   * @param filePath A file containing the weight vectors
   * @return A set of weight vectors
   */
  public static double[][] getWeightsFromFile(String filePath) {
    double[][] weights = new double[0][0];

    Vector<double[]> listOfWeights = new Vector<double[]>();

    try {
      // Open the file
      FileInputStream fis = new FileInputStream(filePath);
      InputStreamReader isr = new InputStreamReader(fis);
      BufferedReader br = new BufferedReader(isr);

      int numberOfObjectives = 0;
      int i = 0;
      int j = 0;
      String aux = br.readLine();
      while (aux != null) {
        StringTokenizer st = new StringTokenizer(aux);
        j = 0;
        numberOfObjectives = st.countTokens();
        double[] weight = new double[numberOfObjectives];

        while (st.hasMoreTokens()) {
          weight[j] = (new Double(st.nextToken())).doubleValue();
          j++;
        }

        listOfWeights.add(weight);
        aux = br.readLine();
        i++;
      }
      br.close();

      weights = new double[listOfWeights.size()][numberOfObjectives];
      for (int indexWeight=0; indexWeight<listOfWeights.size(); indexWeight++)
      {
        for (int indexOfObjective=0; indexOfObjective<numberOfObjectives; indexOfObjective++)
        {
          weights[indexWeight][indexOfObjective] = listOfWeights.get(indexWeight)[indexOfObjective];
        }
      }
    } catch (Exception e) {
      throw new JMetalException("getWeightsFromFile: failed when reading for file: " + filePath) ;
    }

    return weights;
  }

  /**
   * Calculate the inverse of a set of weight vectors
   * @param weights A set of weight vectors
   * @param b True if the weights should be normalize by the sum of the components
   * @return A set of weight vectors
   */
  public static double[][] invertWeights(double[][] weights, boolean b) {
    double[][] result = new double[weights.length][weights[0].length];

    for (int indexOfWeight = 0; indexOfWeight < weights.length; indexOfWeight++) {
      if (b) {
        double sum = 0;

        for (int indexOfComponent = 0; indexOfComponent < weights[indexOfWeight].length; indexOfComponent++) {
          sum = sum + (1.0/weights[indexOfWeight][indexOfComponent]);
        }

        for (int indexOfComponent = 0; indexOfComponent < weights[indexOfWeight].length; indexOfComponent++) {
          result[indexOfWeight][indexOfComponent] = (1.0 / weights[indexOfWeight][indexOfComponent]) / sum;
        }
      }
      else {
        for (int indexOfComponent = 0; indexOfComponent < weights[indexOfWeight].length; indexOfComponent++)
          result[indexOfWeight][indexOfComponent] = 1.0/weights[indexOfWeight][indexOfComponent];
      }
    }

    return result;
  }
}
