package org.uma.jmetal.util.point.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Class representing a point (i.e, an array of double values)
 *
 * @author Antonio J. Nebro
 */
public class ArrayPoint implements Point {
  protected double[] point;


  /**
   * Default constructor
   */
  public ArrayPoint() {
    point = null ;
  }

  /**
   * Constructor
   *
   * @param dimensions Dimensions of the point
   */
  public ArrayPoint(int dimensions) {
    point = new double[dimensions];

    for (int i = 0; i < dimensions; i++) {
      point[i] = 0.0;
    }
  }

  /**
   * Copy constructor
   *
   * @param point
   */
  public ArrayPoint(Point point) {
    if (point == null) {
      throw new JMetalException("The point is null") ;
    }

    this.point = new double[point.getNumberOfDimensions()];

    for (int i = 0; i < point.getNumberOfDimensions(); i++) {
      this.point[i] = point.getDimensionValue(i);
    }
  }

  /**
   * Constructor from a solution
   *
   * @param solution
   */
  public ArrayPoint(Solution<?> solution) {
    if (solution == null) {
      throw new JMetalException("The solution is null") ;
    }

    int dimensions = solution.getNumberOfObjectives();
    point = new double[dimensions];

    for (int i = 0; i < dimensions; i++) {
      point[i] = solution.getObjective(i);
    }
  }

  /**
   * Constructor from an array of double values
   *
   * @param point
   */
  public ArrayPoint(double[] point) {
    if (point == null) {
      throw new JMetalException("The array of values is null") ;
    }

    this.point = new double[point.length];
    System.arraycopy(point, 0, this.point, 0, point.length);
  }

  /**
   * Constructor reading the values from a file
   * @param fileName
   */
  public ArrayPoint(String fileName) throws IOException {
    FileInputStream fis = new FileInputStream(fileName);
    InputStreamReader isr = new InputStreamReader(fis);
    BufferedReader br = new BufferedReader(isr);

    List<Double> auxiliarPoint = new ArrayList<Double>();
    String aux = br.readLine();
    while (aux != null) {
      StringTokenizer st = new StringTokenizer(aux);

      while (st.hasMoreTokens()) {
        Double value = (new Double(st.nextToken()));
        auxiliarPoint.add(value);
      }
      aux = br.readLine();
    }

    point = new double[auxiliarPoint.size()] ;
    for (int i = 0; i < auxiliarPoint.size(); i++) {
      point[i] = auxiliarPoint.get(i) ;
    }

    br.close();
  }

  @Override
  public int getNumberOfDimensions() {
    return point.length;
  }

  @Override
  public double[] getValues() {
    return point;
  }

  @Override
  public double getDimensionValue(int index) {
    if ((index < 0) || (index >= point.length)) {
      throw new JMetalException("Index value invalid: " + index +
          ". The point length is: " + point.length) ;
    }
    return point[index] ;
  }

  @Override
  public void setDimensionValue(int index, double value) {
    if ((index < 0) || (index >= point.length)) {
      throw new JMetalException("Index value invalid: " + index +
          ". The point length is: " + point.length) ;
    }
    point[index] = value ;
  }

  @Override
  public String toString() {
    String result = "";
    for (double anObjectives_ : point) {
      result += anObjectives_ + " ";
    }

    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    ArrayPoint that = (ArrayPoint) o;

    if (!Arrays.equals(point, that.point))
      return false;

    return true;
  }

  @Override public int hashCode() {
    return Arrays.hashCode(point);
  }
}
