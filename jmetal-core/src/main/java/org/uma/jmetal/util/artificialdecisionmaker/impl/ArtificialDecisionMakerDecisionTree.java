package org.uma.jmetal.util.artificialdecisionmaker.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.algorithm.InteractiveAlgorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.artificialdecisionmaker.ArtificialDecisionMaker;
import org.uma.jmetal.util.artificialdecisionmaker.DecisionTreeEstimator;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenSolutionsInObjectiveSpace;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;


/**
 * Class implementing the
 * Towards automatic testing of reference point based interactive methods described in:
 * Ojalehto, V., Podkopaev, D., & Miettinen, K. (2016, September).
 * Towards automatic testing of reference point based interactive methods.
 * In International Conference on Parallel Problem Solving from Nature (pp. 483-492). Springer, Cham.
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class ArtificialDecisionMakerDecisionTree<S extends Solution<?>> extends ArtificialDecisionMaker<S,List<S>> {

  protected List<Double> idealOjectiveVector = null;
  protected List<Double> nadirObjectiveVector = null;
  protected @Nullable List<Double> rankingCoeficient = null;
  protected List<Double> asp = null;
  protected double tolerance;
 // protected int numberReferencePoints;
  protected @Nullable JMetalRandom random = null;
  protected double considerationProbability;
  protected  int numberOfObjectives;
  protected double varyingProbability;
  protected int evaluations;
  protected int maxEvaluations;
  protected List<Double> allReferencePoints;
  protected List<Double> currentReferencePoint;
  protected List<Double> distances;
  private @Nullable S solutionRun=null;
  
  public ArtificialDecisionMakerDecisionTree(@NotNull Problem<S> problem,
                                             InteractiveAlgorithm<S,List<S>> algorithm, double considerationProbability, double tolerance, int maxEvaluations
  , @Nullable List<Double> rankingCoeficient, @Nullable List<Double> asp) {
    super(problem, algorithm);
    this.considerationProbability = considerationProbability;
    this.tolerance = tolerance;
    numberOfObjectives=problem.getNumberOfObjectives();
    this.random = JMetalRandom.getInstance();
    this.maxEvaluations = maxEvaluations;
    this.rankingCoeficient = rankingCoeficient;
    if(rankingCoeficient==null || rankingCoeficient.isEmpty()){
      initialiceRankingCoeficient();
    }
    //this.numberReferencePoints = numberReferencePoints;
    this.allReferencePoints = new ArrayList<>();
    this.distances = new ArrayList<>();


    if(asp!=null){
      this.asp = new ArrayList<>(asp);
    }
  }


  private  void  initialiceRankingCoeficient(){
    rankingCoeficient = new ArrayList<>();
    for (var i = 0; i < problem.getNumberOfObjectives() ; i++) {
      rankingCoeficient.add(1.0/problem.getNumberOfObjectives());
    }
  }

  private void updateObjectiveVector(List<S> solutionList){
   for (var j = 0; j < numberOfObjectives; j++) {
      solutionList.sort(new ObjectiveComparator<>(j));
       var objetiveMinn = solutionList.get(0).objectives()[j];
       var objetiveMaxn = solutionList.get(solutionList.size() - 1).objectives()[j];
      idealOjectiveVector.add(objetiveMinn);
      nadirObjectiveVector.add(objetiveMaxn);
    }
    if(problem instanceof DoubleProblem){
        var aux =(DoubleProblem) problem;
      for (var i = 0; i < numberOfObjectives ; i++) {
        Bounds<?> bounds = aux.getVariableBounds().get(i);
        idealOjectiveVector.add(((Number) bounds.getLowerBound()).doubleValue());
        nadirObjectiveVector.add(((Number) bounds.getUpperBound()).doubleValue());
      }
    }
    if(asp==null)
      asp = idealOjectiveVector;
  }

  @Override
  protected List<Double> generatePreferenceInformation() {

    idealOjectiveVector = new ArrayList<>(numberOfObjectives);
    nadirObjectiveVector = new ArrayList<>(numberOfObjectives);

    List<S> solutions = new ArrayList<>();
      var sol = problem.createSolution();
    problem.evaluate(sol);
    solutions.add(sol);
    updateObjectiveVector(solutions);
   // solutionRun = solutions.get(0);
      List<Double> referencePoint = new ArrayList<>(numberOfObjectives);
      for (var j = 0; j < numberOfObjectives; j++) {
          var rand = random.nextDouble(0.0, 1.0);
        if (rand < considerationProbability * rankingCoeficient.get(j)) {
          referencePoint.add( asp.get(j));
        } else {
          referencePoint.add( nadirObjectiveVector.get(j));
        }
      }


    currentReferencePoint = referencePoint;
    allReferencePoints.addAll(referencePoint);
    return referencePoint;
  }

  @Override
  protected boolean isStoppingConditionReached() {
      var stop = evaluations > maxEvaluations ;

    if(indexOfRelevantObjectiveFunctions!=null   ){
      stop = stop || indexOfRelevantObjectiveFunctions.size()==numberOfObjectives;
    }
    return stop;
  }


  @Override
  protected void initProgress() {
    evaluations =0;
    varyingProbability = considerationProbability;
  }

  @Override
  protected void updateProgress() {
    evaluations++;
  }

  @Override
  protected List<Integer> relevantObjectiveFunctions(List<S> front) {
      @NotNull List<Integer> indexRelevantObjectivesFunctions = new ArrayList<>();
    SortedMap<Double, List<Integer>> map = new TreeMap<>(Collections.reverseOrder());
    for (var i = 0; i < rankingCoeficient.size(); i++) {
        var aux = map.getOrDefault(rankingCoeficient.get(i), new ArrayList<>());
      aux.add(i);
      map.putIfAbsent(rankingCoeficient.get(i),aux);
    }
      var keys =map.keySet();
      List<Integer> list = new ArrayList<>();
      for (var key : keys) {
          for (var integer : map.get(key)) {
              list.add(integer);
          }
      }
      var order = list;
      var solution = getSolution(front,currentReferencePoint);
    for (var i : order) {
        var rand = random.nextDouble(0.0, 1.0);
      if ((asp.get(i) - solution.objectives()[i]) < tolerance
          && rand < considerationProbability) {
        indexRelevantObjectivesFunctions.add(i);
      } else if (rand < varyingProbability) {
        indexRelevantObjectivesFunctions.add(i);
      }
      varyingProbability -= (varyingProbability / i) * indexRelevantObjectivesFunctions.size();
    }
    return indexRelevantObjectivesFunctions;
  }

  @Override
  protected List<Double> calculateReferencePoints(
          List<Integer> indexOfRelevantObjectiveFunctions, @NotNull List<S> front, List<S> paretoOptimalSolutions) {
    List<Double> result = new ArrayList<>();
    @NotNull List<S> temporal = new ArrayList<>(front);

      var solution = getSolution(temporal,currentReferencePoint);
      solutionRun = solution;
      temporal.remove(solution);

        for (var i = 0; i < numberOfObjectives; i++) {
          if (indexOfRelevantObjectiveFunctions.contains(i)) {
            result.add(
                asp.get(i) - (asp.get(i) - solution.objectives()[i]) / 2);
          } else {
            //predict the i position of reference point
            result.add(prediction(i,front,solution));
          }
        }
        calculateDistance(solutionRun,asp);

    currentReferencePoint = result;
    allReferencePoints.addAll(result);

    return result;
  }

  private void calculateDistance(S solution, @NotNull List<Double> referencePoint) {
    @NotNull EuclideanDistanceBetweenSolutionsInObjectiveSpace<S> euclidean = new EuclideanDistanceBetweenSolutionsInObjectiveSpace<>();

      var distance = euclidean
        .compute(solution, getSolutionFromRP(referencePoint));
    distances.add(distance);
  }
  private S getSolutionFromRP(List<Double> referencePoint){
      var result = problem.createSolution();
    for (var i = 0; i < result.objectives().length; i++) {
      result.objectives()[i] = referencePoint.get(i);
    }
    return result;
  }

 private double prediction(int index,List<S> paretoOptimalSolutions,S solution) {
     var dte = new DecisionTreeEstimator<S>(paretoOptimalSolutions);

     var data=dte.doPrediction(index,solution);
   return data;
 }
  @Override
  protected void updateParetoOptimal(List<S> front,List<S> paretoOptimalSolutions) {
    //paretoOptimalSolutions.addAll(front);
    paretoOptimalSolutions = new ArrayList<>(front);
  }

  @Override
  public List<Double> getReferencePoints() {
    //allReferencePoints.remove(allReferencePoints.size()-1);
   return allReferencePoints;
  }

  @Override
  public List<Double> getDistances() {

    return distances;
  }

  private S getSolution(List<S> front, @NotNull List<Double> referencePoint) {
      var euclidean = new EuclideanDistanceBetweenSolutionsInObjectiveSpace<S>();
    SortedMap<Double, S> map = new TreeMap<>();
      var aux = getSolutionFromRP(referencePoint);
    for (@NotNull S solution : front) {
        var distance = euclidean.compute(solution,aux);
      map.put(distance, solution);
    }
      var result = map.get(map.firstKey());
    return result;
  }



}
