//
// Special Session on Real-Parameter Optimization at CEC-05
// Edinburgh, UK, 2-5 Sept. 2005
//
// Organizers:
//	Prof. Kalyanmoy Deb
//		deb@iitk.ac.in
//		http://www.iitk.ac.in/kangal/deb.htm
//	A/Prof. P. N. Suganthan
//		epnsugan@ntu.edu.sg
//		http://www.ntu.edu.sg/home/EPNSugan
//
// Java version of the org.uma.test functions
//
// Matlab reference code
//	http://www.ntu.edu.sg/home/EPNSugan
//
// Java version developer:
//	Assistant Prof. Ying-ping Chen
//		Department of Computer Science
//		National Chiao Tung University
//		HsinChu City, Taiwan
//		ypchen@csie.nctu.edu.tw
//		http://www.csie.nctu.edu.tw/~ypchen/
//
// Typical use of the org.uma.test functions in the Benchmark:
//
//		// Create a Benchmark object
// 		Benchmark theBenchmark = new Benchmark();
//		// Use the factory function call to create a org.uma.test function object
//		//		org.uma.test function 3 with 50 dimension
//		//		the object class is "TestFunc"
//		TestFunc aTestFunc = theBenchmark.testFunctionFactory(3, 50);
//		// Invoke the function with x
//		double experimentoutput = aTestFunc.f(x);
//
// Version 0.90
//		Currently, this version cannot handle any numbers of dimensions.
//		It cannot generate the shifted global optima and rotation matrices
//		that are not provided with the Matlab reference code.
//		It can handle all cases whose data files are provided with
//		the Matlab reference code.
// Version 0.91
//		Revised according to the Matlab reference code and the PDF document
//		dated March 8, 2005.
//

