//  ParallelMOEAD.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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

package org.uma.jmetal.metaheuristic.multiobjective.moead;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;

public class ParallelMOEAD extends MOEADTemplate implements Runnable {
  private static final long serialVersionUID = -8602634324582384579L;
  // FIXME: Pending to be implemented
  private ParallelMOEAD parentThread;
  private Thread[] thread;
  private CyclicBarrier barrier;
  private int numberOfThreads;
  private int threadIdentifier ;

  private Builder builder ;

  /** Constructor */
  protected ParallelMOEAD(Builder builder) {
    super(builder) ;

    parentThread = null;
    threadIdentifier = 0;

    numberOfThreads = 8 ;
  }

  /** Constructor */
  protected ParallelMOEAD(Builder builder, ParallelMOEAD parentThread, int threadIdentifier, int numberOfThreads) {
    super(builder) ;
    this.builder = builder ;
    this.parentThread = parentThread;

    this.numberOfThreads = numberOfThreads;
    thread = new Thread[numberOfThreads];
    this.threadIdentifier = threadIdentifier;
  }

  public void run() {
    neighborhood = parentThread.neighborhood;
    problem = parentThread.problem;
    lambda = parentThread.lambda;
    population = parentThread.population;
    idealPoint = parentThread.idealPoint;
    barrier = parentThread.barrier;

    int partitions = parentThread.populationSize / parentThread.numberOfThreads;

    evaluations= 0;
    maxEvaluations = parentThread.maxEvaluations / parentThread.numberOfThreads;

    try {
      barrier.await();
    } catch (InterruptedException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    } catch (BrokenBarrierException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    }

    int first;
    int last;

    first = partitions * threadIdentifier;
    if (threadIdentifier == (parentThread.numberOfThreads - 1)) {
      last = parentThread.populationSize - 1;
    } else {
      last = first + partitions - 1;
    }

    //  JMetalLogger.logger.info("Id: " + id_ + "  Partitions: " + partitions +
    //          " First: " + first + " Last: " + last);

    do {
      int permutationLength = last - first ;
      int[] permutation = new int[permutationLength];
      Utils.randomPermutation(permutation, permutationLength);

      for (int i = first; i <= last; i++) {
        int subProblemId = i ; //permutation[i] + first ;
        NeighborType neighborType = chooseNeighborType() ;

        Solution child ;
        synchronized (parentThread) {
          Solution[] parents = parentSelection(subProblemId, neighborType) ;
          child = (Solution) parentThread.crossover.execute(new Object[] {parentThread.population.get(subProblemId), parents});
          parentThread.mutation.execute(child);
        }

        parentThread.problem.evaluate(child);

        evaluations++;

        synchronized (parentThread) {
          updateIdealPoint(child);
          updateNeighborhood(child, subProblemId, neighborType);
        }
      }
    } while (evaluations < maxEvaluations);

    //long estimatedTime = System.currentTimeMillis() - parentThread.initTime;
    //JMetalLogger.logger.info("Time thread " + id_ + ": " + estimatedTime);
  }


  /** Execute() method */
  public SolutionSet execute() throws ClassNotFoundException {
    evaluations = 0 ;
    parentThread = this ;

    thread = new Thread[numberOfThreads];
    barrier = new CyclicBarrier(numberOfThreads);

    initializeUniformWeight();
    initializeNeighborhood();
    initializePopulation();
    initializeIdealPoint();

    for (int i = 0; i < numberOfThreads; i++) {
      thread[i] = new Thread(new ParallelMOEAD(builder, this, i, numberOfThreads), "");
      thread[i].start();
    }

    for (int i = 0; i < numberOfThreads; i++) {
      try {
        thread[i].join();
      } catch (InterruptedException ex) {
        java.util.logging.Logger.getLogger(ParallelMOEAD.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return population;
  }
}

