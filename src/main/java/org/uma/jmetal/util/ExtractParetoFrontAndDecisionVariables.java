//  ExtractParetoFrontAndDecisionVariables.java
//
//  Author:
//       Shahriar Mahbub
//
//  Copyright (c) 2014 Shahriar Mahbub
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

package org.uma.jmetal.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class ExtractParetoFrontAndDecisionVariables {

  String FUNFileName_;
  String VARFileName_;

  int dimensions_;
  int numberOfDecisionVariables_;

  List<Point> points_ = new LinkedList<Point>();

  private class Point {
    double[] vector_;
    double[] decisionVector_;

    public Point(double[] vector, double[] decisionVector) {
      vector_ = vector;
      decisionVector_ = decisionVector;
    }
  }

  public ExtractParetoFrontAndDecisionVariables(
    String FUNFileName,
    String VARFileName,
    int dimensions,
    int numberOfDecisionVariables) {
    FUNFileName_ = FUNFileName;
    VARFileName_ = VARFileName;
    dimensions_ = dimensions;
    numberOfDecisionVariables_ = numberOfDecisionVariables;
    loadInstance();
  }

  /**
   * Read the points instance from file
   */
  public void loadInstance() {

    try {
      // read the objetives values
      File archive = new File(FUNFileName_);
      FileReader fr = null;
      BufferedReader br = null;
      fr = new FileReader(archive);
      br = new BufferedReader(fr);

      // read the corresponding decision variable
      File dcFile = new File(VARFileName_);
      FileReader dcfr = null;
      BufferedReader dcbr = null;
      dcfr = new FileReader(dcFile);
      dcbr = new BufferedReader(dcfr);

      // File reading
      String line;
      int lineCnt = 0;
      // reading the first line (special case)
      line = br.readLine();

      String lineDecicionVarible = dcbr.readLine();

      while (line != null) {
        double objectiveValues[] = new double[dimensions_];
        double decisionVariables[] = new double[numberOfDecisionVariables_];

        StringTokenizer stobj = new StringTokenizer(line);
        StringTokenizer stdec = new StringTokenizer(lineDecicionVarible);
        try {
          for (int i = 0; i < dimensions_; i++) {
            objectiveValues[i] = new Double(stobj.nextToken());
          }

          for (int i = 0; i < numberOfDecisionVariables_; i++) {
            decisionVariables[i] = new Double(stdec.nextToken());
          }

          Point auxPoint = new Point(objectiveValues,
            decisionVariables);
          add(auxPoint);

          line = br.readLine();
          lineDecicionVarible = dcbr.readLine();
          lineCnt++;
        } catch (NumberFormatException e) {
          Configuration.logger_.log(
            Level.SEVERE, "Number in a wrong format in line "
              + lineCnt);
          System.err.println(line);
          line = br.readLine();
          lineCnt++;
        } catch (NoSuchElementException e2) {
          Configuration.logger_.log(
            Level.SEVERE,"Line " + lineCnt
              + " does not have the right number of objectives");
          System.err.println(line);
          line = br.readLine();
          lineCnt++;
        }
      }
      br.close();
      dcbr.close();
    } catch (FileNotFoundException e3) {
      Configuration.logger_.log(
        Level.SEVERE,"The file " + FUNFileName_
          + " has not been found in your file system");
    } catch (IOException e3) {
      Configuration.logger_.log(
        Level.SEVERE,"The file " + FUNFileName_
          + " has not been found in your file system");
    }
  }

  public void add(Point point) {
    Iterator<Point> iterator = points_.iterator();

    while (iterator.hasNext()) {
      Point auxPoint = iterator.next();
      int flag = compare(point, auxPoint);

      if (flag == -1) { // A solution in the list is dominated by the new one
        iterator.remove();

      } else if (flag == 1) { // The solution is dominated
        return;
      }
    }
    points_.add(point);
  }

  public int compare(Point one, Point two) {
    int flag1 = 0, flag2 = 0;
    for (int i = 0; i < dimensions_; i++) {
      if (one.vector_[i] < two.vector_[i])
        flag1 = 1;

      if (one.vector_[i] > two.vector_[i])
        flag2 = 1;
    }
    // one dominates
    if (flag1 > flag2)
      return -1;
    // two dominates
    if (flag2 > flag1)
      return 1;
    // both are non dominated
    return 0;
  }

  public void writeParetoFront() {
    try {
/* Open the objective output file */
      FileOutputStream fosObj = new FileOutputStream(FUNFileName_ + ".pf");
      OutputStreamWriter oswObj = new OutputStreamWriter(fosObj);
      BufferedWriter bwObj = new BufferedWriter(oswObj);

/* Open the decision variables output file */
      FileOutputStream fosDC = new FileOutputStream(VARFileName_ + ".pf");
      OutputStreamWriter oswDC = new OutputStreamWriter(fosDC);
      BufferedWriter bwDC = new BufferedWriter(oswDC);

      for (Point auxPoint : points_) {
        String auxObj = "";
        String auxDC = "";

        for (int i = 0; i < auxPoint.vector_.length; i++) {
          auxObj += auxPoint.vector_[i] + " ";

        }
        bwObj.write(auxObj);
        bwObj.newLine();

        for (int i = 0; i < auxPoint.decisionVector_.length; i++) {
          auxDC += auxPoint.decisionVector_[i] + " ";
        }
        bwDC.write(auxDC);
        bwDC.newLine();
      }

      /* Close the file */
      bwObj.close();
      bwDC.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    if (args.length != 4) {
      Configuration.logger_.info("Wrong number of arguments: ");
      Configuration.logger_.info("Sintex: java ExtractParetoFront <FUNfile> <VARfile> <dimensions> <numberOfDecicionVariables>");
      Configuration.logger_.info("\t<FUNfile> is a file containing all objective values");
      Configuration.logger_.info("\t<VARfile> is a file containing all corresponding decision variable values");
      Configuration.logger_.info("\t<dimensions> represents the number of dimensions of the problem");
      Configuration.logger_.info("\t<numberOfDecicionVariables> represents the number of decision varibales of the problem");
      throw new JMetalException("");
    }

    ExtractParetoFrontAndDecisionVariables epf = new ExtractParetoFrontAndDecisionVariables(
      args[0], args[1], new Integer(args[2]), new Integer(args[3]));

    epf.writeParetoFront();
  }
}
