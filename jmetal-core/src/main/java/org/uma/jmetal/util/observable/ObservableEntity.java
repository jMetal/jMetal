package org.uma.jmetal.util.observable;

/**
 * Interface representing observable entities according to the Observer pattern
 *
 * @author Antonio J. Nebro
 */
public interface ObservableEntity<T> {
  Observable<T> observable();
}
