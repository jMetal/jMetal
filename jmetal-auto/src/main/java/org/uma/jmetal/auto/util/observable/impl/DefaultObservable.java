package org.uma.jmetal.auto.util.observable.impl;

import org.uma.jmetal.auto.util.observer.Observer;
import org.uma.jmetal.auto.util.observable.Observable;
import org.uma.jmetal.util.JMetalLogger;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DefaultObservable<D> implements Observable<D> {
	private Set<Observer<D>> observers ;
	private boolean dataHasChanged ;
	private String name ;

	public DefaultObservable(String name) {
		observers = new HashSet<>() ;
		dataHasChanged = false ;
		this.name = name ;
	}

	@Override
	public synchronized void register(Observer<D> observer) {
		observers.add(observer) ;
    JMetalLogger.logger.info("DefaultObservable " + name + ": " + observer.getName() + " registered") ;
	}

	@Override
	public synchronized void unregister(Observer<D> observer) {
		observers.remove(observer) ;
	}

	@Override
	public synchronized void notifyObservers(D data) {
		if (dataHasChanged) {
      observers.forEach(observer -> observer.update(this, data));
		}
		clearChanged();
	}

	@Override
	public synchronized int numberOfRegisteredObservers() {
		return observers.size();
	}

	@Override
	public synchronized void setChanged() {
		dataHasChanged = true ;
	}

	@Override
	public synchronized boolean hasChanged() {
		return dataHasChanged ;
	}

	@Override
	public synchronized void clearChanged() {
		dataHasChanged = false ;
	}

	@Override
	public synchronized String getName() {
		return name;
	}

	@Override
	public synchronized String getDescription() {
		return name;
	}

	@Override
	public synchronized void run() {
	}
}
