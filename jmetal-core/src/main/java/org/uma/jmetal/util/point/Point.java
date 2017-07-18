package org.uma.jmetal.util.point;

/**
 * Interface representing a point
 *
 * @author Antonio J. Nebro
 */
public interface Point {
  public int getNumberOfDimensions();
  public double[] getValues() ;
  public double getDimensionValue(int index) ;
  public void setDimensionValue(int index, double value) ;
}
