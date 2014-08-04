package org.uma.jmetal.metaheuristic.multiobjective.nsgaII;

import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.comparator.CrowdingComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;
import org.uma.jmetal.util.offspring.Offspring;
import org.uma.jmetal.util.offspring.PolynomialMutationOffspring;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Comparator;

public class NSGAIIAdaptive extends NSGAIITemplate {
  private static final long serialVersionUID = 4290510927100994634L;

  public SolutionSet union;

  int[] contributionCounter;
  double[] contribution;

  public NSGAIIAdaptive(SolutionSetEvaluator evaluator) {
    super(evaluator);
  }

  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    // FIXME: those variables are necessary? because they only store value, we don't do anything with it
    double contrDE = 0;
    double contrSBX = 0;
    double contrPol = 0;
    double contrTotalDE = 0;
    double contrTotalSBX = 0;
    double contrTotalPol = 0;

    double contrReal[] = new double[3];
    contrReal[0] = contrReal[1] = contrReal[2] = 0;

    Comparator dominance = new DominanceComparator();
    Comparator crowdingComparator = new CrowdingComparator();
    Distance distance = new Distance();

    Operator selectionOperator;

    //Read parameter values
    populationSize = ((Integer) getInputParameter("populationSize")).intValue();
    //CR_ = ((Double) getInputParameter("CR")).doubleValue();
    //F_ = ((Double) getInputParameter("F")).doubleValue();
    maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();

    //Init the variables
    population = new SolutionSet(populationSize);
    evaluations = 0;

    selectionOperator = operators.get("selection");

    Offspring[] getOffspring;
    int N_O; // number of offpring objects

    getOffspring = ((Offspring[]) getInputParameter("offspringsCreators"));
    N_O = getOffspring.length;

    contribution = new double[N_O];
    contributionCounter = new int[N_O];

    contribution[0] = (double) (populationSize / (double) N_O) / (double) populationSize;
    for (int i = 1; i < N_O; i++) {
      contribution[i] = (double) (populationSize / (double) N_O) / (double) populationSize
        + (double) contribution[i - 1];
    }

    for (int i = 0; i < N_O; i++) {
      JMetalLogger.logger.info(getOffspring[i].configuration());
      JMetalLogger.logger.info("Contribution: " + contribution[i]);
    }

    createInitialPopulation();
    evaluatePopulation(population);


    for (int i = 0; i < populationSize; i++) {
      population.get(i).setLocation(i);
    }

    while (evaluations < maxEvaluations) {

      // Create the offSpring solutionSet
      offspringPopulation = new SolutionSet(populationSize);
      Solution[] parents = new Solution[2];
      for (int i = 0; i < populationSize; i++) {
        if (evaluations < maxEvaluations) {
          Solution individual =
            new Solution(population.get(PseudoRandom.randInt(0, populationSize - 1)));

          int selected = 0;
          boolean found = false;
          Solution offSpring = null;
          double rnd = PseudoRandom.randDouble();
          for (selected = 0; selected < N_O; selected++) {

            if (!found && (rnd <= contribution[selected])) {
              if ("DE".equals(getOffspring[selected].id())) {
                offSpring = getOffspring[selected].getOffspring(population, i);
                contrDE++;
              } else if ("SBXCrossover".equals(getOffspring[selected].id())) {
                offSpring = getOffspring[selected].getOffspring(population);
                contrSBX++;
              } else if ("PolynomialMutation".equals(getOffspring[selected].id())) {
                offSpring =
                  ((PolynomialMutationOffspring) getOffspring[selected]).getOffspring(individual);
                contrPol++;
              } else {
                JMetalLogger.logger.info("Error in NSGAIIAdaptive. Operator " + offSpring + " does not exist");
              }

              offSpring.setFitness((int) selected);
              found = true;
            }
          }

          problem.evaluate(offSpring);
          offspringPopulation.add(offSpring);
          evaluations += 1;
        }
      }

      // Create the solutionSet union of solutionSet and offSpring
      union = ((SolutionSet) population).union(offspringPopulation);

      // Ranking the union
      Ranking ranking = new Ranking(union);

      int remain = populationSize;
      int index = 0;
      SolutionSet front = null;
      population.clear();

      // Obtain the next front
      front = ranking.getSubfront(index);

      while ((remain > 0) && (remain >= front.size())) {
        //Assign crowding distance to individuals
        distance.crowdingDistanceAssignment(front);
        //Add the individuals of this front
        for (int k = 0; k < front.size(); k++) {
          population.add(front.get(k));
        } // for

        //Decrement remain
        remain = remain - front.size();

        //Obtain the next front
        index++;
        if (remain > 0) {
          front = ranking.getSubfront(index);
        }
      }

      // Remain is less than front(index).size, insert only the best one
      if (remain > 0) {  // front contains individuals to insert
        distance.crowdingDistanceAssignment(front);
        front.sort(new CrowdingComparator());
        for (int k = 0; k < remain; k++) {
          population.add(front.get(k));
        }

        remain = 0;
      }


      // CONTRIBUTION CALCULATING PHASE
      // First: reset contribution counter
      for (int i = 0; i < N_O; i++) {
        contributionCounter[i] = 0;
      }

      // Second: determine the contribution of each operator
      for (int i = 0; i < population.size(); i++) {
        if ((int) population.get(i).getFitness() != -1) {
          contributionCounter[(int) population.get(i).getFitness()] += 1;
        }
        population.get(i).setFitness(-1);
      }

      contrTotalDE += contributionCounter[0];
      contrTotalSBX += contributionCounter[1];
      contrTotalPol += contributionCounter[2];

      int minimumContribution = 2;
      int totalContributionCounter = 0;

      for (int i = 0; i < N_O; i++) {
        if (contributionCounter[i] < minimumContribution) {
          contributionCounter[i] = minimumContribution;
        }
        totalContributionCounter += contributionCounter[i];
      }

      if (totalContributionCounter == 0) {
        for (int i = 0; i < N_O; i++) {
          contributionCounter[i] = 10;
        }
      }

      // Third: calculating contribution
      contribution[0] = contributionCounter[0] * 1.0
        / (double) totalContributionCounter;
      for (int i = 1; i < N_O; i++) {
        contribution[i] = contribution[i - 1] + 1.0
          * contributionCounter[i]
          / (double) totalContributionCounter;
      }
    }

    // Return the first non-dominated front
    Ranking ranking = new Ranking(population);
    return ranking.getSubfront(0);
  }
}
