package org.uma.jmetal.util.point;

/**
 * Interface representing a point
 *
 * @author Antonio J. Nebro
 */
public interface Point {
  int getNumberOfDimensions();
  double[] getValues() ;
  double getDimensionValue(int index) ;
  void setDimensionValue(int index, double value) ;
}
