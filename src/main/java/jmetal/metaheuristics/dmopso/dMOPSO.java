/**
 * dMOPSO.java
 *
 * @version 1.0
 */
package jmetal.metaheuristics.dmopso;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.random.PseudoRandom;
import jmetal.util.wrapper.XReal;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

public class dMOPSO extends Algorithm {

  /**
   *
   */
  private static final long serialVersionUID = -7045622315440304387L;

  //static double y2;
  //static int use_last = 0;
  /**
   * Z vector (ideal point)
   */
  double[] z_;
  /**
   * Lambda vectors
   */
  double[][] lambda_;
  Solution[] indArray_;
  //"_PBI";//"_TCHE";//"_AGG";
  String functionType_ = "_PBI";
  String dataDirectory_;
  QualityIndicator indicators_;
  double r1Max_;
  double r1Min_;
  double r2Max_;
  double r2Min_;
  double c1Max_;
  double c1Min_;
  double c2Max_;
  double c2Min_;
  double weightMax_;
  double weightMin_;
  double changeVelocity1_;
  double changeVelocity2_;
  /**
   * Stores the number of particles_ used
   */
  private int swarmSize_;
  /**
   * Stores the maximum Age of each particle before reset
   */
  private int maxAge_;
  /**
   * Stores the maximum number of iteration_
   */
  private int maxIterations_;
  /**
   * Stores the current number of iteration_
   */
  private int iteration_;
  /**
   * Stores the particles
   */
  private SolutionSet particles_;
  /**
   * Stores the best_ solutions founds so far for each particles
   */
  private Solution[] lBest_;
  /**
   * Stores the global best solutions founds so far for each particles
   */
  private Solution[] gBest_;
  private int[] shfGBest_;
  /**
   * Stores the speed_ of each particle
   */
  private double[][] speed_;
  /**
   * Stores the age_ of each particle
   */
  private int[] age_;
  private double trueHypervolume_;
  private Hypervolume hy_;
  private SolutionSet trueFront_;
  private double deltaMax_[];
  private double deltaMin_[];

  public dMOPSO() {
    super();
    r1Max_ = 1.0;
    r1Min_ = 0.0;
    r2Max_ = 1.0;
    r2Min_ = 0.0;
    c1Max_ = 2.5;
    c1Min_ = 1.5;
    c2Max_ = 2.5;
    c2Min_ = 1.5;
    weightMax_ = 0.4;
    weightMin_ = 0.1;
    changeVelocity1_ = -1.0;
    changeVelocity2_ = -1.0;
  }

  public dMOPSO(
    Vector<Double> variables,
    String trueParetoFront) throws FileNotFoundException {
    super();

    r1Max_ = variables.get(0);
    r1Min_ = variables.get(1);
    r2Max_ = variables.get(2);
    r2Min_ = variables.get(3);
    c1Max_ = variables.get(4);
    c1Min_ = variables.get(5);
    c2Max_ = variables.get(6);
    c2Min_ = variables.get(7);
    weightMax_ = variables.get(8);
    weightMin_ = variables.get(9);
    changeVelocity1_ = variables.get(10);
    changeVelocity2_ = variables.get(11);

    // THIS IS GOING TO CRASH
    hy_ = new Hypervolume();
    jmetal.qualityIndicator.util.MetricsUtil mu = new jmetal.qualityIndicator.util.MetricsUtil();
    trueFront_ = mu.readNonDominatedSolutionSet(trueParetoFront);
    trueHypervolume_ = hy_.hypervolume(trueFront_.writeObjectivesToMatrix(),
      trueFront_.writeObjectivesToMatrix(),
      problem_.getNumberOfObjectives());

  }


