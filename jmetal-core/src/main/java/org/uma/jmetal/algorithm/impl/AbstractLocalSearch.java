package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;

public abstract class AbstractLocalSearch<S> implements Algorithm<S> {

  private S currentSolution;

  protected abstract S setCurrentSolution();

  protected abstract void initProgress();

  protected abstract void updateProgress();

  protected abstract boolean isStoppingConditionReached();

  protected abstract S updateCurrentSolution(S currentSolution);

  @Override
  public void run() {
    currentSolution = setCurrentSolution();

    initProgress();
    while (!isStoppingConditionReached()) {
      currentSolution = updateCurrentSolution(currentSolution);

      updateProgress();
    }
  }

  @Override
  public S getResult() {
    return currentSolution;
  }

  public void setCurrentSolution(S solution) {
    currentSolution = solution ;
  }

  public S getCurrentSolution() {
    return currentSolution ;
  }
}
