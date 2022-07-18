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

package org.uma.jmetal.algorithm.multiobjective.smpso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.algorithm.impl.AbstractParticleSwarmOptimization;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.measure.Measurable;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.CountingMeasure;
import org.uma.jmetal.util.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

/**
 * This class implements the SMPSORP algorithm described in: "Extending the Speed-constrained
 * Multi-Objective PSO (SMPSO) With Reference Point Based Preference Articulation. Antonio J. Nebro,
 * Juan J. Durillo, José García-Nieto, Cristóbal Barba-González, Javier Del Ser, Carlos A. Coello
 * Coello, Antonio Benítez-Hidalgo, José F. Aldana-Montes. Parallel Problem Solving from Nature --
 * PPSN XV. Lecture Notes In Computer Science, Vol. 11101, pp. 298-310. 2018".
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class SMPSORP
    extends AbstractParticleSwarmOptimization<DoubleSolution, List<DoubleSolution>>
    implements Measurable {

  private DoubleProblem problem;

  private double c1Max;
  private double c1Min;
  private double c2Max;
  private double c2Min;
  private double r1Max;
  private double r1Min;
  private double r2Max;
  private double r2Min;
  private double weightMax;
  private double weightMin;
  private double changeVelocity1;
  private double changeVelocity2;

  protected int swarmSize;
  protected int maxIterations;
  protected int iterations;

  private GenericSolutionAttribute<DoubleSolution, DoubleSolution> localBest;
  private double[][] speed;

  private JMetalRandom randomGenerator;

  public List<ArchiveWithReferencePoint<DoubleSolution>> leaders;
  private DominanceComparator<DoubleSolution> dominanceComparator;

  private MutationOperator<DoubleSolution> mutation;

  protected double deltaMax[];
  protected double deltaMin[];

  protected SolutionListEvaluator<DoubleSolution> evaluator;

  protected List<List<Double>> referencePoints;
  protected CountingMeasure currentIteration;
  protected SimpleMeasureManager measureManager;
  protected BasicMeasure<List<DoubleSolution>> solutionListMeasure;

  private List<DoubleSolution> referencePointSolutions;

  /**
   * Constructor
   */
  public SMPSORP(DoubleProblem problem, int swarmSize,
                 List<ArchiveWithReferencePoint<DoubleSolution>> leaders,
                 @NotNull List<List<Double>> referencePoints,
                 MutationOperator<DoubleSolution> mutationOperator, int maxIterations, double r1Min,
                 double r1Max,
                 double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max,
                 double weightMin, double weightMax, double changeVelocity1, double changeVelocity2,
                 DominanceComparator<DoubleSolution> dominanceComparator,
                 SolutionListEvaluator<DoubleSolution> evaluator) {
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.leaders = leaders;
    this.mutation = mutationOperator;
    this.maxIterations = maxIterations;
    this.referencePoints = referencePoints;

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
    this.evaluator = evaluator;

    this.dominanceComparator = dominanceComparator;
    localBest = new GenericSolutionAttribute<>();
    speed = new double[swarmSize][problem.getNumberOfVariables()];

    deltaMax = new double[problem.getNumberOfVariables()];
    deltaMin = new double[problem.getNumberOfVariables()];
    for (var i = 0; i < problem.getNumberOfVariables(); i++) {
      var bounds = problem.getVariableBounds().get(i);
      deltaMax[i] = (bounds.getUpperBound() - bounds.getLowerBound()) / 2.0;
      deltaMin[i] = -deltaMax[i];
    }

    currentIteration = new CountingMeasure(0);
    solutionListMeasure = new BasicMeasure<>();

    measureManager = new SimpleMeasureManager();
    measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
    measureManager.setPushMeasure("currentIteration", currentIteration);

    referencePointSolutions = new ArrayList<>();
    for (var i = 0; i < referencePoints.size(); i++) {
      var refPoint = problem.createSolution();
      for (var j = 0; j < referencePoints.get(0).size(); j++) {
        refPoint.objectives()[j] = referencePoints.get(i).get(j);
      }

      referencePointSolutions.add(refPoint);
    }
  }

  protected void updateLeadersDensityEstimator() {
    for (@NotNull BoundedArchive<DoubleSolution> leader : leaders) {
      leader.computeDensityEstimator();
    }
  }

  @Override
  protected void initProgress() {
    iterations = 1;
    currentIteration.reset(1);
    updateLeadersDensityEstimator();
  }

  @Override
  protected void updateProgress() {
    iterations += 1;
    currentIteration.increment(1);
    ;
    updateLeadersDensityEstimator();

    solutionListMeasure.push(getResult());
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override
  protected List<DoubleSolution> createInitialSwarm() {
    List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (var i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      swarm.add(newSolution);
    }

    return swarm;
  }

  @Override
  protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    swarm = evaluator.evaluate(swarm, problem);

    return swarm;
  }

  @Override
  protected void initializeLeader(List<DoubleSolution> swarm) {
    for (var particle : swarm) {
      for (@NotNull BoundedArchive<DoubleSolution> leader : leaders) {
        leader.add((DoubleSolution) particle.copy());
      }
    }
  }

  @Override
  protected void initializeVelocity(@NotNull List<DoubleSolution> swarm) {
    for (var i = 0; i < swarm.size(); i++) {
      for (var j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  @Override
  protected void initializeParticlesMemory(List<DoubleSolution> swarm) {
    for (var particle : swarm) {
      localBest.setAttribute(particle, (DoubleSolution) particle.copy());
    }
  }

  @Override
  protected void updateVelocity(List<DoubleSolution> swarm) {
    double r1, r2, c1, c2;
    double wmax, wmin;
    DoubleSolution bestGlobal;

    for (var i = 0; i < swarm.size(); i++) {
      var particle = (DoubleSolution) swarm.get(i).copy();
      var bestParticle = (DoubleSolution) localBest.getAttribute(swarm.get(i)).copy();

      bestGlobal = selectGlobalBest();

      r1 = randomGenerator.nextDouble(r1Min, r1Max);
      r2 = randomGenerator.nextDouble(r2Min, r2Max);
      c1 = randomGenerator.nextDouble(c1Min, c1Max);
      c2 = randomGenerator.nextDouble(c2Min, c2Max);
      wmax = weightMax;
      wmin = weightMin;

      for (var var = 0; var < particle.variables().size(); var++) {
        speed[i][var] = velocityConstriction(constrictionCoefficient(c1, c2) * (
                inertiaWeight(iterations, maxIterations, wmax, wmin) * speed[i][var] +
                    c1 * r1 * (bestParticle.variables().get(var) - particle.variables().get(var)) +
                    c2 * r2 * (bestGlobal.variables().get(var) - particle.variables().get(var))),
            deltaMax, deltaMin, var);
      }
    }
  }

  @Override
  protected void updatePosition(List<DoubleSolution> swarm) {
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

  @Override
  protected void perturbation(List<DoubleSolution> swarm) {
    for (var i = 0; i < swarm.size(); i++) {
      if ((i % 6) == 0) {
        mutation.execute(swarm.get(i));
      }
    }
  }

  @Override
  protected void updateLeaders(List<DoubleSolution> swarm) {
    for (var particle : swarm) {
      for (BoundedArchive<DoubleSolution> leader : leaders) {
        leader.add((DoubleSolution) particle.copy());
      }
    }
  }

  @Override
  protected void updateParticlesMemory(List<DoubleSolution> swarm) {
    for (var i = 0; i < swarm.size(); i++) {
      var flag = dominanceComparator.compare(swarm.get(i), localBest.getAttribute(swarm.get(i)));
      if (flag != 1) {
        var particle = (DoubleSolution) swarm.get(i).copy();
        localBest.setAttribute(swarm.get(i), particle);
      }
    }
  }

  @Override
  public @NotNull List<DoubleSolution> getResult() {
      List<DoubleSolution> resultList = new ArrayList<>();
      for (@NotNull ArchiveWithReferencePoint<DoubleSolution> leader : leaders) {
          for (var doubleSolution : leader.getSolutionList()) {
              resultList.add(doubleSolution);
          }
      }

      return resultList;
  }

  protected DoubleSolution selectGlobalBest() {

    var selectedSwarmIndex = randomGenerator.nextInt(0, leaders.size() - 1);
    BoundedArchive<DoubleSolution> selectedSwarm = leaders.get(selectedSwarmIndex);

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

  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
    return wma;
  }

  @Override
  public String getName() {
    return "SMPSO/RP";
  }

  @Override
  public String getDescription() {
    return "Speed contrained Multiobjective PSO";
  }

  @Override
  public MeasureManager getMeasureManager() {
    return measureManager;
  }

  public void removeDominatedSolutionsInArchives() {
    for (var archive : leaders) {
      var i = 0;
      while (i < archive.getSolutionList().size()) {
        var dominated = false;
        for (var referencePoint : referencePointSolutions) {
          if (dominanceComparator.compare(archive.getSolutionList().get(i), referencePoint) == 0) {
            dominated = true;
          }
        }

        if (dominated) {
          archive.getSolutionList().remove(i);
        } else {
          i++;
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
}
