//  FastPGA.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
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

package org.uma.jmetal.metaheuristic.multiobjective.fastpga;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.operator.selection.BinaryTournament;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.comparator.FPGAFitnessComparator;

import java.util.Comparator;

/**
 * This class implements the FPGA (Fast Pareto Genetic Algorithm).
 */
public class FastPGA extends Algorithm {
  private static final long serialVersionUID = -1288400553889158174L;

  private int maxPopulationSize ;
  private int initialPopulationSize ;
  private int maxEvaluations ;
  private double a ;
  private double b ;
  private double c ;
  private double d ;
  private int termination ;

  private Crossover crossover;
  private Mutation mutation;
  private Selection selection;

  @Deprecated
  public FastPGA() {
    super();
  }

  /** Constructor */
  private FastPGA(Builder builder) {
    problem = builder.problem ;
    maxPopulationSize = builder.maxPopulationSize ;
    initialPopulationSize = builder.initialPopulationSize ;
    maxEvaluations = builder.maxEvaluations ;
    a = builder.a ;
    b = builder.b ;
    c = builder.c ;
    d = builder.d ;
    termination = builder.termination ;

    crossover = builder.crossover ;
    mutation = builder.mutation ;
    selection = builder.selection ;
  }

  /* Getters */
  public int getMaxPopulationSize() {
    return maxPopulationSize;
  }

  public int getInitialPopulationSize() {
    return initialPopulationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public double getA() {
    return a;
  }

  public double getB() {
    return b;
  }

  public double getC() {
    return c;
  }

  public double getD() {
    return d;
  }

  public int getTermination() {
    return termination;
  }

  public Crossover getCrossover() {
    return crossover;
  }

  public Mutation getMutation() {
    return mutation;
  }

  public Selection getSelection() {
    return selection;
  }

  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    int populationSize ;
    int offSpringSize ;
    int evaluations;
    SolutionSet solutionSet ;
    SolutionSet offSpringSolutionSet ;
    SolutionSet candidateSolutionSet ;

    Distance distance = new Distance();
    Comparator<Solution> fpgaFitnessComparator = new FPGAFitnessComparator();

    //Initialize populationSize and offSpringSize
    evaluations = 0;
    populationSize = initialPopulationSize;
    offSpringSize = maxPopulationSize;

    //Build a solution set randomly
    solutionSet = new SolutionSet(populationSize);
    for (int i = 0; i < populationSize; i++) {
      Solution solution = new Solution(problem);
      problem.evaluate(solution);
      problem.evaluateConstraints(solution);
      evaluations++;
      solutionSet.add(solution);
    }

    //Begin the iterations
    Solution[] parents = new Solution[2];
    Solution[] offSprings;
    boolean stop = false;
    int reachesMaxNonDominated = 0;
    while (!stop) {

      // Create the candidate solutionSet
      offSpringSolutionSet = new SolutionSet(offSpringSize);
      for (int i = 0; i < offSpringSize / 2; i++) {
        parents[0] = (Solution) selection.execute(solutionSet);
        parents[1] = (Solution) selection.execute(solutionSet);
        offSprings = (Solution[]) crossover.execute(parents);
        mutation.execute(offSprings[0]);
        mutation.execute(offSprings[1]);
        problem.evaluate(offSprings[0]);
        problem.evaluateConstraints(offSprings[0]);
        evaluations++;
        problem.evaluate(offSprings[1]);
        problem.evaluateConstraints(offSprings[1]);
        evaluations++;
        offSpringSolutionSet.add(offSprings[0]);
        offSpringSolutionSet.add(offSprings[1]);
      }

      // Merge the populations
      candidateSolutionSet = solutionSet.union(offSpringSolutionSet);

      // Rank
      Ranking ranking = new Ranking(candidateSolutionSet);
      distance.crowdingDistanceAssignment(ranking.getSubfront(0));
      FPGAFitness fitness = new FPGAFitness(candidateSolutionSet, problem);
      fitness.fitnessAssign();

      // Count the non-dominated solutions in candidateSolutionSet      
      int count = ranking.getSubfront(0).size();

      //Regulate
      populationSize = (int) Math.min(a + Math.floor(b * count), maxPopulationSize);
      offSpringSize = (int) Math.min(c + Math.floor(d * count), maxPopulationSize);

      candidateSolutionSet.sort(fpgaFitnessComparator);
      solutionSet = new SolutionSet(populationSize);

      for (int i = 0; i < populationSize; i++) {
        solutionSet.add(candidateSolutionSet.get(i));
      }

      //Termination
      if (termination == 0) {
        ranking = new Ranking(solutionSet);
        count = ranking.getSubfront(0).size();
        if (count == maxPopulationSize) {
          if (reachesMaxNonDominated == 0) {
            reachesMaxNonDominated = evaluations;
          }
          if (evaluations - reachesMaxNonDominated >= maxEvaluations) {
            stop = true;
          }
        } else {
          reachesMaxNonDominated = 0;
        }
      } else {
        if (evaluations >= maxEvaluations) {
          stop = true;
        }
      }
    }

    Ranking ranking = new Ranking(solutionSet);
    return ranking.getSubfront(0);
  }

  /** Builder class */
  public static class Builder {
    private Problem problem ;
    private int maxPopulationSize ;
    private int initialPopulationSize ;
    private int maxEvaluations ;
    private double a ;
    private double b ;
    private double c ;
    private double d ;
    private int termination ;

    private Crossover crossover;
    private Mutation mutation;
    private Selection selection;

    public Builder(Problem problem) {
      this.problem = problem ;
      maxPopulationSize = 100 ;
      initialPopulationSize = 100 ;
      maxEvaluations = 25000 ;
      a = 20.0 ;
      b = 1.0 ;
      c = 20.0 ;
      d = 0.0 ;
      termination = 1 ;

      crossover = new SBXCrossover.Builder()
              .setProbability(0.9)
              .setDistributionIndex(20.0)
              .build() ;

      mutation = new PolynomialMutation.Builder()
              .setProbability(1.0 / problem.getNumberOfVariables())
              .setDistributionIndex(20.0)
              .build() ;

      selection = new BinaryTournament.Builder()
              .setComparator(new FPGAFitnessComparator())
              .build() ;
    }

    public Builder setMaxPopulationSize(int maxPopulationSize) {
      this.maxPopulationSize = maxPopulationSize;

      return this ;
    }

    public Builder setInitialPopulationSize(int initialPopulationSize) {
      this.initialPopulationSize = initialPopulationSize;

      return this ;
    }

    public Builder setMaxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations;

      return this ;
    }

    public Builder setA(double a) {
      this.a = a;

      return this ;
    }

    public Builder setB(double b) {
      this.b = b;

      return this ;
    }

    public Builder setC(double c) {
      this.c = c;

      return this ;
    }

    public Builder setD(double d) {
      this.d = d;

      return this ;
    }

    public Builder setTermination(int termination) {
      if ((termination != 0) && (termination != 1)) {
        throw new JMetalException("Invalid termination value: " + termination) ;
      }
      this.termination = termination ;

      return this ;
    }

    public Builder setCrossover(Crossover crossover) {
      this.crossover = crossover;

      return this ;
    }

    public Builder setMutation(Mutation mutation) {
      this.mutation = mutation;

      return this ;
    }

    public Builder setSelection(Selection selection) {
      this.selection = selection;

      return this ;
    }

    public FastPGA build() {
      return new FastPGA(this) ;
    }
  }
}
