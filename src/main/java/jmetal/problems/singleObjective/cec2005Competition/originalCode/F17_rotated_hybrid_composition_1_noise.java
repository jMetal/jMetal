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

public class F17_rotated_hybrid_composition_1_noise extends test_func {

	// Fixed (class) parameters
	static final public String FUNCTION_NAME = "Rotated Hybrid Composition Function 1 with Noise in Fitness";
	static final public String DEFAULT_FILE_DATA = "supportData/hybrid_func1_data.txt";
	static final public String DEFAULT_FILE_MX_PREFIX = "supportData/hybrid_func1_M_D";
	static final public String DEFAULT_FILE_MX_SUFFIX = ".txt";

	// Number of functions
	static final public int NUM_FUNC = 10;

	private final MyHCJob theJob = new MyHCJob();

	// Shifted global optimum
	private final double[][] m_o;
	private final double[][][] m_M;
	private final double[] m_sigma = {
		1.0,	1.0,	1.0,	1.0,	1.0,
		1.0,	1.0,	1.0,	1.0,	1.0
	};
	private final double[] m_lambda = {
		1.0,		1.0,		10.0,		10.0,
		5.0/60.0,	5.0/60.0,	5.0/32.0,	5.0/32.0,
		5.0/100.0,	5.0/100.0
	};
	private final double[] m_func_biases = {
		0.0,	100.0,	200.0,	300.0,	400.0,
		500.0,	600.0,	700.0,	800.0,	900.0
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
	public F17_rotated_hybrid_composition_1_noise (int dimension, double bias) {
		this(dimension, bias, DEFAULT_FILE_DATA, DEFAULT_FILE_MX_PREFIX + dimension + DEFAULT_FILE_MX_SUFFIX);
	}
	public F17_rotated_hybrid_composition_1_noise (int dimension, double bias, String file_data, String file_m) {
		super(dimension, bias, FUNCTION_NAME);

		// Note: dimension starts from 0
		m_o = new double[NUM_FUNC][m_dimension];
		m_M = new double[NUM_FUNC][m_dimension][m_dimension];

		m_testPoint = new double[m_dimension];
		m_testPointM = new double[m_dimension];
		m_fmax = new double[NUM_FUNC];

		m_w = new double[NUM_FUNC];
		m_z = new double[NUM_FUNC][m_dimension];
		m_zM = new double[NUM_FUNC][m_dimension];

		// Load the shifted global optimum
		benchmark.loadMatrixFromFile(file_data, NUM_FUNC, m_dimension, m_o);
		// Load the matrix
		benchmark.loadNMatrixFromFile(file_m, NUM_FUNC, m_dimension, m_dimension, m_M);

		// Initialize the hybrid composition job object
		theJob.num_func = NUM_FUNC;
		theJob.num_dim = m_dimension;
		theJob.C = 2000.0;
		theJob.sigma = m_sigma;
		theJob.biases = m_func_biases;
		theJob.lambda = m_lambda;
		theJob.o = m_o;
		theJob.M = m_M;
		theJob.w = m_w;
		theJob.z = m_z;
		theJob.zM = m_zM;
		// Calculate/estimate the fmax for all the functions involved
		for (int i = 0 ; i < NUM_FUNC ; i ++) {
			for (int j = 0 ; j < m_dimension ; j ++) {
				m_testPoint[j] = (5.0 / m_lambda[i]);
			}
			benchmark.rotate(m_testPointM, m_testPoint, m_M[i]);
			m_fmax[i] = Math.abs(theJob.basic_func(i, m_testPointM));
		}
		theJob.fmax = m_fmax;
	}

	private class MyHCJob extends HCJob {
		public double basic_func(int func_no, double[] x) {
			double result = 0.0;
			switch(func_no) {
				case 0:
				case 1:
					result = benchmark.rastrigin(x);
					break;
				case 2:
				case 3:
					result = benchmark.weierstrass(x);
					break;
				case 4:
				case 5:
					result = benchmark.griewank(x);
					break;
				case 6:
				case 7:
					result = benchmark.ackley(x);
					break;
				case 8:
				case 9:
					result = benchmark.sphere(x);
					break;
				default:
					System.err.println("func_no is out of range.");
					System.exit(-1);
			}
			return (result);
		}
	}

	// Function body
	public double f(double[] x) {

		double result = 0.0;

		result = benchmark.hybrid_composition(x, theJob);

		result += m_bias;

		// NOISE
		// Comment the next line to remove the noise
		result *= (1.0 + 0.2 * Math.abs(benchmark.random.nextGaussian()));

		return (result);
	}
}
