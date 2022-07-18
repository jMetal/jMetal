package org.uma.jmetal.util.legacy.front.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.legacy.front.Front;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

/**
 * This class implements the {@link Front} interface by using an array of {@link Point} objects
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class ArrayFront implements Front {
  protected Point[] points;
  protected int numberOfPoints;
  private int pointDimensions;

  /** Constructor */
  public ArrayFront() {
    points = null;
    numberOfPoints = 0;
    pointDimensions = 0;
  }

  /** Constructor */
  public ArrayFront(@Nullable List<? extends Solution<?>> solutionList) {
    if (solutionList == null) {
      throw new JMetalException("The list of solutions is null");
    } else if (solutionList.size() == 0) {
      throw new JMetalException("The list of solutions is empty");
    }

    numberOfPoints = solutionList.size();
    pointDimensions = solutionList.get(0).objectives().length;
    points = new Point[numberOfPoints];

    points = new Point[numberOfPoints];
    for (int i = 0; i < numberOfPoints; i++) {
      Point point = new ArrayPoint(pointDimensions);
      for (int j = 0; j < pointDimensions; j++) {
        point.setValue(j, solutionList.get(i).objectives()[j]);
      }
      points[i] = point;
    }
  }

  /** Copy Constructor */
  public ArrayFront(Front front) {
    if (front == null) {
      throw new JMetalException("The front is null");
    } else if (front.getNumberOfPoints() == 0) {
      throw new JMetalException("The front is empty");
    }
    numberOfPoints = front.getNumberOfPoints();
    pointDimensions = front.getPoint(0).getDimension();
    points = new Point[numberOfPoints];

    points = new Point[numberOfPoints];
    for (int i = 0; i < numberOfPoints; i++) {
      points[i] = new ArrayPoint(front.getPoint(i));
    }
  }

  /** Constructor */
  public ArrayFront(int numberOfPoints, int dimensions) {
    this.numberOfPoints = numberOfPoints;
    pointDimensions = dimensions;
    points = new Point[this.numberOfPoints];

    for (int i = 0; i < this.numberOfPoints; i++) {
      @NotNull Point point = new ArrayPoint(pointDimensions);
      for (int j = 0; j < pointDimensions; j++) {
        point.setValue(j, 0.0);
      }
      points[i] = point;
    }
  }

  public ArrayFront(@NotNull String fileName, String separator) throws FileNotFoundException {
    this();

    InputStream inputStream = createInputStream(fileName);
    InputStreamReader isr = new InputStreamReader(inputStream);
    @NotNull BufferedReader br = new BufferedReader(isr);

    @NotNull List<Point> list = new ArrayList<>();
    int numberOfObjectives = 0;
    String line;
    try {
      line = br.readLine();

      while (line != null) {
        String @NotNull [] stringValues = line.split(separator);
        double[] values = new double[10];
        int count = 0;
        for (String stringValue : stringValues) {
          double valueOf = Double.valueOf(stringValue);
          if (values.length == count) values = Arrays.copyOf(values, count * 2);
          values[count++] = valueOf;
        }
        values = Arrays.copyOfRange(values, 0, count);

        if (numberOfObjectives == 0) {
          numberOfObjectives = stringValues.length;
        } else {
          Check.that(
              numberOfObjectives == stringValues.length,
              "\"Invalid number of points read. \"\n"
                  + "                  + \"Expected: \"\n"
                  + "                  + numberOfObjectives\n"
                  + "                  + \", received: \"\n"
                  + "                  + values.length");
        }

        Point point = new ArrayPoint(values);
        list.add(point);
        line = br.readLine();
      }
      br.close();
    } catch (IOException e) {
      throw new JMetalException("Error reading file", e);
    } catch (NumberFormatException e) {
      throw new JMetalException("Format number exception when reading file", e);
    }

    numberOfPoints = list.size();
    points = new Point[list.size()];
    points = list.toArray(points);
    if (numberOfPoints == 0) {
      pointDimensions = 0;
    } else {
      pointDimensions = points[0].getDimension();
    }
    for (int i = 0; i < numberOfPoints; i++) {
      points[i] = list.get(i);
    }
  }

  /**
   * Constructor
   *
   * @param fileName File containing the data. Each line of the file is a list of objective values
   * @throws FileNotFoundException
   */
  public ArrayFront(String fileName) throws FileNotFoundException {
    //this(fileName, "\\s+");
    this(fileName, ",");
  }

  public InputStream createInputStream(String fileName) throws FileNotFoundException {
    InputStream inputStream = getClass().getResourceAsStream(fileName);
    if (inputStream == null) {
      inputStream = new FileInputStream(fileName);
    }

    return inputStream;
  }

  @Override
  public int getNumberOfPoints() {
    return numberOfPoints;
  }

  @Override
  public int getPointDimensions() {
    return pointDimensions;
  }

  @Override
  public Point getPoint(int index) {
    if (index < 0) {
      throw new JMetalException("The index value is negative");
    } else if (index >= numberOfPoints) {
      throw new JMetalException(
          "The index value ("
              + index
              + ") is greater than the number of "
              + "points ("
              + numberOfPoints
              + ")");
    }
    return points[index];
  }

  @Override
  public void setPoint(int index, Point point) {
    if (index < 0) {
      throw new JMetalException("The index value is negative");
    } else if (index >= numberOfPoints) {
      throw new JMetalException(
          "The index value ("
              + index
              + ") is greater than the number of "
              + "points ("
              + numberOfPoints
              + ")");
    } else if (point == null) {
      throw new JMetalException("The point is null");
    }
    points[index] = point;
  }

  @Override
  public void sort(Comparator<Point> comparator) {
    // Arrays.sort(points, comparator);
    Arrays.sort(points, 0, numberOfPoints, comparator);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ArrayFront that = (ArrayFront) o;

    if (numberOfPoints != that.numberOfPoints) return false;
    if (pointDimensions != that.pointDimensions) return false;
    if (!Arrays.equals(points, that.points)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(points);
    result = 31 * result + numberOfPoints;
    result = 31 * result + pointDimensions;
    return result;
  }

  @Override
  public String toString() {
    return Arrays.toString(points);
  }

  @Override
  public double[][] getMatrix() {
    List<double[]> list = new ArrayList<>();
    int bound = getNumberOfPoints();
    for (int i = 0; i < bound; i++) {
      double[] values = points[i].getValues();
      list.add(values);
    }
    double[][] matrix = list.toArray(new double[0][]);

      return matrix;
  }
}
