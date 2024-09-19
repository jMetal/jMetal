package org.uma.jmetal.util.point.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.point.Point;

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
   * @param dimension Dimension of the point
   */
  public ArrayPoint(int dimension) {
    point = new double[dimension];

    Arrays.fill(point, 0);
  }

  /**
   * Copy constructor
   *
   * @param point
   */
  public ArrayPoint(Point point) {
    Check.notNull(point);

    this.point = new double[point.dimension()];

    for (int i = 0; i < point.dimension(); i++) {
      this.point[i] = point.value(i);
    }
  }

  /**
   * Constructor from an array of double values
   *
   * @param point
   */
  public ArrayPoint(double[] point) {
    Check.notNull(point);

    this.point = new double[point.length];
    System.arraycopy(point, 0, this.point, 0, point.length);
  }

  @Override
  public int dimension() {
    return point.length;
  }

  @Override
  public double[] values() {
    return point;
  }

  @Override
  public double value(int index) {
    Check.that((index >= 0) && (index < point.length), "Index value invalid: " + index +
            ". The point length is: " + point.length);

    return point[index] ;
  }

  @Override
  public void value(int index, double value) {
    Check.that((index >= 0) && (index < point.length), "Index value invalid: " + index +
            ". The point length is: " + point.length);

    point[index] = value ;
  }

  @Override
  public void update(double[] point) {
    this.set(point);
  }

  @Override
  public void set(double[] point) {
    Check.that(point.length == this.point.length, "The point to be update have a dimension of " + point.length + " "
            + "while the parameter point has a dimension of " + point.length);

    System.arraycopy(point, 0, this.point, 0, point.length);
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

    return Arrays.equals(point, that.point);
  }

  @Override public int hashCode() {
    return Arrays.hashCode(point);
  }
}
