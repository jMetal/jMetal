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

public class F20RotatedHybridComposition2GlobalOptBound extends TestFunc {

  // Fixed (class) parameters
  static final public String FUNCTION_NAME =
    "Rotated Hybrid Composition Function 2 with Global Optimimum on the Bounds";
  static final public String DEFAULT_FILE_DATA = Benchmark.CEC2005SUPPORTDATADIRECTORY + "/hybrid_func2_data.txt";
  static final public String DEFAULT_FILE_MX_PREFIX = Benchmark.CEC2005SUPPORTDATADIRECTORY + "/hybrid_func2_M_D";
  static final public String DEFAULT_FILE_MX_SUFFIX = ".txt";

  // Number of functions
  static final public int NUM_FUNC = 10;

  private final MyHCJob theJob = new MyHCJob();

  // Shifted global optimum
  private final double[][] m_o;
  private final double[][][] m_M;
  private final double[] m_sigma = {
    1.0, 2.0, 1.5, 1.5, 1.0, 1.0,
    1.5, 1.5, 2.0, 2.0
  };
  private final double[] m_lambda = {
    2.0 * 5.0 / 32.0, 5.0 / 32.0, 2.0 * 1, 1.0, 2.0 * 5.0 / 100.0,
    5.0 / 100.0, 2.0 * 10.0, 10.0, 2.0 * 5.0 / 60.0, 5.0 / 60.0
  };
  private final double[] m_func_biases = {
    0.0, 100.0, 200.0, 300.0, 400.0,
    500.0, 600.0, 700.0, 800.0, 900.0
  };
  private final double[] m_testPoint;
  private final double[] m_testPointM;
  private final double[] m_fmax;

  // In order to avoid excessive memory allocation,
  // a fixed memory buffer is allocated for each function object.
  private double[] m_w;
  private double[][] m_z;
  private double[][] m_zM;

  // Constructors
  public F20RotatedHybridComposition2GlobalOptBound(int dimension, double bias)
    throws JMetalException {
    this(dimension, bias, DEFAULT_FILE_DATA,
      DEFAULT_FILE_MX_PREFIX + dimension + DEFAULT_FILE_MX_SUFFIX);
  }

  public F20RotatedHybridComposition2GlobalOptBound(int dimension, double bias, String file_data,
      String file_m) throws JMetalException {
    super(dimension, bias, FUNCTION_NAME);

    // Note: dimension starts from 0
    m_o = new double[NUM_FUNC][mDimension];
    m_M = new double[NUM_FUNC][mDimension][mDimension];

    m_testPoint = new double[mDimension];
    m_testPointM = new double[mDimension];
    m_fmax = new double[NUM_FUNC];

    m_w = new double[NUM_FUNC];
    m_z = new double[NUM_FUNC][mDimension];
    m_zM = new double[NUM_FUNC][mDimension];

    // Load the shifted global optimum
    Benchmark.loadMatrixFromFile(file_data, NUM_FUNC, mDimension, m_o);
    for (int i = 0; i < mDimension; i++) {
      m_o[9][i] = 0.0;
    }
    for (int i = 1; i < mDimension; i += 2) {
      m_o[0][i] = 5.0;
    }
    // Load the matrix
    Benchmark.loadNMatrixFromFile(file_m, NUM_FUNC, mDimension, mDimension, m_M);

    // Initialize the hybrid composition job object
    theJob.numberOfBasicFunctions = NUM_FUNC;
    theJob.numberOfDimensions = mDimension;
    theJob.C = 2000.0;
    theJob.sigma = m_sigma;
    theJob.biases = m_func_biases;
    theJob.lambda = m_lambda;
    theJob.shiftGlobalOptimum = m_o;
    theJob.linearTransformationMatrix = m_M;
    theJob.w = m_w;
    theJob.z = m_z;
    theJob.zM = m_zM;
    // Calculate/estimate the fmax for all the functions involved
    for (int i = 0; i < NUM_FUNC; i++) {
      for (int j = 0; j < mDimension; j++) {
        m_testPoint[j] = (5.0 / m_lambda[i]);
      }
      Benchmark.rotate(m_testPointM, m_testPoint, m_M[i]);
      m_fmax[i] = Math.abs(theJob.basicFunc(i, m_testPointM));
    }
    theJob.fmax = m_fmax;
  }

  // Function body
  public double f(double[] x) throws JMetalException {

    double result = 0.0;

    result = Benchmark.hybrid_composition(x, theJob);

    result += mBias;

    return (result);
  }


  private class MyHCJob extends HCJob {
    public double basicFunc(int func_no, double[] x) throws JMetalException {
      double result = 0.0;
      switch (func_no) {
        case 0:
        case 1:
          result = Benchmark.ackley(x);
          break;
        case 2:
        case 3:
          result = Benchmark.rastrigin(x);
          break;
        case 4:
        case 5:
          result = Benchmark.sphere(x);
          break;
        case 6:
        case 7:
          result = Benchmark.weierstrass(x);
          break;
        case 8:
        case 9:
          result = Benchmark.griewank(x);
          break;
        default:
          throw new JMetalException("func_no is out of range.");
      }
      return (result);
    }
  }
}
