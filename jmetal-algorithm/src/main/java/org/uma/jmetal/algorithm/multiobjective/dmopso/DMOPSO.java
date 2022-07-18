package org.uma.jmetal.algorithm.multiobjective.dmopso;

import static java.lang.Double.parseDouble;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@SuppressWarnings("serial")
public class DMOPSO implements Algorithm<List<DoubleSolution>> {

  public enum FunctionType {
    TCHE, PBI, AGG
  }

  private String name;
  private DoubleProblem problem;
  private List<DoubleSolution> swarm ;

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
  protected int maxAge ;

  private DoubleSolution[] localBest ;
  private DoubleSolution[] globalBest ;
  private int[] shfGBest ;
  private double[][] speed;
  private int[] age;
  double[] z;
  double[][] lambda;
  DoubleSolution[] indArray;

  private double deltaMax[];
  private double deltaMin[];

  String dataDirectory ;

  FunctionType functionType = FunctionType.PBI ;

  private JMetalRandom randomGenerator;
  private SolutionListEvaluator<DoubleSolution> evaluator;

  public DMOPSO(@NotNull DoubleProblem problem, int swarmSize,
                int maxIterations, double r1Min, double r1Max,
                double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max,
                double weightMin, double weightMax, double changeVelocity1, double changeVelocity2,
                FunctionType functionType, String dataDirectory, int maxAge) {
      this(problem, swarmSize,
           maxIterations, r1Min, r1Max,
           r2Min, r2Max, c1Min, c1Max, c2Min, c2Max,
           weightMin, weightMax, changeVelocity1, changeVelocity2,
           functionType, dataDirectory, maxAge, "dMOPSO");
  }
  
  public DMOPSO(DoubleProblem problem, int swarmSize,
                int maxIterations, double r1Min, double r1Max,
                double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max,
                double weightMin, double weightMax, double changeVelocity1, double changeVelocity2,
                FunctionType functionType, String dataDirectory, int maxAge, String name) {
    this.name = name;
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.maxIterations = maxIterations;

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
    this.functionType = functionType ;
    this.maxAge = maxAge ;

    this.dataDirectory = dataDirectory ;

    evaluator = new SequentialSolutionListEvaluator<DoubleSolution>();

    randomGenerator = JMetalRandom.getInstance() ;

    localBest = new DoubleSolution[swarmSize] ;
    globalBest = new DoubleSolution[swarmSize] ;
    shfGBest = new int[swarmSize] ;
    speed = new double[swarmSize][problem.getNumberOfVariables()];
    age = new int[swarmSize] ;

    indArray = new DoubleSolution[problem.getNumberOfObjectives()];
    z = new double[problem.getNumberOfObjectives()];
    lambda = new double[swarmSize][problem.getNumberOfObjectives()];

    deltaMax = new double[problem.getNumberOfVariables()];
    deltaMin = new double[problem.getNumberOfVariables()];
    for (var i = 0; i < problem.getNumberOfVariables(); i++) {
      var bounds = problem.getVariableBounds().get(i) ;
      deltaMax[i] = (bounds.getUpperBound() - bounds.getLowerBound()) / 2.0 ;
      deltaMin[i] = -deltaMax[i];
    }
  }

  public List<DoubleSolution> getSwarm() {
    return swarm ;
  }

  protected void initProgress() {
    iterations = 1 ;
  }

  protected void updateProgress() {
    iterations ++ ;
  }

  protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations ;
  }

  protected List<DoubleSolution> createInitialSwarm() {
    @NotNull List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (var i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      swarm.add(newSolution);
    }

    return swarm;
  }

  protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    swarm = evaluator.evaluate(swarm, problem);

    return swarm;
  }

  protected void initializeLeaders(List<DoubleSolution> swarm) {
    for (var i = 0; i < getSwarm().size(); i++) {
      var particle = (DoubleSolution)getSwarm().get(i).copy() ;
      globalBest[i] = particle;
    }

    updateGlobalBest();
  }

  protected void initializeParticlesMemory(List<DoubleSolution> swarm) {
    for (var i = 0; i < getSwarm().size(); i++) {
      @Nullable DoubleSolution particle = (DoubleSolution)getSwarm().get(i).copy() ;
      localBest[i] = particle;
    }
  }

  protected void initializeVelocity(List<DoubleSolution> swarm) {
    // Initialize the speed and age of each particle to 0
    for (var i = 0; i < swarmSize; i++) {
      for (var j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
      age[i] = 0;
    }
  }

  protected void updateVelocity(int i) {

    var particle = getSwarm().get(i) ;
    var bestParticle = localBest[i] ;
    var bestGlobal = globalBest[shfGBest[i]] ;

    var r1 = randomGenerator.nextDouble(r1Min, r1Max);
    var r2 = randomGenerator.nextDouble(r2Min, r2Max);
    var C1 = randomGenerator.nextDouble(c1Min, c1Max);
    var C2 = randomGenerator.nextDouble(c2Min, c2Max);
    
    for (var var = 0; var < particle.variables().size(); var++) {
      //Computing the velocity of this particle
        speed[i][var] = velocityConstriction(constrictionCoefficient(C1, C2) *
                (inertiaWeight(iterations, maxIterations, this.weightMax, this.weightMin) * speed[i][var] +
                        C1 * r1 * (bestParticle.variables().get(var) -
                                particle.variables().get(var)) +
                        C2 * r2 * (bestGlobal.variables().get(var) -
                                particle.variables().get(var))), deltaMax, deltaMin, var, i) ;

    }
  }

  private void computeNewPositions(int i) {
    var particle = getSwarm().get(i) ;
    for (var var = 0; var < particle.variables().size(); var++) {
      particle.variables().set(var, particle.variables().get(var) + speed[i][var]) ;
    }
  }

  /**
   * initUniformWeight
   */
  protected void initializeUniformWeight() {
    if ((problem.getNumberOfObjectives() == 2) && (swarmSize <= 300)) {
      for (var n = 0; n < swarmSize; n++) {
        var a = 1.0 * n / (swarmSize - 1);
        lambda[n][0] = a;
        lambda[n][1] = 1 - a;
      }
    } else {
      var dataFileName = "W" + problem.getNumberOfObjectives() + "D_" +
              swarmSize + ".dat";

      try {

        //       String path =
        // Paths.get(VectorFileUtils.class.getClassLoader().getResource(filePath).toURI()).toString
        // ();
        var path = "/" + dataDirectory + "/" + dataFileName;

        @Nullable InputStream inputStream =
            getClass()
                .getClassLoader()
                .getResourceAsStream(path);
        if (inputStream == null) {
          inputStream = new FileInputStream(dataDirectory + "/" + dataFileName);
        }
        @NotNull InputStreamReader isr = new InputStreamReader(inputStream);
        var br = new BufferedReader(isr);

        var i = 0;
        var j = 0;
        var aux = br.readLine();
        while (aux != null) {
          var st = new StringTokenizer(aux);
          j = 0;
          while (st.hasMoreTokens()) {
            var value = parseDouble(st.nextToken());
            lambda[i][j] = value;
            j++;
          }
          aux = br.readLine();
          i++;
        }
        br.close();
      } catch (Exception e) {
        throw new JMetalException("initializeUniformWeight: failed when reading for file: "
            + dataDirectory + "/" + dataFileName, e);
      }
    }
  }



  private void initIdealPoint()  {
    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      z[i] = 1.0e+30;
      indArray[i] = problem.createSolution() ;
      problem.evaluate(indArray[i]);
    }

    for (var i = 0; i < swarmSize; i++) {
      updateReference(getSwarm().get(i));
    }
  }

  private void updateReference(DoubleSolution individual) {
    for (var n = 0; n < problem.getNumberOfObjectives(); n++) {
      if (individual.objectives()[n] < z[n]) {
        z[n] = individual.objectives()[n];

        indArray[n] = (DoubleSolution)individual.copy() ;
      }
    }
  }

  private void updateGlobalBest() {

    double gBestFitness ;

    for(var j = 0; j<lambda.length; j++){
      gBestFitness = fitnessFunction(globalBest[j], lambda[j]) ;

      for (var i = 0; i < getSwarm().size(); i++) {
        var v1 = fitnessFunction(getSwarm().get(i), lambda[j]) ;
        var v2 = gBestFitness ;
        if (v1 < v2) {
          globalBest[j] = (DoubleSolution)getSwarm().get(i).copy() ;
          gBestFitness = v1;
        }
      }
    }
  }

  private void updateLocalBest(int part) {

    var indiv = (DoubleSolution)getSwarm().get(part).copy();

    var f1 = fitnessFunction(localBest[part], lambda[part]);
    var f2 = fitnessFunction(indiv, lambda[part]);

    if(age[part] >= maxAge || f2 <= f1){
      localBest[part] = indiv;
      age[part] = 0;
    }else{
      age[part]++;
    }
  }

  private double fitnessFunction(@NotNull DoubleSolution sol, double[] lambda){
    var fitness = 0.0;

    if (functionType == FunctionType.TCHE) {
      var maxFun = -1.0e+30;

      for (var n = 0; n < problem.getNumberOfObjectives(); n++) {
        var diff = Math.abs(sol.objectives()[n] - z[n]);

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

    }else if(functionType == FunctionType.AGG){
      var sum = 0.0;
      var bound = problem.getNumberOfObjectives();
      for (var n = 0; n < bound; n++) {
        var v = (lambda[n]) * sol.objectives()[n];
        sum += v;
      }

      fitness = sum;

    }else if(functionType == FunctionType.PBI){
      double d1, d2, nl;
      var theta = 5.0;

      d1 = d2 = nl = 0.0;

      for (var i = 0; i < problem.getNumberOfObjectives(); i++)
      {
        d1 += (sol.objectives()[i] - z[i]) * lambda[i];
        nl += Math.pow(lambda[i], 2.0);
      }
      nl = Math.sqrt(nl);
      d1 = Math.abs(d1) / nl;

      for (var i = 0; i < problem.getNumberOfObjectives(); i++)
      {
        d2 += Math.pow((sol.objectives()[i] - z[i]) - d1 * (lambda[i] / nl), 2.0);
      }
      d2 = Math.sqrt(d2);

      fitness = (d1 + theta * d2);

    }else{
      System.out.println("dMOPSO.fitnessFunction: unknown type " + functionType);
      System.exit(-1);
    }
    return fitness;
  }

  private void shuffleGlobalBest(){
    int rnd;
    int tmp;

    var arr = new int[10];
    var count = 0;
    var bound = swarmSize;
    for (var i1 = 0; i1 < bound; i1++) {
      if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
      arr[count++] = i1;
    }
    arr = Arrays.copyOfRange(arr, 0, count);
    var aux = arr;

    for (var i = 0; i < swarmSize; i++) {
      rnd = randomGenerator.nextInt(i, swarmSize - 1);
      tmp = aux[rnd];
      aux[rnd] = aux[i];
      shfGBest[i] = tmp;
    }
  }

  private void repairBounds(int part){

    var particle = getSwarm().get(part) ;

    for(var var = 0; var < particle.variables().size(); var++){
      var bounds = problem.getVariableBounds().get(var) ;
      var lowerBound = bounds.getLowerBound() ;
      var upperBound = bounds.getUpperBound() ;
      if (particle.variables().get(var) < lowerBound) {
        particle.variables().set(var, lowerBound);
        speed[part][var] = speed[part][var] * changeVelocity1;
      }
      if (particle.variables().get(var) > upperBound) {
        particle.variables().set(var, upperBound);
        speed[part][var] = speed[part][var] * changeVelocity2;
      }
    }
  }

  private void resetParticle(int i) {
    var particle = getSwarm().get(i) ;
    double mean, sigma, N;

    for (var var = 0; var < particle.variables().size(); var++) {
      var gB = globalBest[shfGBest[i]];
      var pB = localBest[i];

      mean = (gB.variables().get(var) - pB.variables().get(var))/2;

      sigma = Math.abs(gB.variables().get(var) - pB.variables().get(var));

      var rnd = new java.util.Random();

      N = rnd.nextGaussian()*sigma + mean;

      particle.variables().set(var,N);
      speed[i][var] = 0.0;
    }
  }

  private double velocityConstriction(double v, double @NotNull [] deltaMax, double[] deltaMin,
                                      int variableIndex, int particleIndex) {

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
  public void run() {
    swarm = createInitialSwarm() ;
    evaluateSwarm(swarm) ;
    initializeVelocity(getSwarm());

    initializeUniformWeight();
    initIdealPoint();

    initializeLeaders(getSwarm());
    initializeParticlesMemory(getSwarm());

    updateGlobalBest();

    initProgress();
    while (!isStoppingConditionReached()) {
      shuffleGlobalBest();

      for (var i = 0; i < getSwarm().size(); i++) {
        if (age[i] < maxAge) {
          updateVelocity(i);
          computeNewPositions(i);
        } else {
          resetParticle(i);
        }
       
        repairBounds(i);

        problem.evaluate(swarm.get(i));
        updateReference(swarm.get(i));
        updateLocalBest(i);
      }
      updateGlobalBest();
      updateProgress();
    }
  }

  @Override
  public List<DoubleSolution> getResult() {
    return Arrays.asList(globalBest) ;
  }

  @Override public String getName() {
    return this.name ;
  }

  @Override public @NotNull String getDescription() {
    return "MOPSO with decomposition" ;
  }
}