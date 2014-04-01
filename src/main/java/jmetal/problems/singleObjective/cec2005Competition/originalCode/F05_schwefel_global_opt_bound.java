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
// Java version of the test functions
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
// Typical use of the test functions in the benchmark:
//
//		// Create a benchmark object
// 		benchmark theBenchmark = new benchmark();
//		// Use the factory function call to create a test function object
//		//		test function 3 with 50 dimension
//		//		the object class is "test_func"
//		test_func aTestFunc = theBenchmark.testFunctionFactory(3, 50);
//		// Invoke the function with x
//		double result = aTestFunc.f(x);
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
import java.io.*;
import java.util.*;

public class F05_schwefel_global_opt_bound extends test_func {

	// Fixed (class) parameters
	static final public String FUNCTION_NAME = "Schwefel's Problem 2.6 with Global Optimum on Bounds";
	static final public String DEFAULT_FILE_DATA = "supportData/schwefel_206_data.txt";

	// Shifted global optimum
	private final double[] m_o;
	private final double[][] m_A;

	// In order to avoid excessive memory allocation,
	// a fixed memory buffer is allocated for each function object.
	private double[] m_B;
	private double[] m_z;

	// Constructors
	public F05_schwefel_global_opt_bound (int dimension, double bias) {
		this(dimension, bias, DEFAULT_FILE_DATA);
	}
	public F05_schwefel_global_opt_bound (int dimension, double bias, String file_data) {
		super(dimension, bias, FUNCTION_NAME);

		// Note: dimension starts from 0
		m_o = new double[m_dimension];
		m_A = new double[m_dimension][m_dimension];

		m_B = new double[m_dimension];
		m_z = new double[m_dimension];

		double[][] m_data = new double[m_dimension+1][m_dimension];

		// Load the shifted global optimum
		benchmark.loadMatrixFromFile(file_data, m_dimension+1, m_dimension, m_data);
		for (int i = 0 ; i < m_dimension ; i ++) {
			if ((i+1) <= Math.ceil(m_dimension / 4.0))
				m_o[i] = -100.0;
			else if ((i+1) >= Math.floor((3.0 * m_dimension) / 4.0))
				m_o[i] = 100.0;
			else
				m_o[i] = m_data[0][i];
		}
		for (int i = 0 ; i < m_dimension ; i ++) {
			for (int j = 0 ; j < m_dimension ; j ++) {
				m_A[i][j] = m_data[i+1][j];
			}
		}
		benchmark.Ax(m_B, m_A, m_o);
	}

	// Function body
	public double f(double[] x) {

		double max = Double.NEGATIVE_INFINITY;

		benchmark.Ax(m_z, m_A, x);

		for (int i = 0 ; i < m_dimension ; i ++) {
			double temp = Math.abs(m_z[i] - m_B[i]);
			if (max < temp)
				max = temp;
		}

		return (max + m_bias);
	}
}
