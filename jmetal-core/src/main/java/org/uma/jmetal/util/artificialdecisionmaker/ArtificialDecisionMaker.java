package org.uma.jmetal.util.artificialdecisionmaker;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;

@SuppressWarnings("serial")
public abstract class ArtificialDecisionMaker<S, R> implements Algorithm<R> {

  protected InteractiveAlgorithm<S,R> algorithm;
  protected Problem<S> problem ;
  protected List<Integer> indexOfRelevantObjectiveFunctions;
  protected List<S> paretoOptimalSolutions;

  public ArtificialDecisionMaker(Problem<S> problem,InteractiveAlgorithm<S,R> algorithm){
    this.problem = problem;
    this.algorithm =algorithm;
    this.indexOfRelevantObjectiveFunctions = new ArrayList<>();
    this.paretoOptimalSolutions = new ArrayList<>();
  }

  protected abstract List<Double> generatePreferenceInformation();
  protected abstract boolean isStoppingConditionReached();
  protected abstract void initProgress();
  protected abstract void updateProgress();
  protected abstract List<Integer> relevantObjectiveFunctions(R front);
  protected abstract List<Double> calculateReferencePoints(
      List<Integer> indexOfRelevantObjectiveFunctions,R front,List<S> paretoOptimalSolutions);
  protected abstract void updateParetoOptimal(R front,List<S> paretoOptimalSolutions);
  public abstract List<Double> getReferencePoints();
  public abstract List<Double> getDistances();
  @Override
  public void run() {
    List<Double> initialReferencePoints=generatePreferenceInformation();
    R front;
    List<Double> interestingPoint=initialReferencePoints;
    initProgress();
    while (!isStoppingConditionReached()) {
      this.algorithm.updatePointOfInterest(interestingPoint);
      this.algorithm.run();
      front=this.algorithm.result();
      updateParetoOptimal(front,paretoOptimalSolutions);
      indexOfRelevantObjectiveFunctions=relevantObjectiveFunctions(front);
      interestingPoint = calculateReferencePoints(indexOfRelevantObjectiveFunctions,front,paretoOptimalSolutions);
      updateProgress();
    }
  }

  @Override
  public R result() {
    return this.algorithm.result();
  }

  @Override
  public String name() {
    return "ArtificialDecisionMaker";
  }

  @Override
  public String description() {
    return "ArtificialDecisionMaker";
  }
}
