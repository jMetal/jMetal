/**
 * DMOPSO.java
 *
 * @version 1.0
 */
package org.uma.jmetal.metaheuristic.multiobjective.dmopso;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.random.PseudoRandom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class DMOPSO implements Algorithm {
  private static final long serialVersionUID = -7045622315440304387L;

  private Problem problem ;

  double[] z;
  double[][] lambda;
  Solution[] indArray;

  //"_PBI";//"_TCHE";//"_AGG";
  private String functionType = "_PBI";
  private String dataDirectory;
  QualityIndicatorGetter indicators;

  private int swarmSize;
  private int maxAge;
  private int maxIterations;
  private int iteration;

  private double changeVelocity1 = -1.0 ;
  private double changeVelocity2 = -1.0 ;
  
  private SolutionSet swarm;
  private Solution[] localBest;
  private Solution[] globalBest;
  private int[] shfGBest;

  private double[][] speed;
  private int[] age;

  private double deltaMax[];
  private double deltaMin[];

  /** Constructor */
  private DMOPSO(Builder builder) {
    this.problem = builder.problem ;

    this.swarmSize = builder.swarmSize ;
    this.maxAge = builder.maxAge ;
    this.maxIterations = builder.maxIterations ;
    this.functionType = builder.functionType ;
    this.dataDirectory = builder.dataDirectory ;
    
    changeVelocity1 = -1.0;
    changeVelocity2 = -1.0;
  }
  
  /* Getters */
  public int getSwarmSize() {
  	return swarmSize ;
  }
  
  public int getMaxAge() {
  	return maxAge ;
  }
  
  public int getMaxIterations() {
  	return maxIterations ;
  }
  
  public String getFunctionType() {
  	return functionType ;
  }
  
  public String getDataDirectory() {
  	return dataDirectory ;
  }


  /** Builder class */
  public static class Builder {
    protected Problem problem;
    private int swarmSize;
    private int maxAge;
    private int maxIterations;
    private String functionType ;
    private String dataDirectory ;

    public Builder(Problem problem) {
      this.problem = problem ;

      swarmSize = 100 ;
      maxIterations = 250 ;
      maxAge = 2 ;
      functionType = "_TCHE" ;
      dataDirectory = "" ;
    }

    public Builder setSwarmSize(int swarmSize) {
      this.swarmSize = swarmSize ;

      return this ;
    }

    public Builder setMaxIterations(int maxIterations) {
      this.maxIterations = maxIterations ;

      return this ;
    }

    public Builder setMaxAge(int maxAge) {
      this.maxAge = maxAge ;

      return this ;
    }

    public Builder setFunctionType(String functionType) {
      this.functionType = functionType ;

      return this ;
    }

    public Builder setDataDirectory(String dataDirectory) {
      this.dataDirectory = dataDirectory ;

      return this ;
    }

    public DMOPSO build() {
      return new DMOPSO(this) ;
    }
  }

  /**
   * Initialize all parameter of the algorithm
   */
  public void initParams() {
    iteration = 0;

    swarm = new SolutionSet(swarmSize);
    localBest = new Solution[swarmSize];
    globalBest = new Solution[swarmSize];
    shfGBest = new int[swarmSize];

    // Create the speed vector
    speed = new double[swarmSize][problem.getNumberOfVariables()];
    age = new int[swarmSize];

    deltaMax = new double[problem.getNumberOfVariables()];
    deltaMin = new double[problem.getNumberOfVariables()];
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      deltaMax[i] = (problem.getUpperLimit(i) -
              problem.getLowerLimit(i)) / 2.0;
      deltaMin[i] = -deltaMax[i];
    }
  }

  // Adaptive inertia
  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
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
   * @throws org.uma.jmetal.util.JMetalException
   */
  private void computeSpeed(int i) throws JMetalException {
    double r1, r2, C1, C2;
    double wmax, wmin;

    XReal particle = new XReal(swarm.get(i));
    XReal bestParticle = new XReal(localBest[i]);
    XReal bestGlobal = new XReal(globalBest[shfGBest[i]]);

    r1 = PseudoRandom.randDouble(0, 1);
    r2 = PseudoRandom.randDouble(0, 1);
    C1 = PseudoRandom.randDouble(1.5, 2.5);
    C2 = PseudoRandom.randDouble(1.5, 2.5);

    wmax = 0.4;
    wmin = 0.1;

    for (int var = 0; var < particle.size(); var++) {
      try {
        speed[i][var] = velocityConstriction(constrictionCoefficient(C1, C2) *
                (inertiaWeight(iteration, maxIterations, wmax, wmin) * speed[i][var] +
                        C1 * r1 * (bestParticle.getValue(var) -
                                particle.getValue(var)) +
                        C2 * r2 * (bestGlobal.getValue(var) -
                                particle.getValue(var))), deltaMax, deltaMin, var, i);
      } catch (IOException e) {
        JMetalLogger.logger.log(Level.SEVERE, "Error", e);
      }
    }
  }

  /**
   * Update the position of each particle
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  private void computeNewPositions(int i) throws JMetalException {
    XReal particle = new XReal(swarm.get(i));
    for (int var = 0; var < particle.size(); var++) {
      particle.setValue(var, particle.getValue(var) + speed[i][var]);
    }
  }

  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    //->Step 1.1 Initialize parameters (iteration = 0)
    initParams();

    //->Step 1.3 Generate a swarm of N random particles
    for (int i = 0; i < swarmSize; i++) {
      Solution particle = new Solution(problem);
      problem.evaluate(particle);
      swarm.add(particle);
    }

    //-> Step 1.4 Initialize the speed and age of each particle to 0
    for (int i = 0; i < swarmSize; i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
      age[i] = 0;
    }

    indArray = new Solution[problem.getNumberOfObjectives()];
    z = new double[problem.getNumberOfObjectives()];
    lambda = new double[swarmSize][problem.getNumberOfObjectives()];

    //-> Step 1.2 Generate a well-distributed set of N weighted vectors
    initUniformWeight();
    initIdealPoint();

    //-> Step 1.5 and 1.6 Define the personal best and the global best
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      localBest[i] = particle;
      globalBest[i] = particle;
    }
    updateGlobalBest();

    // Iterations ...
    while (iteration < maxIterations) {

      //-> Step 2 Suffle the global best
      shuffleGlobalBest();

      //-> Step 3 The cycle
      for (int i = 0; i < swarm.size(); i++) {

        if (age[i] < maxAge) {
          //-> Step 3.1 Update particle
          updateParticle(i);
        } else {
          //-> Step 3.2 Reset particle
          resetParticle(i);
        }

        //-> Step 3.3 Repair bounds
        repairBounds(i);

        //-> Step 3.4 Evaluate the particle and update Z*
        problem.evaluate(swarm.get(i));
        updateReference(swarm.get(i));

        //-> Step 3.5 Update the personal best
        updateLocalBest(i);
      }

      //-> Step 4 Update the global best
      updateGlobalBest();
      iteration++;
    }

    SolutionSet ss = new SolutionSet(globalBest.length);
    for (int i = 0; i < globalBest.length; i++) {
      ss.add(globalBest[i]);
    }

    return ss;
  }

  private void shuffleGlobalBest() {
    int[] aux = new int[swarmSize];
    int rnd;
    int tmp;

    for (int i = 0; i < swarmSize; i++) {
      aux[i] = i;
    }

    for (int i = 0; i < swarmSize; i++) {
      rnd = PseudoRandom.randInt(i, swarmSize - 1);
      tmp = aux[rnd];
      aux[rnd] = aux[i];
      shfGBest[i] = tmp;
    }
  }

  private void repairBounds(int part) throws JMetalException {
    XReal particle = new XReal(swarm.get(part));

    for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
      if (particle.getValue(var) < problem.getLowerLimit(var)) {
        particle.setValue(var, problem.getLowerLimit(var));
        speed[part][var] = speed[part][var] * changeVelocity1;
      }
      if (particle.getValue(var) > problem.getUpperLimit(var)) {
        particle.setValue(var, problem.getUpperLimit(var));
        speed[part][var] = speed[part][var] * changeVelocity2;
      }
    }
  }

  private void resetParticle(int i) throws JMetalException {
    XReal particle = new XReal(swarm.get(i));
    double mean, sigma, N;

    for (int var = 0; var < particle.size(); var++) {
      XReal gB, pB;
      gB = new XReal(globalBest[shfGBest[i]]);
      pB = new XReal(localBest[i]);

      mean = (gB.getValue(var) - pB.getValue(var)) / 2;

      sigma = Math.abs(gB.getValue(var) - pB.getValue(var));

      java.util.Random rnd = new java.util.Random();

      N = rnd.nextGaussian() * sigma + mean;

      particle.setValue(var, N);
      speed[i][var] = 0.0;
    }
  }

  private void updateParticle(int i) throws JMetalException {
    computeSpeed(i);
    computeNewPositions(i);
  }

  /**
   * initializeUniformWeight
   */
  private void initUniformWeight() {
    if ((problem.getNumberOfObjectives() == 2) && (swarmSize <= 300)) {
      for (int n = 0; n < swarmSize; n++) {
        double a = 1.0 * n / (swarmSize - 1);
        lambda[n][0] = a;
        lambda[n][1] = 1 - a;
      }
    } else {
      String dataFileName;
      dataFileName = "W" + problem.getNumberOfObjectives() + "D_" +
              swarmSize + ".dat";

      try {
        // Open the file from the resources directory
        FileInputStream fis =
                new FileInputStream(
                        this.getClass().getClassLoader().getResource(dataDirectory + "/" + dataFileName)
                                .getPath());
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        int numberOfObjectives ;
        int i = 0;
        int j = 0;
        String aux = br.readLine();
        while (aux != null) {
          StringTokenizer st = new StringTokenizer(aux);
          j = 0;
          numberOfObjectives = st.countTokens();
          while (st.hasMoreTokens()) {
            double value = new Double(st.nextToken());
            lambda[i][j] = value;
            j++;
          }
          aux = br.readLine();
          i++;
        }
        br.close();
      } catch (Exception e) {
        throw new JMetalException("initializeUniformWeight: failed when reading for file: "
                + dataDirectory + "/" + dataFileName, e) ;
      }
    }
  }


  private void initIdealPoint() throws JMetalException, ClassNotFoundException {
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      z[i] = 1.0e+30;
      indArray[i] = new Solution(problem);
      problem.evaluate(indArray[i]);
    }

    for (int i = 0; i < swarmSize; i++) {
      updateReference(swarm.get(i));
    }
  }

  private void updateReference(Solution individual) {
    for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
      if (individual.getObjective(n) < z[n]) {
        z[n] = individual.getObjective(n);

        indArray[n] = new Solution(individual);
      }
    }
  }

  private void updateGlobalBest() throws JMetalException {

    double gBestFitness;

    for (int j = 0; j < lambda.length; j++) {
      gBestFitness = fitnessFunction(globalBest[j], lambda[j]);

      for (int i = 0; i < swarm.size(); i++) {
        double v1 = fitnessFunction(swarm.get(i), lambda[j]);
        double v2 = gBestFitness;
        if (v1 < v2) {
          globalBest[j] = new Solution(swarm.get(i));
          gBestFitness = v1;
        }
      }
    }
  }

  private void updateLocalBest(int part) throws JMetalException {

    double f1, f2;
    Solution indiv = new Solution(swarm.get(part));

    f1 = fitnessFunction(localBest[part], lambda[part]);
    f2 = fitnessFunction(indiv, lambda[part]);

    if (age[part] >= maxAge || f2 <= f1) {
      localBest[part] = indiv;
      age[part] = 0;
    } else {
      age[part]++;
    }
  }


  private double fitnessFunction(Solution sol, double[] lambda) throws JMetalException {
    double fitness = 0.0;

    if ("_TCHE".equals(functionType)) {
      double maxFun = -1.0e+30;

      for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
        double diff = Math.abs(sol.getObjective(n) - z[n]);

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

    } else if ("_AGG".equals(functionType)) {
      double sum = 0.0;
      for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
        sum += (lambda[n]) * sol.getObjective(n);
      }

      fitness = sum;

    } else if ("_PBI".equals(functionType)) {
      double d1, d2, nl;
      double theta = 5.0;

      d1 = d2 = nl = 0.0;

      for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
        d1 += (sol.getObjective(i) - z[i]) * lambda[i];
        nl += Math.pow(lambda[i], 2.0);
      }
      nl = Math.sqrt(nl);
      d1 = Math.abs(d1) / nl;

      for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
        d2 += Math.pow((sol.getObjective(i) - z[i]) - d1 * (lambda[i] / nl), 2.0);
      }
      d2 = Math.sqrt(d2);

      fitness = (d1 + theta * d2);

    } else {
      throw new JMetalException("DMOPSO.fitnessFunction: unknown type " + functionType);
    }
    return fitness;
  }
}
