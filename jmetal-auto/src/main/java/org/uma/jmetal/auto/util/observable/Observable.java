package org.uma.jmetal.auto.util.observable;

import org.uma.jmetal.auto.util.observer.Observer;
import org.uma.jmetal.util.naming.DescribedEntity;

import java.util.Collection;
import java.util.List;

/**
 * Interface representing observable entities according to the Observer Pattern
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observable<D> extends DescribedEntity {
	void register(Observer<D> observer) ;
	void unregister(Observer<D> observer) ;

	void notifyObservers(D data);
	int numberOfRegisteredObservers() ;
	void setChanged() ;
	boolean hasChanged() ;
	void clearChanged() ;

	Collection<Observer<D>> getObservers() ;
}
