package org.uma.jmetal.problem.multiobjective.fda;

import org.uma.jmetal.problem.BoundedProblem;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;

/** Cristóbal Barba <cbarba@lcc.uma.es> */
@SuppressWarnings("serial")
public abstract class FDA extends AbstractDoubleProblem
    implements DynamicProblem<DoubleSolution, Integer>, BoundedProblem<Double, DoubleSolution> {
  protected double time;
  private boolean changeStatus = false;

  private int tauT = 5;
  private int nT = 10;

  public FDA() {
  }

  public void update(Integer counter) {
    time = (1.0d / (double) nT) * Math.floor(counter / (double) tauT);
    JMetalLogger.logger.info("Received counter: " + counter + ". Time: " + time);

    setChanged();
  }

  @Override
  public boolean hasChanged() {
    return changeStatus;
  }

  @Override
  public void setChanged() {
    changeStatus = true;
  }

  @Override
  public void clearChanged() {
    changeStatus = false;
  }
}
