package org.uma.jmetal.util.point;

/**
 * Interface representing a point
 *
 * @author Antonio J. Nebro
 */
public interface Point {
  int dimension();
  double[] values() ;
  double value(int index) ;
  void value(int index, double value) ;
  void update(double[] point) ;
  void set(double[] point) ;
}
