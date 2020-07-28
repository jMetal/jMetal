package org.uma.jmetal.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class VectorUtils {
  /**
   * Method that apply a dominance test between two vectors. It is assumed that the vectors have
   * been properly tested before calling this method to ensure that they have the same length
   *
   * @param vector1
   * @param vector2
   * @return 0 if the vectors are non-dominated, -1 if vector1 dominates vector2, and 1 if vector2
   *     dominates vector 1
   */
  public static int dominanceTest(double[] vector1, double[] vector2) {
    int bestIsOne = 0;
    int bestIsTwo = 0;
    int result;
    for (int i = 0; i < vector1.length; i++) {
      double value1 = vector1[i];
      double value2 = vector2[i];
      if (value1 != value2) {
        if (value1 < value2) {
          bestIsOne = 1;
        }
        if (value2 < value1) {
          bestIsTwo = 1;
        }
      }
    }
    result = Integer.compare(bestIsTwo, bestIsOne);
    return result;
  }

  /**
   * @param filePath the file need to read
   * @return referenceVectors. referenceVectors[i][j] means the i-th vector's j-th value
   * @throws JMetalException if error while read file
   */
  public static double[][] readVectors(String filePath) {
    double[][] referenceVectors;
    String path = filePath ;

    URL url = VectorUtils.class.getClassLoader().getResource(filePath);
    if (url != null) {
      try {
        path = Paths.get(url.toURI()).toString();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }

    List<String> vectorStrList = null;
    try {
      vectorStrList = Files.readAllLines(Paths.get(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
    referenceVectors = new double[vectorStrList.size()][];
    for (int i = 0; i < vectorStrList.size(); i++) {
      String vectorStr = vectorStrList.get(i);
      String[] objectArray = vectorStr.split("\\s+");
      referenceVectors[i] = new double[objectArray.length];
      for (int j = 0; j < objectArray.length; j++) {
        referenceVectors[i][j] = Double.parseDouble(objectArray[j]);
      }
    }

    return referenceVectors;
  }
}
