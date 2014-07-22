//  Front.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro, Juan J. Durillo
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
//

//  CREDIT
//  This class is based on the code of the WFG group (http://www.wfg.csse.uwa.edu.au/hypervolume/)
//  Copyright (C) 2010 Lyndon While, Lucas Bradstreet.

package org.uma.jmetal.qualityIndicator.fastHypervolume.wfg;

import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.Configuration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 25/07/13
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class Front {
  public Point[] points;
  public int nPoints;
  private int numberOfPoints;
  private int dimension;
  private boolean maximizing;
  private Comparator<Point> pointComparator;

  public Front() {
    maximizing = true;
    pointComparator = new PointComparator(maximizing);
  }

  public Front(int numberOfPoints, int dimension, SolutionSet solutionSet) {
    maximizing = true;
    pointComparator = new PointComparator(maximizing);
    this.numberOfPoints = numberOfPoints;
    this.dimension = dimension;
    nPoints = this.numberOfPoints;

    points = new Point[this.numberOfPoints];
    for (int i = 0; i < this.numberOfPoints; i++) {
      double[] p = new double[dimension];
      for (int j = 0; j < dimension; j++) {
        p[j] = solutionSet.get(i).getObjective(j);
      }
      points[i] = new Point(p);
    }
  }

  public Front(int numberOfPoints, int dimension) {
    maximizing = true;
    pointComparator = new PointComparator(maximizing);
    this.numberOfPoints = numberOfPoints;
    this.dimension = dimension;
    nPoints = this.numberOfPoints;

    points = new Point[this.numberOfPoints];
    for (int i = 0; i < this.numberOfPoints; i++) {
      double[] p = new double[dimension];
      for (int j = 0; j < dimension; j++) {
        p[j] = 0.0;
      }
      points[i] = new Point(p);
    }
  }

  public Front(int numberOfPoints, int dimension, List<double[]> listOfPoints) {
    maximizing = true;
    pointComparator = new PointComparator(maximizing);
    this.numberOfPoints = numberOfPoints;
    this.dimension = dimension;

    points = new Point[this.numberOfPoints];
    for (int i = 0; i < this.numberOfPoints; i++) {
      points[i] = new Point(listOfPoints.get(i));
    }
  }


  public void readFront(String fileName) throws IOException {
    FileInputStream fis = new FileInputStream(fileName);
    InputStreamReader isr = new InputStreamReader(fis);
    BufferedReader br = new BufferedReader(isr);

    List<double[]> list = new ArrayList<double[]>();
    int numberOfObjectives = 0;
    String aux = br.readLine();
    while (aux != null) {
      StringTokenizer st = new StringTokenizer(aux);
      int i = 0;
      numberOfObjectives = st.countTokens();

      double[] vector = new double[st.countTokens()];
      while (st.hasMoreTokens()) {
        double value = new Double(st.nextToken());
        vector[i] = value;
        i++;
      }
      list.add(vector);
      aux = br.readLine();
    }
    br.close();
    numberOfPoints = list.size();
    dimension = numberOfObjectives;
    points = new Point[numberOfPoints];
    nPoints = numberOfPoints;
    for (int i = 0; i < numberOfPoints; i++) {
      points[i] = new Point(list.get(i));
    }
  }


  public void loadFront(SolutionSet solutionSet, int notLoadingIndex) {

    if (notLoadingIndex >= 0 && notLoadingIndex < solutionSet.size()) {
      numberOfPoints = solutionSet.size() - 1;
    } else {
      numberOfPoints = solutionSet.size();
    }

    nPoints = numberOfPoints;
    dimension = solutionSet.get(0).getNumberOfObjectives();

    points = new Point[numberOfPoints];

    int index = 0;
    for (int i = 0; i < solutionSet.size(); i++) {
      if (i != notLoadingIndex) {
        double[] vector = new double[dimension];
        for (int j = 0; j < dimension; j++) {
          vector[j] = solutionSet.get(i).getObjective(j);
        }
        points[index++] = new Point(vector);
      }
    }
  }

  public void printFront() {
    Configuration.logger.info("Objectives:       " + dimension);
    Configuration.logger.info("Number of points: " + numberOfPoints);

    for (Point point : points) {
      Configuration.logger.info(""+point);
    }
  }

  public int getNumberOfObjectives() {
    return dimension;
  }

  public int getNumberOfPoints() {
    return numberOfPoints;
  }

  public Point getPoint(int index) {
    return points[index];
  }

  public Point[] getPoints() {
    return points;
  }

  public void setToMazimize() {
    maximizing = true;
    pointComparator = new PointComparator(maximizing);
  }

  public void setToMinimize() {
    maximizing = false;
    pointComparator = new PointComparator(maximizing);
  }

  public void sort() {
    Arrays.sort(points, pointComparator);
  }

  public Point getReferencePoint() {
    Point referencePoint = new Point(dimension);

    double[] maxObjectives = new double[numberOfPoints];
    for (int i = 0; i < numberOfPoints; i++) {
      maxObjectives[i] = 0;
    }

    for (Point aPoints_ : points) {
      for (int j = 0; j < dimension; j++) {
        if (maxObjectives[j] < aPoints_.objectives_[j]) {
          maxObjectives[j] = aPoints_.objectives_[j];
        }
      }
    }

    System.arraycopy(maxObjectives, 0, referencePoint.objectives_, 0, dimension);

    return referencePoint;
  }
}
