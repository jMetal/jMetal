package org.uma.jmetal.util.observable;

import org.uma.jmetal.util.observer.Observer;

import java.util.Collection;

/**
 * Interface representing observable entities according to the Observer Pattern
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observable<D> {
	void register(Observer<D> observer) ;
	void unregister(Observer<D> observer) ;

	void notifyObservers(D data);
	int numberOfRegisteredObservers() ;
	void setChanged() ;
	boolean hasChanged() ;
	void clearChanged() ;

	Collection<Observer<D>> getObservers() ;
}