  /**
   * Initialize all parameter of the algorithm
   */
  public void initParams() {
    swarmSize_ = (Integer) getInputParameter("swarmSize");
    maxIterations_ = (Integer) getInputParameter("maxIterations");
    maxAge_ = (Integer) getInputParameter("maxAge");
    indicators_ = (QualityIndicator) getInputParameter("indicators");
    dataDirectory_ = getInputParameter("dataDirectory").toString();

    String funcType = ((String) getInputParameter("functionType"));
    if (funcType != null && funcType != "") {
      functionType_ = funcType;
    }

    iteration_ = 0;

    particles_ = new SolutionSet(swarmSize_);
    lBest_ = new Solution[swarmSize_];
    gBest_ = new Solution[swarmSize_];
    shfGBest_ = new int[swarmSize_];

    // Create the speed_ vector
    speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];
    age_ = new int[swarmSize_];

    deltaMax_ = new double[problem_.getNumberOfVariables()];
    deltaMin_ = new double[problem_.getNumberOfVariables()];
    for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
      deltaMax_[i] = (problem_.getUpperLimit(i) -
        problem_.getLowerLimit(i)) / 2.0;
      deltaMin_[i] = -deltaMax_[i];
    }
  }

  // Adaptive inertia 
  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
    //return - (((wma-wmin)*(double)iter)/(double)miter);
    return wma;
  }

  // constriction coefficient (M. Clerc)
  private double constrictionCoefficient(double c1, double c2) {
    double rho = c1 + c2;
        if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
    }
  }

  // velocity bounds
  private double velocityConstriction(double v, double[] deltaMax,
    double[] deltaMin, int variableIndex,
    int particleIndex) throws IOException {

    double result;

    double dmax = deltaMax[variableIndex];
    double dmin = deltaMin[variableIndex];

    result = v;

    if (v > dmax) {
      result = dmax;
    }

    if (v < dmin) {
      result = dmin;
    }

    return result;
  }

  /**
   * Update the speed of each particle
   *
   * @throws JMException
   */
  private void computeSpeed(int i) throws JMException {
    double r1, r2, W, C1, C2;
    double wmax, wmin;

    XReal particle = new XReal(particles_.get(i));
    XReal bestParticle = new XReal(lBest_[i]);
    XReal bestGlobal = new XReal(gBest_[shfGBest_[i]]);

    r1 = PseudoRandom.randDouble(r1Min_, r1Max_);
    r2 = PseudoRandom.randDouble(r2Min_, r2Max_);
    C1 = PseudoRandom.randDouble(c1Min_, c1Max_);
    C2 = PseudoRandom.randDouble(c2Min_, c2Max_);
    W = PseudoRandom.randDouble(weightMin_, weightMax_);

    wmax = weightMax_;
    wmin = weightMin_;

    for (int var = 0; var < particle.size(); var++) {
      //Computing the velocity of this particle 
      try {
        speed_[i][var] = velocityConstriction(constrictionCoefficient(C1, C2) *
          (inertiaWeight(iteration_, maxIterations_, wmax, wmin) * speed_[i][var] +
            C1 * r1 * (bestParticle.getValue(var) -
              particle.getValue(var)) +
            C2 * r2 * (bestGlobal.getValue(var) -
              particle.getValue(var))), deltaMax_, deltaMin_, var, i);
      } catch (IOException e) {
        Configuration.logger_.log(Level.SEVERE, "Error", e);
      }
    }
  }

  /**
   * Update the position of each particle
   *
   * @throws JMException
   */
  private void computeNewPositions(int i) throws JMException {
    XReal particle = new XReal(particles_.get(i));
    for (int var = 0; var < particle.size(); var++) {
      particle.setValue(var, particle.getValue(var) + speed_[i][var]);
    }
  } 

  /**
   * Runs of the dMOPSO algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws JMException
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    //->Step 1.1 Initialize parameters (iteration_ = 0)
    initParams();

    //->Step 1.3 Generate a swarm of N random particles
    for (int i = 0; i < swarmSize_; i++) {
      Solution particle = new Solution(problem_);
      problem_.evaluate(particle);
      particles_.add(particle);
    }

    //-> Step 1.4 Initialize the speed_ and age_ of each particle to 0
    for (int i = 0; i < swarmSize_; i++) {
      for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
        speed_[i][j] = 0.0;
      }
      age_[i] = 0;
    }

    indArray_ = new Solution[problem_.getNumberOfObjectives()];
    z_ = new double[problem_.getNumberOfObjectives()];
    lambda_ = new double[swarmSize_][problem_.getNumberOfObjectives()];

    //-> Step 1.2 Generate a well-distributed set of N weighted vectors
    initUniformWeight();
    initIdealPoint();

    //-> Step 1.5 and 1.6 Define the personal best and the global best
    for (int i = 0; i < particles_.size(); i++) {
      Solution particle = new Solution(particles_.get(i));
      lBest_[i] = particle;
      gBest_[i] = particle;
    }
    updateGlobalBest();

    // Iterations ...        
    while (iteration_ < maxIterations_) {

      //-> Step 2 Suffle the global best
      shuffleGlobalBest();

      //-> Step 3 The cycle
      for (int i = 0; i < particles_.size(); i++) {

        if (age_[i] < maxAge_) {
          //-> Step 3.1 Update particle
          updateParticle(i);
        } else {
          //-> Step 3.2 Reset particle
          resetParticle(i);
        }

        //-> Step 3.3 Repair bounds
        repairBounds(i);

        //-> Step 3.4 Evaluate the particle and update Z*
        problem_.evaluate(particles_.get(i));
        updateReference(particles_.get(i));

        //-> Step 3.5 Update the personal best
        updateLocalBest(i);
      }

      //-> Step 4 Update the global best
      updateGlobalBest();
      iteration_++;
    }

    SolutionSet ss = new SolutionSet(gBest_.length);
    for (int i = 0; i < gBest_.length; i++) {
      ss.add(gBest_[i]);
    }

    return ss;
  } 

  private void shuffleGlobalBest() {
    int[] aux = new int[swarmSize_];
    int rnd;
    int tmp;

    for (int i = 0; i < swarmSize_; i++) {
      aux[i] = i;
    }

    for (int i = 0; i < swarmSize_; i++) {
      rnd = PseudoRandom.randInt(i, swarmSize_ - 1);
      tmp = aux[rnd];
      aux[rnd] = aux[i];
      shfGBest_[i] = tmp;
    }
  }

  private void repairBounds(int part) throws JMException {
    XReal particle = new XReal(particles_.get(part));

    for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
      if (particle.getValue(var) < problem_.getLowerLimit(var)) {
        particle.setValue(var, problem_.getLowerLimit(var));
        speed_[part][var] = speed_[part][var] * changeVelocity1_;
      }
      if (particle.getValue(var) > problem_.getUpperLimit(var)) {
        particle.setValue(var, problem_.getUpperLimit(var));
        speed_[part][var] = speed_[part][var] * changeVelocity2_;
      }
    }
  }

  private void resetParticle(int i) throws JMException {
    XReal particle = new XReal(particles_.get(i));
    double mean, sigma, N;

    for (int var = 0; var < particle.size(); var++) {
      XReal gB, pB;
      gB = new XReal(gBest_[shfGBest_[i]]);
      pB = new XReal(lBest_[i]);

      mean = (gB.getValue(var) - pB.getValue(var)) / 2;

      sigma = Math.abs(gB.getValue(var) - pB.getValue(var));

      java.util.Random rnd = new java.util.Random();

      N = rnd.nextGaussian() * sigma + mean; 
      
      particle.setValue(var, N);
      speed_[i][var] = 0.0;
    }
  } 

  private void updateParticle(int i) throws JMException {
    computeSpeed(i);
    computeNewPositions(i);
  }

  /**
   * initUniformWeight
   */
  private void initUniformWeight() {
    if ((problem_.getNumberOfObjectives() == 2) && (swarmSize_ < 300)) {
      for (int n = 0; n < swarmSize_; n++) {
        double a = 1.0 * n / (swarmSize_ - 1);
        lambda_[n][0] = a;
        lambda_[n][1] = 1 - a;
      }
    } else {
      String dataFileName;
      dataFileName = "W" + problem_.getNumberOfObjectives() + "D_" +
        swarmSize_ + ".dat";

      try {
        // Open the file
        FileInputStream fis =
          new FileInputStream(
            this.getClass().getClassLoader().getResource(dataDirectory_ + "/" + dataFileName)
              .getPath());
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        int numberOfObjectives = 0;
        int i = 0;
        int j = 0;
        String aux = br.readLine();
        while (aux != null) {
          StringTokenizer st = new StringTokenizer(aux);
          j = 0;
          numberOfObjectives = st.countTokens();
          while (st.hasMoreTokens()) {
            double value = new Double(st.nextToken());
            lambda_[i][j] = value;
            j++;
          }
          aux = br.readLine();
          i++;
        }
        br.close();
      } catch (Exception e) {
        Configuration.logger_.log(
          Level.SEVERE,
          "initUniformWeight: failed when reading for file: " + dataDirectory_ + "/" + dataFileName,
          e);
      }
    }
  }


  private void initIdealPoint() throws JMException, ClassNotFoundException {
    for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
      z_[i] = 1.0e+30;
      indArray_[i] = new Solution(problem_);
      problem_.evaluate(indArray_[i]);
    }

    for (int i = 0; i < swarmSize_; i++) {
      updateReference(particles_.get(i));
    }
  }

  private void updateReference(Solution individual) {
    for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
      if (individual.getObjective(n) < z_[n]) {
        z_[n] = individual.getObjective(n);

        indArray_[n] = new Solution(individual);
      }
    }
  }

  private void updateGlobalBest() throws JMException {

    double gBestFitness;

    for (int j = 0; j < lambda_.length; j++) {
      gBestFitness = fitnessFunction(gBest_[j], lambda_[j]);

      for (int i = 0; i < particles_.size(); i++) {
        double v1 = fitnessFunction(particles_.get(i), lambda_[j]);
        double v2 = gBestFitness;
        if (v1 < v2) {
          gBest_[j] = new Solution(particles_.get(i));
          gBestFitness = v1;
        }
      }
    }
  }

  private void updateLocalBest(int part) throws JMException {

    double f1, f2;
    Solution indiv = new Solution(particles_.get(part));

    f1 = fitnessFunction(lBest_[part], lambda_[part]);
    f2 = fitnessFunction(indiv, lambda_[part]);

    if (age_[part] >= maxAge_ || f2 <= f1) {
      lBest_[part] = indiv;
      age_[part] = 0;
    } else {
      age_[part]++;
    }
  }


  private double fitnessFunction(Solution sol, double[] lambda) throws JMException {
    double fitness = 0.0;

    if ("_TCHE".equals(functionType_)) {
      double maxFun = -1.0e+30;

      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
        double diff = Math.abs(sol.getObjective(n) - z_[n]);

        double feval;
        if (lambda[n] == 0) {
          feval = 0.0001 * diff;
        } else {
          feval = diff * lambda[n];
        }
        if (feval > maxFun) {
          maxFun = feval;
        }
      }

      fitness = maxFun;

    } else if ("_AGG".equals(functionType_)) {
      double sum = 0.0;
      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
        sum += (lambda[n]) * sol.getObjective(n);
      }

      fitness = sum;

    } else if ("_PBI".equals(functionType_)) {
      double d1, d2, nl;
      double theta = 5.0;

      d1 = d2 = nl = 0.0;

      for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
        d1 += (sol.getObjective(i) - z_[i]) * lambda[i];
        nl += Math.pow(lambda[i], 2.0);
      }
      nl = Math.sqrt(nl);
      d1 = Math.abs(d1) / nl;

      for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
        d2 += Math.pow((sol.getObjective(i) - z_[i]) - d1 * (lambda[i] / nl), 2.0);
      }
      d2 = Math.sqrt(d2);

      fitness = (d1 + theta * d2);

    } else {
      throw new JMException("dMOPSO.fitnessFunction: unknown type " + functionType_);
    }
    return fitness;
  }
}