package org.uma.jmetal.problem.singleobjective.cec2005competitioncode;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class Benchmark {
  // Fixed (class) parameters
  static final public String CEC2005SUPPORTDATADIRECTORY = "cec2005CompetitionResources/supportData" ;
  static final private String CEC2005Code = "org.uma.jmetal.problem.singleobjective.cec2005competitioncode" ;
  static final public int NUM_TEST_FUNC = 25;
  static final public String DEFAULT_FILE_BIAS = CEC2005SUPPORTDATADIRECTORY + "/fbias_data.txt";
  static final public String[] test_func_class_names = {
    "F01ShiftedSphere",
    "F02ShiftedSchwefel",
    "F03ShiftedRotatedHighCondElliptic",
    "F04ShiftedSchwefelNoise",
    "F05SchwefelGlobalOptBound",
    "F06ShiftedRosenbrock",
    "F07ShiftedRotatedGriewank",
    "F08ShiftedRotatedAckleyGlobalOptBound",
    "F09ShiftedRastrigin",
    "F10ShiftedRotatedRastrigin",
    "F11ShiftedRotatedWeierstrass",
    "F12Schwefel",
    "F13ShiftedExpandedGriewankRosenbrock",
    "F14ShiftedRotatedExpandedScaffer",
    "F15HybridComposition1",
    "F16RotatedHybridComposition1",
    "F17RotatedHybridComposition1Noise",
    "F18RotatedHybridComposition2",
    "F19RotatedHybridComposition2NarrowBasinGlobalOpt",
    "F20RotatedHybridComposition2GlobalOptBound",
    "F21RotatedHybridComposition3",
    "F22RotatedHybridComposition3HighCondNumMatrix",
    "F23NoncontinuousRotatedHybridComposition3",
    "F24RotatedHybridComposition4",
    "F25RotatedHybridComposition4Bound"
  };

  // For certain functions, some essential data can be calculated beforehand.
  // Hence, a maximum supported number of dimensions should be specified.
  // Specifiy the number of dimensions here if you need more.
  static final public int MAX_SUPPORT_DIM = 100;
  static final public double PIx2 = Math.PI * 2.0;

  // Formatter for the number representation
  static final public DecimalFormat scientificFormatter =
    new DecimalFormat("0.0000000000000000E00");
  static final public DecimalFormat numberFormatter = scientificFormatter;
  static final public DecimalFormat percentageFormatter = new DecimalFormat("0.0000000000");

  // Random number generator
  // If you want to plan a specific seed, do it here.
  static final public Random random = new Random();

  // Class loader & reflection
  static final public ClassLoader loader = ClassLoader.getSystemClassLoader();
  static final Class<?>[] test_func_arg_types = {int.class, double.class};

  // Class variables
  static private double[] m_iSqrt;

  // Instance variables
  private double[] m_biases;

  // Constructors
  //	Load the data common to all org.uma.test functions.
  //	Get ready for creating org.uma.test function instances.
  public Benchmark() throws JMetalException {
    this(DEFAULT_FILE_BIAS);
  }

  public Benchmark(String file_bias) throws JMetalException {
    m_biases = new double[NUM_TEST_FUNC];
    m_iSqrt = new double[MAX_SUPPORT_DIM];

    loadRowVectorFromFile(file_bias, NUM_TEST_FUNC, m_biases);

    for (int i = 0; i < MAX_SUPPORT_DIM; i++) {
      m_iSqrt[i] = Math.sqrt(((double) i) + 1.0);
    }
  }

  // Entry point
  //	If the Benchmark class is executed as a stand-alone application,
  //	its job is to run the org.uma.test on all the org.uma.test functions.
  static public void main(String args[]) throws JMetalException {
    Benchmark theBenchmark = new Benchmark();
    theBenchmark.runTest(0);
  }

  // Sphere function
  static public double sphere(double[] x) {

    double sum = 0.0;

    for (int i = 0; i < x.length; i++) {
      sum += x[i] * x[i];
    }

    return (sum);
  }

  // Sphere function with noise
  static public double sphere_noise(double[] x) {

    double sum = 0.0;

    for (int i = 0; i < x.length; i++) {
      sum += x[i] * x[i];
    }

    // NOISE
    // Comment the next line to remove the noise
    sum *= (1.0 + 0.1 * Math.abs(random.nextGaussian()));

    return (sum);
  }

  // Schwefel's problem 1.2
  static public double schwefel_102(double[] x) {

    double prev_sum, curr_sum, outer_sum;

    curr_sum = x[0];
    outer_sum = (curr_sum * curr_sum);

    for (int i = 1; i < x.length; i++) {
      prev_sum = curr_sum;
      curr_sum = prev_sum + x[i];
      outer_sum += (curr_sum * curr_sum);
    }

    return (outer_sum);
  }

  //
  // Basic functions
  //

  // Rosenbrock's function
  static public double rosenbrock(double[] x) {

    double sum = 0.0;

    for (int i = 0; i < (x.length - 1); i++) {
      double temp1 = (x[i] * x[i]) - x[i + 1];
      double temp2 = x[i] - 1.0;
      sum += (100.0 * temp1 * temp1) + (temp2 * temp2);
    }

    return (sum);
  }

  // F2: Rosenbrock's Function -- 2D version
  static public double F2(double x, double y) {
    double temp1 = (x * x) - y;
    double temp2 = x - 1.0;
    return ((100.0 * temp1 * temp1) + (temp2 * temp2));
  }

  // Griewank's function
  static public double griewank(double[] x) {

    double sum = 0.0;
    double product = 1.0;

    for (int i = 0; i < x.length; i++) {
      sum += ((x[i] * x[i]) / 4000.0);
      product *= Math.cos(x[i] / m_iSqrt[i]);
    }

    return (sum - product + 1.0);
  }

  // F8: Griewank's Function -- 1D version
  static public double F8(double x) {
    return (((x * x) / 4000.0) - Math.cos(x) + 1.0);
  }

  // Ackley's function
  static public double ackley(double[] x) {

    double sum1 = 0.0;
    double sum2 = 0.0;

    for (int i = 0; i < x.length; i++) {
      sum1 += (x[i] * x[i]);
      sum2 += (Math.cos(PIx2 * x[i]));
    }

    return (-20.0 * Math.exp(-0.2 * Math.sqrt(sum1 / ((double) x.length))) - Math
      .exp(sum2 / ((double) x.length)) + 20.0 + Math.E);
  }

  // Round function
  // 0. Use the Matlab version for rounding numbers
  static public double myRound(double x) {
    return (Math.signum(x) * Math.round(Math.abs(x)));
  }

  // 1. "o" is provided
  static public double myXRound(double x, double o) {
    return ((Math.abs(x - o) < 0.5) ? x : (myRound(2.0 * x) / 2.0));
  }

  // 2. "o" is not provided
  static public double myXRound(double x) {
    return ((Math.abs(x) < 0.5) ? x : (myRound(2.0 * x) / 2.0));
  }

  // Rastrigin's function
  static public double rastrigin(double[] x) {

    double sum = 0.0;

    for (int i = 0; i < x.length; i++) {
      sum += (x[i] * x[i]) - (10.0 * Math.cos(PIx2 * x[i])) + 10.0;
    }

    return (sum);
  }

  // Non-Continuous Rastrigin's function
  static public double rastriginNonCont(double[] x) {

    double sum = 0.0;
    double currX;

    for (int i = 0; i < x.length; i++) {
      currX = myXRound(x[i]);
      sum += (currX * currX) - (10.0 * Math.cos(PIx2 * currX)) + 10.0;
    }

    return (sum);
  }

  // Weierstrass function
  static public double weierstrass(double[] x) {
    return (weierstrass(x, 0.5, 3.0, 20));
  }

  static public double weierstrass(double[] x, double a, double b, int Kmax) {

    double sum1 = 0.0;
    for (int i = 0; i < x.length; i++) {
      for (int k = 0; k <= Kmax; k++) {
        sum1 += Math.pow(a, k) * Math.cos(PIx2 * Math.pow(b, k) * (x[i] + 0.5));
      }
    }

    double sum2 = 0.0;
    for (int k = 0; k <= Kmax; k++) {
      sum2 += Math.pow(a, k) * Math.cos(PIx2 * Math.pow(b, k) * (0.5));
    }

    return (sum1 - sum2 * ((double) (x.length)));
  }

  // F8F2
  static public double F8F2(double[] x) {

    double sum = 0.0;

    for (int i = 1; i < x.length; i++) {
      sum += F8(F2(x[i - 1], x[i]));
    }
    sum += F8(F2(x[x.length - 1], x[0]));

    return (sum);
  }

  // Scaffer's F6 function
  static public double ScafferF6(double x, double y) {
    double temp1 = x * x + y * y;
    double temp2 = Math.sin(Math.sqrt(temp1));
    double temp3 = 1.0 + 0.001 * temp1;
    return (0.5 + ((temp2 * temp2 - 0.5) / (temp3 * temp3)));
  }

  // Expanded Scaffer's F6 function
  static public double EScafferF6(double[] x) {

    double sum = 0.0;

    for (int i = 1; i < x.length; i++) {
      sum += ScafferF6(x[i - 1], x[i]);
    }
    sum += ScafferF6(x[x.length - 1], x[0]);

    return (sum);
  }

  // Non-Continuous Expanded Scaffer's F6 function
  static public double EScafferF6NonCont(double[] x) {

    double sum = 0.0;
    double prevX, currX;

    currX = myXRound(x[0]);
    for (int i = 1; i < x.length; i++) {
      prevX = currX;
      currX = myXRound(x[i]);
      sum += ScafferF6(prevX, currX);
    }
    prevX = currX;
    currX = myXRound(x[0]);
    sum += ScafferF6(prevX, currX);

    return (sum);
  }

  // Elliptic
  static public double elliptic(double[] x) {

    double sum = 0.0;
    double a = 1e6;

    for (int i = 0; i < x.length; i++) {
      sum += Math.pow(a, (((double) i) / ((double) (x.length - 1)))) * x[i] * x[i];
    }

    return (sum);
  }

  // Hybrid composition
  static public double hybrid_composition(double[] x, HCJob job) throws JMetalException {

    int num_func = job.numberOfBasicFunctions;
    int num_dim = job.numberOfDimensions;

    // Get the raw weights
    double wMax = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < num_func; i++) {
      double sumSqr = 0.0;
      shift(job.z[i], x, job.shiftGlobalOptimum[i]);
      for (int j = 0; j < num_dim; j++) {
        sumSqr += (job.z[i][j] * job.z[i][j]);
      }
      job.w[i] = Math.exp(-1.0 * sumSqr / (2.0 * num_dim * job.sigma[i] * job.sigma[i]));
      if (wMax < job.w[i]) {
        wMax = job.w[i];
      }
    }

    // Modify the weights
    double wSum = 0.0;
    double w1mMaxPow = 1.0 - Math.pow(wMax, 10.0);
    for (int i = 0; i < num_func; i++) {
      if (job.w[i] != wMax) {
        job.w[i] *= w1mMaxPow;
      }
      wSum += job.w[i];
    }

    // Normalize the weights
    for (int i = 0; i < num_func; i++) {
      job.w[i] /= wSum;
    }

    double sumF = 0.0;
    for (int i = 0; i < num_func; i++) {
      for (int j = 0; j < num_dim; j++) {
        job.z[i][j] /= job.lambda[i];
      }
      rotate(job.zM[i], job.z[i], job.linearTransformationMatrix[i]);
      sumF +=
        job.w[i] *
          (
            job.C * job.basicFunc(i, job.zM[i]) / job.fmax[i] +
              job.biases[i]
          );
    }
    return (sumF);
  }

  // Shift
  static public void shift(double[] results, double[] x, double[] o) {
    for (int i = 0; i < x.length; i++) {
      results[i] = x[i] - o[i];
    }
  }

  // Rotate
  static public void rotate(double[] results, double[] x, double[][] matrix) {
    xA(results, x, matrix);
  }

  // (1xD) row vector * (Dx1) column vector = (1) scalar
  static public double xy(double[] x, double[] y) {
    double result = 0.0;
    for (int i = 0; i < x.length; i++) {
      result += (x[i] * y[i]);
    }

    return (result);
  }

  //
  // Elementary operations
  //

  // (1xD) row vector * (DxD) matrix = (1xD) row vector
  static public void xA(double[] result, double[] x, double[][] A) {
    for (int i = 0; i < result.length; i++) {
      result[i] = 0.0;
      for (int j = 0; j < result.length; j++) {
        result[i] += (x[j] * A[j][i]);
      }
    }
  }

  // (DxD) matrix * (Dx1) column vector = (Dx1) column vector
  static public void Ax(double[] result, double[][] A, double[] x) {
    for (int i = 0; i < result.length; i++) {
      result[i] = 0.0;
      for (int j = 0; j < result.length; j++) {
        result[i] += (A[i][j] * x[j]);
      }
    }
  }

  //
  // Matrix & vector operations
  //

  //
  // Utility functions for loading data from the given text file
  //
  static public void loadTestDataFromFile(String file, int num_test_points, int test_dimension,
    double[][] x, double[] f) throws JMetalException {
    try {
      JMetalLogger.logger.info("File bias: " + file);

      BufferedReader brSrc = new BufferedReader(new FileReader(file));
      loadMatrix(brSrc, num_test_points, test_dimension, x);
      loadColumnVector(brSrc, num_test_points, f);
      brSrc.close();
    } catch (Exception e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error in Benchmark.java", e);
      throw new JMetalException("Error in Benchmark.java");
    }
  }

  static public void loadRowVectorFromFile(String file, int columns, double[] row)
    throws JMetalException {
    try {
      BufferedReader brSrc =
              new BufferedReader(
                      new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource(file).getPath()))) ;
      //BufferedReader brSrc = new BufferedReader(new FileReader(file));
      loadRowVector(brSrc, columns, row);
      brSrc.close();
    } catch (Exception e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error in Benchmark.java", e);
      throw new JMetalException("Error in Benchmark.java");
    }
  }

  static public void loadRowVector(BufferedReader brSrc, int columns, double[] row)
    throws Exception {
    String stToken;
    StringTokenizer stTokenizer = new StringTokenizer(brSrc.readLine());
    for (int i = 0; i < columns; i++) {
      stToken = stTokenizer.nextToken();
      row[i] = Double.parseDouble(stToken);
    }
  }

  static public void loadColumnVectorFromFile(String file, int rows, double[] column)
    throws JMetalException {
    try {
      BufferedReader brSrc =
              new BufferedReader(
                      new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource(file).getPath()))) ;
      //BufferedReader brSrc = new BufferedReader(new FileReader(file));
      loadColumnVector(brSrc, rows, column);
      brSrc.close();
    } catch (Exception e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error in Benchmark.java", e);
      throw new JMetalException("Error in Benchmark.java");
    }
  }

  static public void loadColumnVector(BufferedReader brSrc, int rows, double[] column)
    throws Exception {
    String stToken;
    for (int i = 0; i < rows; i++) {
      StringTokenizer stTokenizer = new StringTokenizer(brSrc.readLine());
      stToken = stTokenizer.nextToken();
      column[i] = Double.parseDouble(stToken);
    }
  }

  static public void loadNMatrixFromFile(String file, int N, int rows, int columns,
    double[][][] matrix) throws JMetalException {
    try {
      BufferedReader brSrc =
              new BufferedReader(
                      new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource(file).getPath()))) ;
      //BufferedReader brSrc = new BufferedReader(new FileReader(file));
      for (int i = 0; i < N; i++) {
        loadMatrix(brSrc, rows, columns, matrix[i]);
      }
      brSrc.close();
    } catch (Exception e) {
      throw new JMetalException("Error in Benchmark.java", e);
    }
  }

  static public void loadMatrixFromFile(String file, int rows, int columns, double[][] matrix)
    throws JMetalException {
    try {
      BufferedReader brSrc =
              new BufferedReader(
                      new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource(file).getPath()))) ;
      //BufferedReader brSrc = new BufferedReader(new FileReader(file));
      loadMatrix(brSrc, rows, columns, matrix);
      brSrc.close();
    } catch (Exception e) {
      throw new JMetalException("Error in Benchmark.java", e);
    }
  }

  static public void loadMatrix(BufferedReader brSrc, int rows, int columns, double[][] matrix)
    throws Exception {
    for (int i = 0; i < rows; i++) {
      loadRowVector(brSrc, columns, matrix[i]);
    }
  }

  //
  // Use this function to manufacture new test function objects
  //
  public TestFunc testFunctionFactory(int func_num, int dimension) throws JMetalException {
    TestFunc returnFunc = null;
    try {
      returnFunc = (TestFunc)
        loader.loadClass(CEC2005Code + "." + test_func_class_names[func_num - 1])
          .getConstructor(test_func_arg_types)
          .newInstance(
            new Object[] {
              new Integer(dimension),
              new Double(m_biases[func_num - 1])
            }
          );
    } catch (Exception e) {
      throw new JMetalException("Error in Benchmark.java", e);
    }
    return (returnFunc);
  }

  // Run tests on the test functions
  //	< 0:	Error!
  //	= 0:	On all the functions
  //	> 0:	On the specified org.uma.test function
  public void runTest() throws JMetalException {
    runTest(0);
  }

  public void runTest(int func_num) throws JMetalException {
    if (func_num == 0) {
      for (int i = 1; i <= NUM_TEST_FUNC; i++) {
        runTest(i);
      }
    } else if ((func_num < 0) || (func_num > NUM_TEST_FUNC)) {
      throw new JMetalException("The specified func_num is out of range.");
    } else {
      // Run the org.uma.test function against the check points
      int num_test_points = 10;
      int test_dimension = 50;

      double[] test_f = new double[num_test_points];
      double[][] test_x = new double[num_test_points][test_dimension];

      String file_test = "testData/test_data_func" + func_num + ".txt";

      // Create the org.uma.test function object
      TestFunc aFunc = testFunctionFactory(func_num, test_dimension);

      JMetalLogger.logger.info("Run tests on function " + func_num +
        " (" + aFunc.name() + "):");
      JMetalLogger.logger.info("  " +
        num_test_points + " " +
        aFunc.dimension() + "-dimension check points");

      loadTestDataFromFile(file_test, num_test_points, test_dimension, test_x, test_f);

      for (int i = 0; i < num_test_points; i++) {
        // Execute the org.uma.test function
        // Collect and compare the results
        double result = aFunc.f(test_x[i]);
        double diff = result - test_f[i];
        double ratio = Math.abs(diff / test_f[i]);
        JMetalLogger.logger.info("    " +
          numberFormatter.format(result) +
          " - " +
          numberFormatter.format(test_f[i]) +
          " = " +
          numberFormatter.format(diff));
        JMetalLogger.logger.info("    " + "    " +
          "Difference ratio = " + numberFormatter.format(ratio));
        if (ratio != 0.0) {
          if (ratio <= 1e-12) {
            JMetalLogger.logger.info(" (<= 1E-12)");
          } else {
            JMetalLogger.logger.info(" (> 1E-12) *****");
          }
        } else {
          JMetalLogger.logger.info("");
        }
      }
    }
  }
}
