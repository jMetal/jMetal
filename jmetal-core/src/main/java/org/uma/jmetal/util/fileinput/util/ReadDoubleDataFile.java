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

package org.uma.jmetal.util.fileinput.util;

import org.uma.jmetal.util.JMetalException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Utiliy class that reads a file containing a double value per line and returns an array with all of them.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ReadDoubleDataFile {

  public double[] readFile(String fileName) throws FileNotFoundException {
    InputStream inputStream = getClass().getResourceAsStream("/" + fileName);

    if (inputStream == null) {
      inputStream = new FileInputStream(fileName);
    }
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
