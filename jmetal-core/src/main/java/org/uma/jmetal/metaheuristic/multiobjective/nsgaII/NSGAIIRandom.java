package org.uma.jmetal.metaheuristic.multiobjective.nsgaII;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;
import org.uma.jmetal.util.offspring.Offspring;
import org.uma.jmetal.util.offspring.PolynomialMutationOffspring;
import org.uma.jmetal.util.random.PseudoRandom;

public class NSGAIIRandom extends NSGAIITemplate {
  private static final long serialVersionUID = -9113018415834859888L;

  int[] contributionCounter;
  double[] contribution;

  double total = 0.0;

  public NSGAIIRandom(SolutionSetEvaluator evaluator) {
    super(evaluator);
  }

  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    double contrReal[] = new double[3];
    contrReal[0] = contrReal[1] = contrReal[2] = 0;

    Offspring[] getOffspring;
    int numberOfOffspringObjects;

    getOffspring = ((Offspring[]) getInputParameter("offspringsCreators"));
    numberOfOffspringObjects = getOffspring.length;

    contribution = new double[numberOfOffspringObjects];
    contributionCounter = new int[numberOfOffspringObjects];

    contribution[0] = (double) (populationSize / (double) numberOfOffspringObjects) / (double) populationSize;
    for (int i = 1; i < numberOfOffspringObjects; i++) {
      contribution[i] = (double) (populationSize / (double) numberOfOffspringObjects) / (double) populationSize
        + (double) contribution[i - 1];
    }

    for (int i = 0; i < numberOfOffspringObjects; i++) {
      JMetalLogger.logger.info(getOffspring[i].configuration());
      JMetalLogger.logger.info("Contribution: " + contribution[i]);
    }

    readParameterSettings();
    createInitialPopulation();
    evaluatePopulation(population);


    for (int i = 0; i < populationSize; i++) {
      population.get(i).setLocation(i);
    }

    while (!stoppingCondition()) {

      // Create the offSpring solutionSet      
      offspringPopulation = new SolutionSet(populationSize);
      Solution[] parents = new Solution[2];
      for (int i = 0; i < populationSize; i++) {
        if (!stoppingCondition()) {
          Solution individual =
            new Solution(population.get(PseudoRandom.randInt(0, populationSize - 1)));

          int selected = 0;
          boolean found = false;
          Solution offSpring = null;
          double rnd = PseudoRandom.randDouble();
          for (selected = 0; selected < numberOfOffspringObjects; selected++) {

            if (!found && (rnd <= contribution[selected])) {
              if ("DE".equals(getOffspring[selected].id())) {
                offSpring = getOffspring[selected].getOffspring(population, i);
              } else if ("SBXCrossover".equals(getOffspring[selected].id())) {
                offSpring = getOffspring[selected].getOffspring(population);
              } else if ("PolynomialMutation".equals(getOffspring[selected].id())) {
                offSpring =
                  ((PolynomialMutationOffspring) getOffspring[selected]).getOffspring(individual);
              } else {
                JMetalLogger.logger.info("Error in NSGAIITRandom. Operator " + offSpring + " does not exist");
              }

              offSpring.setFitness((int) selected);
              found = true;
            }
          }

          problem_.evaluate(offSpring);
          offspringPopulation.add(offSpring);
          evaluations += 1;
        }
      }

      Ranking ranking = rankPopulation() ;

      population.clear();
      int rankingIndex = 0 ;
      while (populationIsNotFull()) {
        if (subfrontFillsIntoThePopulation(ranking, rankingIndex)) {
          addRankedSolutionsToPopulation(ranking, rankingIndex);
          rankingIndex ++ ;
        } else {
          computeCrowdingDistance(ranking, rankingIndex) ;
          addLastRankedSolutions(ranking, rankingIndex);
        }
      }
    }

    tearDown() ;

    return getNonDominatedSolutions(population) ;
  }
}
