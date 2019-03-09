package org.uma.jmetal.auto.observer;

import org.uma.jmetal.auto.observable.Observable;
import org.uma.jmetal.util.naming.DescribedEntity;

/**
 * Interface representing observers according to the Observer Pattern
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observer<D> extends DescribedEntity {
	void update(Observable<D> observable, D data) ;
}
