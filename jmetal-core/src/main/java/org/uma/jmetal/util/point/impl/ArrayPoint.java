package org.uma.jmetal.util.point.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.jetbrains.annotations.NotNull;
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
  public ArrayPoint(@NotNull Point point) {
    Check.notNull(point);

    this.point = new double[point.getDimension()];

    for (var i = 0; i < point.getDimension(); i++) {
      this.point[i] = point.getValue(i);
    }
  }

  /**
   * Constructor from an array of double values
   *
   * @param point
   */
  public ArrayPoint(double @NotNull [] point) {
    Check.notNull(point);

    this.point = new double[point.length];
    System.arraycopy(point, 0, this.point, 0, point.length);
  }

  /**
   * Constructor reading the values from a file
   * @param fileName
   */
  public ArrayPoint(@NotNull String fileName) throws IOException {
    var fis = new FileInputStream(fileName);
    var isr = new InputStreamReader(fis);
   try(@NotNull BufferedReader br = new BufferedReader(isr)){

    List<Double> auxiliarPoint = new ArrayList<Double>();
     var aux = br.readLine();
    while (aux != null) {
      var st = new StringTokenizer(aux);

      while (st.hasMoreTokens()) {
        var value = Double.valueOf(st.nextToken());
        auxiliarPoint.add(value);
      }
      aux = br.readLine();
    }

    point = new double[auxiliarPoint.size()] ;
    for (var i = 0; i < auxiliarPoint.size(); i++) {
      point[i] = auxiliarPoint.get(i) ;
    }

   }
  }

  @Override
  public int getDimension() {
    return point.length;
  }

  @Override
  public double[] getValues() {
    return point;
  }

  @Override
  public double getValue(int index) {
    Check.that((index >= 0) && (index < point.length), "Index value invalid: " + index +
            ". The point length is: " + point.length);

    return point[index] ;
  }

  @Override
  public void setValue(int index, double value) {
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

    for (var i = 0; i < point.length; i++) {
      this.point[i] = point[i] ;
    }
  }

  @Override
  public String toString() {
    var result = "";
    for (var anObjectives_ : point) {
      result += anObjectives_ + " ";
    }

    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    @NotNull ArrayPoint that = (ArrayPoint) o;

    return Arrays.equals(point, that.point);
  }

  @Override public int hashCode() {
    return Arrays.hashCode(point);
  }
}
