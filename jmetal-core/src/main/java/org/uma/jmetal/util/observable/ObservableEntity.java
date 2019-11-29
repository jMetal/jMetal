package org.uma.jmetal.util.observable;

/**
 * Interface representing observable entities according to the Observer Pattern
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface ObservableEntity {
  Observable<?> getObservable();
}
