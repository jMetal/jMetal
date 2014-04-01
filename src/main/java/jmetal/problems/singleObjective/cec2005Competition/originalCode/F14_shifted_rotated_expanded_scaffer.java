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

public class F14_shifted_rotated_expanded_scaffer extends test_func {

	// Fixed (class) parameters
	static final public String FUNCTION_NAME = "Shifted Rotated Expanded Scaffer's F6 Function";
	static final public String DEFAULT_FILE_DATA = "supportData/E_ScafferF6_func_data.txt";
	static final public String DEFAULT_FILE_MX_PREFIX = "supportData/E_ScafferF6_M_D";
	static final public String DEFAULT_FILE_MX_SUFFIX = ".txt";

	// Shifted global optimum
	private final double[] m_o;
	private final double[][] m_matrix;

	// In order to avoid excessive memory allocation,
	// a fixed memory buffer is allocated for each function object.
	private double[] m_z;
	private double[] m_zM;

	// Constructors
	public F14_shifted_rotated_expanded_scaffer (int dimension, double bias) {
		this(dimension, bias, DEFAULT_FILE_DATA, DEFAULT_FILE_MX_PREFIX + dimension + DEFAULT_FILE_MX_SUFFIX);
	}
	public F14_shifted_rotated_expanded_scaffer (int dimension, double bias, String file_data, String file_m) {
		super(dimension, bias, FUNCTION_NAME);

		// Note: dimension starts from 0
		m_o = new double[m_dimension];
		m_matrix = new double[m_dimension][m_dimension];

		m_z = new double[m_dimension];
		m_zM = new double[m_dimension];

		// Load the shifted global optimum
		benchmark.loadRowVectorFromFile(file_data, m_dimension, m_o);
		// Load the matrix
		benchmark.loadMatrixFromFile(file_m, m_dimension, m_dimension, m_matrix);
	}

	// Function body
	public double f(double[] x) {

		double result = 0.0;

		benchmark.shift(m_z, x, m_o);
		benchmark.rotate(m_zM, m_z, m_matrix);

		result = benchmark.EScafferF6(m_zM);

		result += m_bias;

		return (result);
	}
}
