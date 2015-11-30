package org.uma.jmetal.util.experiment.component.util;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by ajnebro on 30/11/15.
 */
public class ReadDoubleDataFile {

  public static double[] readFile(String fileName) throws FileNotFoundException {
    InputStream inputStream = new FileInputStream(fileName);
    InputStreamReader isr = new InputStreamReader(inputStream);
    BufferedReader br = new BufferedReader(isr);

    List<Double> list = new ArrayList<>();
    String line ;
    try {
      line = br.readLine();

      while (line != null) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        while (tokenizer.hasMoreTokens()) {
          double value = new Double(tokenizer.nextToken());
          list.add(value) ;
        }
        line = br.readLine();
      }
      br.close();
    } catch (IOException e) {
      throw new JMetalException("Error reading file", e);
    } catch (NumberFormatException e) {
      throw new JMetalException("Format number exception when reading file", e);
    }

    double[] result = new double[list.size()] ;
    for (int i = 0 ; i < list.size() ; i++) {
      result[i] = list.get(i).doubleValue() ;
    }

    return result ;
  }
}
