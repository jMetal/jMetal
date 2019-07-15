package org.uma.jmetal.auto.util.observer.impl;

import org.uma.jmetal.auto.util.observable.Observable;
import org.uma.jmetal.auto.util.observer.Observer;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;

import java.util.List;
import java.util.Map;

public class ExternalArchiveObserver<S extends Solution<?>> implements Observer<Map<String, Object>> {

  private Archive<S> archive ;

  public ExternalArchiveObserver(Archive<S> archive) {
    this.archive = archive ;
  }

  @Override
  public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
    List<S> population = (List<S>) data.get("POPULATION");
    population.stream().forEach(solution -> archive.add((S) solution.copy()));
  }

  public Archive<S> getArchive() {
    return archive;
  }

  public String getName() {
    return "External archive observer";
  }

  @Override
  public String toString() {
    return getName() ;
  }
}
