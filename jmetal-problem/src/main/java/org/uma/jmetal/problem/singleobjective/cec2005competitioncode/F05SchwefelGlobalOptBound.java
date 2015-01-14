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

public class F05SchwefelGlobalOptBound extends TestFunc {

  // Fixed (class) parameters
  static final public String FUNCTION_NAME = "Schwefel's Problem 2.6 with Global Optimum on Bounds";
  static final public String DEFAULT_FILE_DATA = Benchmark.CEC2005SUPPORTDATADIRECTORY + "/schwefel_206_data.txt";

  // Shifted global optimum
  private final double[] m_o;
  private final double[][] m_A;

  // In order to avoid excessive memory allocation,
  // a fixed memory buffer is allocated for each function object.
  private double[] m_B;
  private double[] m_z;

  // Constructors
  public F05SchwefelGlobalOptBound(int dimension, double bias) throws JMetalException {
    this(dimension, bias, DEFAULT_FILE_DATA);
  }

  public F05SchwefelGlobalOptBound(int dimension, double bias, String file_data)
    throws JMetalException {
    super(dimension, bias, FUNCTION_NAME);

    // Note: dimension starts from 0
    m_o = new double[mDimension];
    m_A = new double[mDimension][mDimension];

    m_B = new double[mDimension];
    m_z = new double[mDimension];

    double[][] m_data = new double[mDimension + 1][mDimension];

    // Load the shifted global optimum
    Benchmark.loadMatrixFromFile(file_data, mDimension + 1, mDimension, m_data);
    for (int i = 0; i < mDimension; i++) {
      if ((i + 1) <= Math.ceil(mDimension / 4.0)) {
        m_o[i] = -100.0;
      } else if ((i + 1) >= Math.floor((3.0 * mDimension) / 4.0)) {
        m_o[i] = 100.0;
      } else {
        m_o[i] = m_data[0][i];
      }
    }
    for (int i = 0; i < mDimension; i++) {
      System.arraycopy(m_data[i + 1], 0, m_A[i], 0, mDimension);
    }
    Benchmark.Ax(m_B, m_A, m_o);
  }

  // Function body
  public double f(double[] x) {

    double max = Double.NEGATIVE_INFINITY;

    Benchmark.Ax(m_z, m_A, x);

    for (int i = 0; i < mDimension; i++) {
      double temp = Math.abs(m_z[i] - m_B[i]);
      if (max < temp) {
        max = temp;
      }
    }

    return (max + mBias);
  }
}
