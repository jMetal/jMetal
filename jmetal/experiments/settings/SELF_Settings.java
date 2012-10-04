/**
 * SELF_Settings.java
 *
 * @author Antonio J. Nebro
 * @author Juan J. Durillo
 * @version 1.0
 *
 * SELF_Settings class of algorithm SELF
 */
package jmetal.experiments.settings;

import java.util.Properties;
import jmetal.core.*;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.self.SELF;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;
import jmetal.util.offspring.DifferentialEvolutionOffspring;
import jmetal.util.offspring.Offspring;
import jmetal.util.offspring.PolynomialOffspringGenerator;
import jmetal.util.offspring.SBXCrossoverAndPolynomialMutation;

/**
 * @author Antonio
 */
public class SELF_Settings extends Settings {
  public int populationSize_            = 40;
  
  int archiveSize_                      ;
  int maxEvaluations_                   ;
  int archiveFeedback_                  ;
  boolean applyMutation_                ; // Polynomial mutation
  double distributionIndexForMutation_  ;
  double distributionIndexForCrossover_ ;
  double crossoverProbability_          ;
  double mutationProbability_           ;

  /**
   * Constructor
   */
  public SELF_Settings(String problem) {
    super(problem);
    
    Object [] problemParams = {"Real"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }      
    archiveSize_                   = 100;
    maxEvaluations_                = 150000;
    archiveFeedback_               = 10;
    applyMutation_                 = true; // Polynomial mutation
    distributionIndexForMutation_  = 20;
    distributionIndexForCrossover_ = 20;
    crossoverProbability_          = 0.9;
    mutationProbability_           = 1.0 / problem_.getNumberOfVariables();
  } // SELF_Settings
  
  /**
   * Constructor
   */
  /*
  public SELF_Settings(String problem, String paretoFrontFile) {
    super(problem);
    this.paretoFrontFile_ = paretoFrontFile;
  } // CellDE_Settings  
  */
  
  /**
   * Configure the algorithm with the specified parameter settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;
    Operator selection;
    Operator crossover;
    Operator mutation;

    QualityIndicator indicators;

    algorithm = new SELF(problem_) ;

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
    algorithm.setInputParameter("feedback", archiveFeedback_);

    Offspring[] getOffspring = new Offspring[3];
    double CR, F;
    getOffspring[0] = new DifferentialEvolutionOffspring(CR = 1.0, F = 0.5);
    
    getOffspring[1] = new SBXCrossoverAndPolynomialMutation(
      1.0 / problem_.getNumberOfVariables(), // mutation probability
      0.9, // crossover probability
      20, // distribution index for mutation
      20); // distribution index for crossover

    getOffspring[2] = new PolynomialOffspringGenerator(1.0/problem_.getNumberOfVariables(), 20);

    algorithm.setInputParameter("offspringsCreators", getOffspring);

    // Creating the indicator object
    if ((paretoFrontFile_!=null) && (!paretoFrontFile_.equals(""))) {
      indicators = new QualityIndicator(problem_, paretoFrontFile_);
      algorithm.setInputParameter("indicators", indicators);
    } // if

    return algorithm;
  }
} // SELF_Settings

