package org.uma.jmetal.experimental.componentbasedalgorithm.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.termination.Termination;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a random search algorithm. It implements the {@link Algorithm} interface by
 * applying a component based approach.
 *
 * @param <S> Solution
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class ComponentBasedRandomSearchAlgorithm<S extends Solution<?>> implements Algorithm<S> {
  private Problem<S> problem;

  protected Termination termination;

  protected Map<String, Object> attributes;

  protected long initTime;
  protected long totalComputingTime;
  protected int evaluations;

  protected Observable<Map<String, Object>> observable;

  protected String name;
  protected NonDominatedSolutionListArchive<S> archive;

  /**
   * Constructor
   *
   * @param name
   * @param termination
   */
  public ComponentBasedRandomSearchAlgorithm(
      String name, Problem<S> problem, Termination termination) {
    this.name = name;

    this.termination = termination;
    this.problem = problem;

    this.observable = new DefaultObservable<>(name);
    this.attributes = new HashMap<>();

    this.archive = new NonDominatedSolutionListArchive<>();

    this.evaluations = 0;
  }

  @Override
  public void run() {
    initTime = System.currentTimeMillis();
    while (!termination.isMet(attributes)) {
      S newSolution = problem.createSolution();
      problem.evaluate(newSolution);
      archive.add(newSolution);

      updateProgress();
    }

    totalComputingTime = System.currentTimeMillis() - initTime;
  }

  protected void updateProgress() {
    evaluations += 1;

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());

    observable.setChanged();
    observable.notifyObservers(attributes);
  }

  @Override
  public S getResult() {
    return archive.getSolutionList().get(0);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return name;
  }

  public int getEvaluations() {
    return evaluations;
  }

  public Archive<S> getArchive() {
    return archive;
  }

  public ComponentBasedRandomSearchAlgorithm<S> withTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public ComponentBasedRandomSearchAlgorithm<S> withName(String newName) {
    this.name = newName;

    return this;
  }

  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public long getTotalComputingTime() {
    return totalComputingTime;
  }

  public long getCurrentComputingTime() {
    return System.currentTimeMillis() - initTime;
  }
}
