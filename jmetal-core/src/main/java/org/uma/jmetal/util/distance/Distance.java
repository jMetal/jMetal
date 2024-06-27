package org.uma.jmetal.util.distance;

/**
 * Interface representing distances between two entities
 *
 * @author
 */
@FunctionalInterface
public interface Distance<E, J> {
  double compute(E element1, J element2) ;
}
