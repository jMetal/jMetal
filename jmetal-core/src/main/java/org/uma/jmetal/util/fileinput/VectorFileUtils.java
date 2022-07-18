package org.uma.jmetal.util.fileinput;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * created at 3:49 pm, 2019/1/29 the common util to read reference vectors/reference points/uniform
 * weight vectors from file
 *
 * Modified by Antonio J. Nebro on 8/03/2019
 *
 * @author sunhaoran
 */
public class VectorFileUtils {
  /**
   * @param filePath the file need to read
   * @return referenceVectors. referenceVectors[i][j] means the i-th vector's j-th value
   * @throws JMetalException if error while read file
   */
  public static double[][] readVectors(String filePath) {
    var path = filePath ;

    var url = VectorFileUtils.class.getClassLoader().getResource(filePath);
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
    var referenceVectors = new double[vectorStrList.size()][];
    for (var i = 0; i < vectorStrList.size(); i++) {
      var vectorStr = vectorStrList.get(i);
      var objectArray = vectorStr.split("\\s+");
      referenceVectors[i] = new double[objectArray.length];
      for (var j = 0; j < objectArray.length; j++) {
        referenceVectors[i][j] = Double.parseDouble(objectArray[j]);
      }
    }

    return referenceVectors;
  }
}
