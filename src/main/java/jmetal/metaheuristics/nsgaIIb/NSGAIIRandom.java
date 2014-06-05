package jmetal.metaheuristics.nsgaIIb;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.evaluator.SolutionSetEvaluator;
import jmetal.util.offspring.Offspring;
import jmetal.util.offspring.PolynomialMutationOffspring;
import jmetal.util.random.PseudoRandom;

public class NSGAIIRandom extends NSGAIITemplate {

  private static final long serialVersionUID = -9113018415834859888L;

  int[] contributionCounter_;
  double[] contribution_;

  double total = 0.0;

  public NSGAIIRandom(SolutionSetEvaluator evaluator) {
    super(evaluator);
  }

  public SolutionSet execute() throws JMException, ClassNotFoundException {
    double contrReal[] = new double[3];
    contrReal[0] = contrReal[1] = contrReal[2] = 0;

    Offspring[] getOffspring;
    int N_O; // number of offpring objects

    getOffspring = ((Offspring[]) getInputParameter("offspringsCreators"));
    N_O = getOffspring.length;

    contribution_ = new double[N_O];
    contributionCounter_ = new int[N_O];

    contribution_[0] = (double) (populationSize_ / (double) N_O) / (double) populationSize_;
    for (int i = 1; i < N_O; i++) {
      contribution_[i] = (double) (populationSize_ / (double) N_O) / (double) populationSize_
        + (double) contribution_[i - 1];
    }

    for (int i = 0; i < N_O; i++) {
      Configuration.logger_.info(getOffspring[i].configuration());
      Configuration.logger_.info("Contribution: " + contribution_[i]);
    }

    readParameterSettings();
    population_ = createInitialPopulation(populationSize_);
    evaluatePopulation(population_);


    for (int i = 0; i < populationSize_; i++) {
      population_.get(i).setLocation(i);
    }

    while (!stoppingCondition()) {

      // Create the offSpring solutionSet      
      offspringPopulation_ = new SolutionSet(populationSize_);
      Solution[] parents = new Solution[2];
      for (int i = 0; i < populationSize_; i++) {
        if (!stoppingCondition()) {
          Solution individual =
            new Solution(population_.get(PseudoRandom.randInt(0, populationSize_ - 1)));

          int selected = 0;
          boolean found = false;
          Solution offSpring = null;
          double rnd = PseudoRandom.randDouble();
          for (selected = 0; selected < N_O; selected++) {

            if (!found && (rnd <= contribution_[selected])) {
              if ("DE".equals(getOffspring[selected].id())) {
                offSpring = getOffspring[selected].getOffspring(population_, i);
              } else if ("SBXCrossover".equals(getOffspring[selected].id())) {
                offSpring = getOffspring[selected].getOffspring(population_);
              } else if ("PolynomialMutation".equals(getOffspring[selected].id())) {
                offSpring =
                  ((PolynomialMutationOffspring) getOffspring[selected]).getOffspring(individual);
              } else {
                Configuration.logger_.info("Error in NSGAIITRandom. Operator " + offSpring + " does not exist");
              }

              offSpring.setFitness((int) selected);
              found = true;
            }
          }

          problem_.evaluate(offSpring);
          offspringPopulation_.add(offSpring);
          evaluations_ += 1;
        }
      }

      Ranking ranking = rankPopulation() ;

      population_.clear();
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

    return getNonDominatedSolutions() ;
  }
}
