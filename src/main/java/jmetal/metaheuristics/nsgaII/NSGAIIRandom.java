package jmetal.metaheuristics.nsgaII;

import jmetal.core.*;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.Ranking;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.offspring.Offspring;
import jmetal.util.offspring.PolynomialMutationOffspring;

public class NSGAIIRandom extends Algorithm {
  public int populationSize_            ;
  public SolutionSet population_        ;
  public SolutionSet offspringPopulation_;
  public SolutionSet union_              ;

  int maxEvaluations_                   ;
  int evaluations_                      ;

  int[] contributionCounter_; // contribution per crossover operator
  double[] contribution_; // contribution per crossover operator
  double total = 0.0;

  Operator selectionOperator_;

  public NSGAIIRandom(Problem problem) {
    super(problem);
  }

  public SolutionSet execute() throws JMException, ClassNotFoundException {
    double contrReal [] = new double[3] ;
    contrReal[0] = contrReal[1] = contrReal[2] = 0 ;    

    Distance distance = new Distance();

    //Read parameter values
    populationSize_ = ((Integer) getInputParameter("populationSize")).intValue();
    maxEvaluations_ = ((Integer) getInputParameter("maxEvaluations")).intValue();

    //Init the variables
    population_ = new SolutionSet(populationSize_);
    evaluations_ = 0;

    selectionOperator_ = operators_.get("selection");

    Offspring[] getOffspring;
    int N_O; // number of offpring objects

    getOffspring = ((Offspring[]) getInputParameter("offspringsCreators"));
    N_O = getOffspring.length;

    contribution_               = new double[N_O];
    contributionCounter_        = new int[N_O];

    contribution_[0] = (double) (populationSize_ / (double) N_O) / (double) populationSize_;
    for (int i = 1; i < N_O; i++) {
      contribution_[i] = (double) (populationSize_ / (double) N_O) / (double) populationSize_ + (double) contribution_[i - 1];
    }

    for (int i = 0; i < N_O; i++) {
      System.out.println(getOffspring[i].configuration()) ;
      System.out.println("Contribution: " + contribution_[i]) ;
    }

    // Create the initial solutionSet
    Solution newSolution;
    for (int i = 0; i < populationSize_; i++) {
      newSolution = new Solution(problem_);
      problem_.evaluate(newSolution);
      problem_.evaluateConstraints(newSolution);
      evaluations_++;
      newSolution.setLocation(i);
      population_.add(newSolution);
    } //for       

    while (evaluations_ < maxEvaluations_) {

      // Create the offSpring solutionSet      
      offspringPopulation_ = new SolutionSet(populationSize_);
      Solution[] parents = new Solution[2];
      for (int i = 0; i < (populationSize_ / 1); i++) {
        if (evaluations_ < maxEvaluations_) {
          Solution individual = new Solution(population_.get(PseudoRandom.randInt(0, populationSize_-1)));
          //  				Solution individual = new Solution(population_.get(i));

          int selected = 0;
          boolean found = false ;
          Solution offSpring = null;
          double rnd = PseudoRandom.randDouble();
          for (selected = 0; selected < N_O; selected++) {

            if (!found && (rnd <= contribution_[selected])) {
              if ("DE".equals(getOffspring[selected].id())) {
                offSpring = getOffspring[selected].getOffspring(population_, i) ;
                //contrDE++;
              } else if ("SBXCrossover".equals(getOffspring[selected].id())) {
                offSpring = getOffspring[selected].getOffspring(population_);
                //contrSBX++;
              } else if ("PolynomialMutation".equals(getOffspring[selected].id())) {
                offSpring = ((PolynomialMutationOffspring)getOffspring[selected]).getOffspring(individual);
                //contrPol++;
              } else {
                System.out.println("Error in NSGAIIAdaptive. Operator " + offSpring + " does not exist") ;
              }

              offSpring.setFitness((int) selected);
              found = true;
            } // if
          } // for

          problem_.evaluate(offSpring) ;
          offspringPopulation_.add(offSpring);
          evaluations_ +=1 ; 
        } // if                            
      } // for

      // Create the solutionSet union of solutionSet and offSpring
      union_ = ((SolutionSet) population_).union(offspringPopulation_);

      // Ranking the union
      Ranking ranking = new Ranking(union_);

      int remain = populationSize_;
      int index = 0;
      SolutionSet front = null;
      population_.clear();

      // Obtain the next front
      front = ranking.getSubfront(index);

      while ((remain > 0) && (remain >= front.size())) {
        //Assign crowding distance to individuals
        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
        //Add the individuals of this front
        for (int k = 0; k < front.size(); k++) {
          population_.add(front.get(k));
        } // for

        //Decrement remain
        remain = remain - front.size();

        //Obtain the next front
        index++;
        if (remain > 0) {
          front = ranking.getSubfront(index);
        } // if        
      } // while

      // Remain is less than front(index).size, insert only the best one
      if (remain > 0) {  // front contains individuals to insert                        
        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
        front.sort(new CrowdingComparator());
        for (int k = 0; k < remain; k++) {
          population_.add(front.get(k));
        } // for

      } // if                 
    } // while


    // Return the first non-dominated front
    Ranking ranking = new Ranking(population_);
    return ranking.getSubfront(0);
  }
}
