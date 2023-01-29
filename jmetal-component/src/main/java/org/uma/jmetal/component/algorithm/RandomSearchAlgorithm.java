package org.uma.jmetal.component.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

/**
 * Class representing a random search algorithm. It implements the {@link Algorithm} interface by
 * applying a component-based approach.
 *
 * @param <S> Solution
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
@SuppressWarnings("serial")
public class RandomSearchAlgorithm<S extends Solution<?>> implements Algorithm<List<S>> {
  protected Termination termination;
  protected SolutionsCreation<S> solutionsCreation;
  protected Evaluation<S> evaluation;

  protected Map<String, Object> attributes;

  protected long initTime;
  protected long totalComputingTime;
  protected int evaluations;

  protected Observable<Map<String, Object>> observable;

  protected String name;
  protected NonDominatedSolutionListArchive<S> archive;

  private int evaluatedSolutions ;

  /**
   * Constructor
   *
   * @param name
   * @param termination
   */
  public RandomSearchAlgorithm(
      String name,
      SolutionsCreation<S> solutionsCreation,
      Evaluation<S> evaluation,
      Termination termination) {
    this.name = name;

    this.termination = termination;
    this.solutionsCreation = solutionsCreation;
    this.evaluation = evaluation ;

    this.observable = new DefaultObservable<>(name);
    this.attributes = new HashMap<>();

    this.archive = new NonDominatedSolutionListArchive<>();
  }

  @Override
  public void run() {
    initTime = System.currentTimeMillis();
    initProgress() ;
    while (!termination.isMet(attributes)) {
      List<S> solutions = solutionsCreation.create();
      evaluation.evaluate(solutions);
      evaluatedSolutions = solutions.size() ;
      updateBestFoundSolutions(solutions) ;
      updateProgress();
    }

    totalComputingTime = System.currentTimeMillis() - initTime;
  }

  protected void initProgress() {
    evaluations = 0 ;

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());
    attributes.put("BEST_SOLUTIONS", archive.solutions());

    observable.setChanged();
    observable.notifyObservers(attributes);
  }

  protected void updateProgress() {
    evaluations += evaluatedSolutions;

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());
    attributes.put("BEST_SOLUTIONS", archive.solutions());

    observable.setChanged();
    observable.notifyObservers(attributes);
  }

  protected void updateBestFoundSolutions(List<S> solutions) {
    solutions.forEach(solution -> archive.add(solution));
  }

  protected List<S> getBestSolutions() {
    return archive.solutions() ;
  }

  @Override
  public List<S> result() {
    return archive.solutions();
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String description() {
    return name;
  }

  public int getEvaluations() {
    return evaluations;
  }

  public Archive<S> getArchive() {
    return archive;
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
