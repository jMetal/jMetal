//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.smpso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.algorithm.impl.AbstractParticleSwarmOptimization;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.CountingMeasure;
import org.uma.jmetal.util.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

/**
 * This class implements the SMPSORP algorithm described in:
 * "Extending the Speed-constrained Multi-Objective PSO (SMPSO) With Reference Point Based Preference
 * Articulation. Antonio J. Nebro, Juan J. Durillo, José García-Nieto, Cristóbal Barba-González,
 * Javier Del Ser, Carlos A. Coello Coello, Antonio Benítez-Hidalgo, José F. Aldana-Montes.
 * Parallel Problem Solving from Nature -- PPSN XV. Lecture Notes In Computer Science, Vol. 11101,
 * pp. 298-310. 2018".
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class SMPSORP
        extends AbstractParticleSwarmOptimization<DoubleSolution, List<DoubleSolution>>
        implements ObservableEntity {
  protected DoubleProblem problem;

  protected double c1Max;
  protected double c1Min;
  protected double c2Max;
  protected double c2Min;
  protected double r1Max;
  protected double r1Min;
  protected double r2Max;
  protected double r2Min;
  protected double weightMax;
  protected double weightMin;
  protected double changeVelocity1;
  protected double changeVelocity2;

  protected int swarmSize;
  protected int evaluations;

  protected GenericSolutionAttribute<DoubleSolution, DoubleSolution> localBest;
  protected double[][] speed;

  protected JMetalRandom randomGenerator;

  protected List<ArchiveWithReferencePoint<DoubleSolution>> leaders;
  private Comparator<DoubleSolution> dominanceComparator;

  protected MutationOperator<DoubleSolution> mutation;

  protected double[] deltaMax;
  protected double[] deltaMin;

  protected Evaluation<DoubleSolution> evaluation;
  protected Termination termination;

  protected List<List<Double>> referencePoints ;
  protected CountingMeasure currentIteration ;
  protected SimpleMeasureManager measureManager ;
  protected BasicMeasure<List<DoubleSolution>> solutionListMeasure ;

  private List<DoubleSolution> referencePointSolutions ;

  protected long startTime;
  protected long totalComputingTime;

  protected Map<String, Object> algorithmStatusData;
  protected Observable<Map<String, Object>> observable;

  /**
   * Constructor
   */
  public SMPSORP(@NotNull DoubleProblem problem, int swarmSize,
                 List<ArchiveWithReferencePoint<DoubleSolution>> leaders,
                 @NotNull List<List<Double>> referencePoints,
                 MutationOperator<DoubleSolution> mutationOperator, double r1Min, double r1Max,
                 double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max,
                 double weightMin, double weightMax, double changeVelocity1, double changeVelocity2,
                 Evaluation<DoubleSolution> evaluation, Termination termination) {
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.leaders = leaders;
    this.mutation = mutationOperator;
    this.referencePoints = referencePoints ;

    this.r1Max = r1Max;
    this.r1Min = r1Min;
    this.r2Max = r2Max;
    this.r2Min = r2Min;
    this.c1Max = c1Max;
    this.c1Min = c1Min;
    this.c2Max = c2Max;
    this.c2Min = c2Min;
    this.weightMax = weightMax;
    this.weightMin = weightMin;
    this.changeVelocity1 = changeVelocity1;
    this.changeVelocity2 = changeVelocity2;

    randomGenerator = JMetalRandom.getInstance();
    this.evaluation = evaluation;
    this.termination = termination ;

    dominanceComparator = new DominanceWithConstraintsComparator<DoubleSolution>();
    localBest = new GenericSolutionAttribute<DoubleSolution, DoubleSolution>();
    speed = new double[swarmSize][problem.getNumberOfVariables()];

    deltaMax = new double[problem.getNumberOfVariables()];
    deltaMin = new double[problem.getNumberOfVariables()];
    for (var i = 0; i < problem.getNumberOfVariables(); i++) {
      var bounds = problem.getVariableBounds().get(i);
      deltaMax[i] = (bounds.getUpperBound() - bounds.getLowerBound()) / 2.0;
      deltaMin[i] = -deltaMax[i];
    }

    currentIteration = new CountingMeasure(0) ;
    solutionListMeasure = new BasicMeasure<>() ;

    measureManager = new SimpleMeasureManager() ;
    measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
    measureManager.setPushMeasure("currentIteration", currentIteration);


    referencePointSolutions = new ArrayList<>() ;
    for (var i = 0; i < referencePoints.size(); i++) {
      var refPoint = problem.createSolution() ;
      for (var j = 0; j < referencePoints.get(0).size(); j++) {
        refPoint.objectives()[j] = referencePoints.get(i).get(j);
      }

      referencePointSolutions.add(refPoint) ;
    }

    algorithmStatusData = new HashMap<>();
    observable = new DefaultObservable<>("SMPSORP observable");
  }

  @Override
  public void run() {
    startTime = System.currentTimeMillis();
    super.run();
    totalComputingTime = System.currentTimeMillis() - startTime;
  }

  protected void updateLeadersDensityEstimator() {
    for (BoundedArchive<DoubleSolution> leader : leaders) {
      leader.computeDensityEstimator();
    }
  }

  @Override
  protected void initProgress() {
    evaluations = swarmSize;
    updateLeadersDensityEstimator();

    algorithmStatusData.put("EVALUATIONS", evaluations);
    algorithmStatusData.put("SWARM", getSwarm());
    algorithmStatusData.put("POPULATION", this.getResult());
    algorithmStatusData.put("LEADERS_ARCHIVE", leaders);
    algorithmStatusData.put("COMPUTING_TIME", System.currentTimeMillis() - startTime);

    observable.setChanged();
    observable.notifyObservers(algorithmStatusData);
  }

  @Override
  protected void updateProgress() {
    evaluations += swarmSize;
    updateLeadersDensityEstimator();

    algorithmStatusData.put("EVALUATIONS", evaluations);
    algorithmStatusData.put("SWARM", getSwarm());
    algorithmStatusData.put("POPULATION", this.getResult());
    algorithmStatusData.put("LEADERS_ARCHIVE", leaders);
    algorithmStatusData.put("COMPUTING_TIME", System.currentTimeMillis() - startTime);

    observable.setChanged();
    observable.notifyObservers(algorithmStatusData);
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return termination.isMet(algorithmStatusData);
  }

  @Override protected List<DoubleSolution> createInitialSwarm() {
    @NotNull List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (var i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      swarm.add(newSolution);
    }

    return swarm;
  }

  @Override
  protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    return evaluation.evaluate(swarm);
  }

  @Override protected void initializeLeader(List<DoubleSolution> swarm) {
    for (@NotNull DoubleSolution particle : swarm) {
      for (@NotNull BoundedArchive<DoubleSolution> leader : leaders) {
        leader.add((DoubleSolution) particle.copy());
      }
    }
  }

  @Override protected void initializeVelocity(@NotNull List<DoubleSolution> swarm) {
    for (var i = 0; i < swarm.size(); i++) {
      for (var j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  @Override protected void initializeParticlesMemory(List<DoubleSolution> swarm) {
    for (var particle : swarm) {
      localBest.setAttribute(particle, (DoubleSolution) particle.copy());
    }
  }

  @Override protected void updateVelocity(@NotNull List<DoubleSolution> swarm) {
    double r1, r2, c1, c2;
    DoubleSolution bestGlobal;

    for (var i = 0; i < swarm.size(); i++) {
      var particle = (DoubleSolution) swarm.get(i).copy();
      var bestParticle = (DoubleSolution) localBest.getAttribute(swarm.get(i)).copy();

      bestGlobal = selectGlobalBest();

      r1 = randomGenerator.nextDouble(r1Min, r1Max);
      r2 = randomGenerator.nextDouble(r2Min, r2Max);
      c1 = randomGenerator.nextDouble(c1Min, c1Max);
      c2 = randomGenerator.nextDouble(c2Min, c2Max);

      for (var var = 0; var < particle.variables().size(); var++) {
        speed[i][var] =
                velocityConstriction(
                        constrictionCoefficient(c1, c2)
                                * (weightMax * speed[i][var]
                                + c1 * r1 * (bestParticle.variables().get(var) - particle.variables().get(var))
                                + c2 * r2 * (bestGlobal.variables().get(var) - particle.variables().get(var))),
                        deltaMax,
                        deltaMin,
                        var);
      }
    }
  }

  @Override protected void updatePosition(List<DoubleSolution> swarm) {
    for (var i = 0; i < swarmSize; i++) {
      var particle = swarm.get(i);
      for (var j = 0; j < particle.variables().size(); j++) {
        particle.variables().set(j, particle.variables().get(j) + speed[i][j]);

        var bounds = problem.getVariableBounds().get(j);
        var lowerBound = bounds.getLowerBound();
        var upperBound = bounds.getUpperBound();
        if (particle.variables().get(j) < lowerBound) {
          particle.variables().set(j, lowerBound);
          speed[i][j] = speed[i][j] * changeVelocity1;
        }
        if (particle.variables().get(j) > upperBound) {
          particle.variables().set(j, upperBound);
          speed[i][j] = speed[i][j] * changeVelocity2;
        }
      }
    }
  }

  @Override protected void perturbation(@NotNull List<DoubleSolution> swarm) {
    for (var i = 0; i < swarm.size(); i++) {
      if ((i % 6) == 0) {
        mutation.execute(swarm.get(i));
      }
    }
  }

  @Override protected void updateLeaders(@NotNull List<DoubleSolution> swarm) {
    for (var particle : swarm) {
      for (BoundedArchive<DoubleSolution> leader : leaders) {
        leader.add((DoubleSolution) particle.copy());
      }
    }
  }

  @Override protected void updateParticlesMemory(List<DoubleSolution> swarm) {
    for (var i = 0; i < swarm.size(); i++) {
      var flag = dominanceComparator.compare(swarm.get(i), localBest.getAttribute(swarm.get(i)));
      if (flag != 1) {
        @Nullable DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
        localBest.setAttribute(swarm.get(i), particle);
      }
    }
  }

  @Override public @NotNull List<DoubleSolution> getResult() {
      @NotNull List<DoubleSolution> resultList = new ArrayList<>();
      for (var leader : leaders) {
          for (var doubleSolution : leader.getSolutionList()) {
              resultList.add(doubleSolution);
          }
      }

      return resultList;
  }

  protected DoubleSolution selectGlobalBest() {

    var selectedSwarmIndex = randomGenerator.nextInt(0, leaders.size() - 1);
    BoundedArchive<DoubleSolution> selectedSwarm = leaders.get(selectedSwarmIndex) ;

    @Nullable DoubleSolution bestGlobal;
    var pos1 = randomGenerator.nextInt(0, selectedSwarm.getSolutionList().size() - 1);
    var pos2 = randomGenerator.nextInt(0, selectedSwarm.getSolutionList().size() - 1);

    var one = selectedSwarm.getSolutionList().get(pos1);
    var two = selectedSwarm.getSolutionList().get(pos2);

    if (selectedSwarm.getComparator().compare(one, two) < 1) {
      bestGlobal = (DoubleSolution) one.copy();
    } else {
      bestGlobal = (DoubleSolution) two.copy();
    }

    return bestGlobal;
  }

  private double velocityConstriction(double v, double[] deltaMax, double @NotNull [] deltaMin,
                                      int variableIndex) {

    var dmax = deltaMax[variableIndex];
    var dmin = deltaMin[variableIndex];

    var result = v;

    if (v > dmax) {
      result = dmax;
    }

    if (v < dmin) {
      result = dmin;
    }

    return result;
  }

  private double constrictionCoefficient(double c1, double c2) {
    var rho = c1 + c2;
    if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
    }
  }

  @Override public @NotNull String getName() {
    return "SMPSORP" ;
  }

  @Override public String getDescription() {
    return "Speed contrained Multiobjective PSO" ;
  }

  public void removeDominatedSolutionsInArchives() {
    for (var archive: leaders) {
      var i = 0 ;
      while (i < archive.getSolutionList().size()) {
        var dominated = false ;
        for (var referencePoint : referencePointSolutions) {
          if (dominanceComparator.compare(archive.getSolutionList().get(i), referencePoint) == 0) {
            dominated = true ;
          }
        }

        if (dominated) {
          archive.getSolutionList().remove(i) ;
        } else {
          i++ ;
        }
      }
    }
  }

  public synchronized void changeReferencePoints(List<List<Double>> referencePoints) {
    for (var i = 0; i < leaders.size(); i++) {
      leaders.get(i).changeReferencePoint(referencePoints.get(i));
    }
  }

  public List<DoubleSolution> getReferencePointSolutions() {
    return referencePointSolutions;
  }

  public void setReferencePointSolutions(List<DoubleSolution> referencePointSolutions) {
    this.referencePointSolutions = referencePointSolutions;
  }

  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }

  public long getTotalComputingTime() {
    return totalComputingTime;
  }

  /* Getters */
  public int getSwarmSize() {
    return swarmSize;
  }

  public int getEvaluations() {
    return evaluations;
  }
}
