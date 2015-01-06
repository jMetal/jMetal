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
//  This class is based on the code of the wfg group (http://www.wfg.csse.uwa.edu.au/hypervolume/)
//  Copyright (C) 2010 Lyndon While, Lucas Bradstreet.

package org.uma.jmetal.util.front.imp;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import java.io.*;
import java.util.*;

/**
 * A front is a list of points
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class ArrayFront implements Front {
  private Point[] points;
  private int numberOfPoints ;
  private int pointDimensions ;

  /** Constructor */
  public ArrayFront() {
    points = null ;
  }

  /** Constructor */
  public ArrayFront(List<Solution<?>> solutionList) {
    numberOfPoints = solutionList.size();
    pointDimensions = solutionList.get(0).getNumberOfObjectives() ;
    points = new Point[numberOfPoints] ;

    points = new Point[numberOfPoints];
    for (int i = 0; i < numberOfPoints; i++) {
      Point point = new ArrayPoint(pointDimensions) ;
      for (int j = 0; j < pointDimensions; j++) {
        point.setDimensionValue(j, solutionList.get(i).getObjective(j));
      }
      points[i] = point;
    }
  }

  /** Constructor */
  public ArrayFront(Front front) {
    numberOfPoints = front.getNumberOfPoints();
    pointDimensions = front.getPoint(0).getNumberOfDimensions() ;
    points = new Point[numberOfPoints] ;

    points = new Point[numberOfPoints];
    for (int i = 0; i < numberOfPoints; i++) {
      points[i] = new ArrayPoint(front.getPoint(i));
    }
  }

  /** Constructor */
  public ArrayFront(int numberOfPoints, int dimensions) {
    this.numberOfPoints = numberOfPoints;
    pointDimensions = dimensions ;
    points = new Point[this.numberOfPoints];

    for (int i = 0; i < this.numberOfPoints; i++) {
      Point point = new ArrayPoint(pointDimensions) ;
      for (int j = 0; j < pointDimensions; j++) {
        point.setDimensionValue(j, 0.0) ;
      }
      points[i] = point ;
    }
  }

  @Override public void readFrontFromFile(String fileName) {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(fileName);
    } catch (FileNotFoundException e) {
      throw new JMetalException("File " + fileName + " not found", e);
    }
    InputStreamReader isr = new InputStreamReader(fis);
    BufferedReader br = new BufferedReader(isr);

    List<Point> list = new ArrayList<>();
    int numberOfObjectives = 0;
    String aux ;
    try {
      aux = br.readLine();

      while (aux != null) {
        StringTokenizer tokenizer = new StringTokenizer(aux);
        int i = 0;
        numberOfObjectives = tokenizer.countTokens();

        Point point = new ArrayPoint(numberOfObjectives) ;
        while (tokenizer.hasMoreTokens()) {
          double value = new Double(tokenizer.nextToken());
          point.setDimensionValue(i, value);
          i++;
        }
        list.add(point);
        aux = br.readLine();
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    numberOfPoints = list.size();
    points = new Point[numberOfPoints];
    pointDimensions = points[0].getNumberOfDimensions() ;
    for (int i = 0; i < numberOfPoints; i++) {
      points[i] = list.get(i);
    }
  }

  @Override public void createFrontFromAListOfSolutions(List<Solution> solutionList) {
    numberOfPoints = solutionList.size() ;
    pointDimensions = solutionList.get(0).getNumberOfObjectives() ;

    points = new Point[numberOfPoints];
    for (int i = 0; i < numberOfPoints; i++) {
      for (int j = 0; j < pointDimensions; j++) {
        points[i].setDimensionValue(j, (Double) solutionList.get(i).getVariableValue(j));
      }
    }
  }

  @Override public int getNumberOfPoints() {
    return points.length;
  }

  @Override public Point getPoint(int index) {
    return points[index];
  }

  @Override public void sort(Comparator<Point> comparator) {
    Arrays.sort(points, comparator);
  }
}
