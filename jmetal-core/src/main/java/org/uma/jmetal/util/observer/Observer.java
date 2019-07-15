package org.uma.jmetal.util.observer;


import org.uma.jmetal.util.observable.Observable;

/**
 * Interface representing observers according to the Observer Pattern
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@FunctionalInterface
public interface Observer<D> {
	void update(Observable<D> observable, D data) ;
}
