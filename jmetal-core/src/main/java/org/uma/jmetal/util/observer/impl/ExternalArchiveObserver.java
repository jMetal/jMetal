package org.uma.jmetal.util.observer.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;

import java.util.List;
import java.util.Map;

/**
 * This observer add the solutions of population to an archive. It expects a pair
 * (EVALUATIONS, int) in the map used in the update() method.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
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
