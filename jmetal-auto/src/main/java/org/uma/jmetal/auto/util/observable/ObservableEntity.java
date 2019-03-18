package org.uma.jmetal.auto.util.observable;

import org.uma.jmetal.auto.util.observer.Observer;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.naming.DescribedEntity;

import java.util.Map;
import java.util.logging.SocketHandler;

/**
 * Interface representing observable entities according to the Observer Pattern
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface ObservableEntity {
  Observable getObservable();
}
