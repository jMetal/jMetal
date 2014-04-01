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
import java.text.*;
import java.lang.reflect.*;

public class benchmark {

	// Fixed (class) parameters
	static final public int NUM_TEST_FUNC = 25;
	static final public String DEFAULT_FILE_BIAS = "supportData/fbias_data.txt";
	static final public String[] test_func_class_names = {
		"F01_shifted_sphere",
		"F02_shifted_schwefel",
		"F03_shifted_rotated_high_cond_elliptic",
		"F04_shifted_schwefel_noise",
		"F05_schwefel_global_opt_bound",
		"F06_shifted_rosenbrock",
		"F07_shifted_rotated_griewank",
		"F08_shifted_rotated_ackley_global_opt_bound",
		"F09_shifted_rastrigin",
		"F10_shifted_rotated_rastrigin",
		"F11_shifted_rotated_weierstrass",
		"F12_schwefel",
		"F13_shifted_expanded_griewank_rosenbrock",
		"F14_shifted_rotated_expanded_scaffer",
		"F15_hybrid_composition_1",
		"F16_rotated_hybrid_composition_1",
		"F17_rotated_hybrid_composition_1_noise",
		"F18_rotated_hybrid_composition_2",
		"F19_rotated_hybrid_composition_2_narrow_basin_global_opt",
		"F20_rotated_hybrid_composition_2_global_opt_bound",
		"F21_rotated_hybrid_composition_3",
		"F22_rotated_hybrid_composition_3_high_cond_num_matrix",
		"F23_noncontinuous_rotated_hybrid_composition_3",
		"F24_rotated_hybrid_composition_4",
		"F25_rotated_hybrid_composition_4_bound"
	};

	// For certain functions, some essential data can be calculated beforehand.
	// Hence, a maximum supported number of dimensions should be specified.
	// Specifiy the number of dimensions here if you need more.
	static final public int MAX_SUPPORT_DIM = 100;
	static final public double PIx2 = Math.PI * 2.0;

	// Formatter for the number representation
	static final public DecimalFormat scientificFormatter = new DecimalFormat("0.0000000000000000E00");
	static final public DecimalFormat numberFormatter = scientificFormatter;
	static final public DecimalFormat percentageFormatter = new DecimalFormat("0.0000000000");

	// Random number generator
	// If you want to plan a specific seed, do it here.
	static final public Random random = new Random();

	// Class loader & reflection
	static final public ClassLoader loader = ClassLoader.getSystemClassLoader();
	static final Class[] test_func_arg_types = { int.class, double.class };

	// Class variables
	static private double[] m_iSqrt;

	// Instance variables
	private double[] m_biases;

	// Entry point
	//	If the benchmark class is executed as a stand-alone application,
	//	its job is to run the test on all the test functions.
	static public void main (String args[]) {
		benchmark theBenchmark = new benchmark();
		theBenchmark.runTest(0);
	}

	// Constructors
	//	Load the data common to all test functions.
	//	Get ready for creating test function instances.
	benchmark () {
		this(DEFAULT_FILE_BIAS);
	}
	benchmark (String file_bias) {
		m_biases = new double[NUM_TEST_FUNC];
		m_iSqrt = new double[MAX_SUPPORT_DIM];

		loadRowVectorFromFile(file_bias, NUM_TEST_FUNC, m_biases);

		for (int i = 0 ; i < MAX_SUPPORT_DIM ; i ++) {
			m_iSqrt[i] = Math.sqrt(((double )i) + 1.0);
		}
	}

	//
	// Use this function to manufacture new test function objects
	//
	public test_func testFunctionFactory(int func_num, int dimension) {
		test_func returnFunc = null;
		try {
			returnFunc = (test_func )
				loader.loadClass(test_func_class_names[func_num-1])
					.getConstructor(test_func_arg_types)
					.newInstance(
						new Object[] {
							new Integer(dimension),
							new Double(m_biases[func_num-1])
						}
					);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return (returnFunc);
	}

	// Run tests on the test functions
	//	< 0:	Error!
	//	= 0:	On all the functions
	//	> 0:	On the specified test function
	public void runTest() {
		runTest(0);
	}
	public void runTest(int func_num) {
		if (func_num == 0) {
			for (int i = 1 ; i <= NUM_TEST_FUNC ; i ++)
				runTest(i);
		}
		else if ((func_num < 0) || (func_num > NUM_TEST_FUNC)) {
			System.err.println("The specified func_num is out of range.");
			System.exit(-1);
		}
		else {
			// Run the test function against the check points
			int num_test_points = 10;
			int test_dimension = 50;

			double[] test_f = new double[num_test_points];
			double[][] test_x = new double[num_test_points][test_dimension];

			String file_test = "testData/test_data_func" + func_num + ".txt";

			// Create the test function object
			test_func aFunc = testFunctionFactory(func_num, test_dimension);

			System.out.println("Run tests on function " + func_num +
				" (" + aFunc.name() + "):");
			System.out.println("  " +
				num_test_points + " " +
				aFunc.dimension() + "-dimension check points");

			loadTestDataFromFile(file_test, num_test_points, test_dimension, test_x, test_f);

			for (int i = 0 ; i < num_test_points ; i ++) {
				// Execute the test function
				// Collect and compare the results
				double result = aFunc.f(test_x[i]);
				double diff = result - test_f[i];
				double ratio = Math.abs(diff / test_f[i]);
				System.out.println("    " +
					numberFormatter.format(result) +
						" - " +
					numberFormatter.format(test_f[i]) +
						" = " +
					numberFormatter.format(diff));
				System.out.print("    " + "    " +
					"Difference ratio = " + numberFormatter.format(ratio));
				if (ratio != 0.0) {
					if (ratio <= 1e-12) {
						System.out.println(" (<= 1E-12)");
					}
					else {
						System.out.println(" (> 1E-12) *****");
					}
				}
				else {
					System.out.println();
				}
			}
		}
	}

	//
	// Basic functions
	//

	// Sphere function
	static public double sphere(double[] x) {

		double sum = 0.0;

		for (int i = 0 ; i < x.length ; i ++) {
			sum += x[i] * x[i];
		}

		return (sum);
	}

	// Sphere function with noise
	static public double sphere_noise(double[] x) {

		double sum = 0.0;

		for (int i = 0 ; i < x.length ; i ++) {
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

		for (int i = 1 ; i < x.length ; i ++) {
			prev_sum = curr_sum;
			curr_sum = prev_sum + x[i];
			outer_sum += (curr_sum * curr_sum);
		}

		return (outer_sum);
	}

	// Rosenbrock's function
	static public double rosenbrock(double[] x) {

		double sum = 0.0;

		for (int i = 0 ; i < (x.length-1) ; i ++) {
			double temp1 = (x[i] * x[i]) - x[i+1];
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

		for (int i = 0 ; i < x.length ; i ++) {
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

		for (int i = 0 ; i < x.length ; i ++) {
			sum1 += (x[i] * x[i]);
			sum2 += (Math.cos(PIx2 * x[i]));
		}

		return (-20.0 * Math.exp(-0.2 * Math.sqrt(sum1 / ((double )x.length))) - Math.exp(sum2 / ((double )x.length)) + 20.0 + Math.E);
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

		for (int i = 0 ; i < x.length ; i ++) {
			sum += (x[i] * x[i]) - (10.0 * Math.cos(PIx2 * x[i])) + 10.0;
		}

		return (sum);
	}

	// Non-Continuous Rastrigin's function
	static public double rastriginNonCont(double[] x) {

		double sum = 0.0;
		double currX;

		for (int i = 0 ; i < x.length ; i ++) {
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
		for (int i = 0 ; i < x.length ; i ++) {
			for (int k = 0 ; k <= Kmax ; k ++) {
				sum1 += Math.pow(a, k) * Math.cos(PIx2 * Math.pow(b, k) * (x[i] + 0.5));
			}
		}

		double sum2 = 0.0;
		for (int k = 0 ; k <= Kmax ; k ++) {
			sum2 += Math.pow(a, k) * Math.cos(PIx2 * Math.pow(b, k) * (0.5));
		}

		return (sum1 - sum2*((double )(x.length)));
	}

	// F8F2
	static public double F8F2(double[] x) {

		double sum = 0.0;

		for (int i = 1 ; i < x.length ; i ++) {
			sum += F8(F2(x[i-1], x[i]));
		}
		sum += F8(F2(x[x.length-1], x[0]));

		return (sum);
	}

	// Scaffer's F6 function
	static public double ScafferF6(double x, double y) {
		double temp1 = x*x + y*y;
		double temp2 = Math.sin(Math.sqrt(temp1));
		double temp3 = 1.0 + 0.001 * temp1;
		return (0.5 + ((temp2 * temp2 - 0.5)/(temp3 * temp3)));
	}

	// Expanded Scaffer's F6 function
	static public double EScafferF6(double[] x) {

		double sum = 0.0;

		for (int i = 1 ; i < x.length ; i ++) {
			sum += ScafferF6(x[i-1], x[i]);
		}
		sum += ScafferF6(x[x.length-1], x[0]);

		return (sum);
	}

	// Non-Continuous Expanded Scaffer's F6 function
	static public double EScafferF6NonCont(double[] x) {

		double sum = 0.0;
		double prevX, currX;

		currX = myXRound(x[0]);
		for (int i = 1 ; i < x.length ; i ++) {
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

		for (int i = 0 ; i < x.length ; i ++) {
			sum += Math.pow(a, (((double )i)/((double )(x.length-1)))) * x[i] * x[i];
		}

		return (sum);
	}

	// Hybrid composition
	static public double hybrid_composition(double[] x, HCJob job) {

		int num_func = job.num_func;
		int num_dim = job.num_dim;

		// Get the raw weights
		double wMax = Double.NEGATIVE_INFINITY;
		for (int i = 0 ; i < num_func ; i ++) {
			double sumSqr = 0.0;
			shift(job.z[i], x, job.o[i]);
			for (int j = 0 ; j < num_dim ; j ++) {
				sumSqr += (job.z[i][j] * job.z[i][j]);
			}
			job.w[i] = Math.exp(-1.0 * sumSqr / (2.0 * num_dim * job.sigma[i] * job.sigma[i]));
			if (wMax < job.w[i])
				wMax = job.w[i];
		}

		// Modify the weights
		double wSum = 0.0;
		double w1mMaxPow = 1.0 - Math.pow(wMax, 10.0);
		for (int i = 0 ; i < num_func ; i ++) {
			if (job.w[i] != wMax) {
				job.w[i] *= w1mMaxPow;
			}
			wSum += job.w[i];
		}

		// Normalize the weights
		for (int i = 0 ; i < num_func ; i ++) {
			job.w[i] /= wSum;
		}

		double sumF = 0.0;
		for (int i = 0 ; i < num_func ; i ++) {
			for (int j = 0 ; j < num_dim ; j ++) {
				job.z[i][j] /= job.lambda[i];
			}
			rotate(job.zM[i], job.z[i], job.M[i]);
			sumF +=
				job.w[i] *
				(
					job.C * job.basic_func(i, job.zM[i]) / job.fmax[i] +
						job.biases[i]
				);
		}
		return (sumF);
	}

	//
	// Elementary operations
	//

	// Shift
	static public void shift(double[] results, double[] x, double[] o) {
		for (int i = 0 ; i < x.length ; i ++) {
			results[i] = x[i] - o[i];
		}
	}

	// Rotate
	static public void rotate(double[] results, double[] x, double[][] matrix) {
		xA(results, x, matrix);
	}

	//
	// Matrix & vector operations
	//

	// (1xD) row vector * (Dx1) column vector = (1) scalar
	static public double xy(double[] x, double[] y) {
		double result = 0.0;
		for (int i = 0 ; i < x.length ; i ++) {
			result += (x[i] * y[i]);
		}

		return (result);
	}

	// (1xD) row vector * (DxD) matrix = (1xD) row vector
	static public void xA(double[] result, double[] x, double[][] A) {
		for (int i = 0 ; i < result.length ; i ++) {
			result[i] = 0.0;
			for (int j = 0 ; j < result.length ; j ++) {
				result[i] += (x[j] * A[j][i]);
			}
		}
	}

	// (DxD) matrix * (Dx1) column vector = (Dx1) column vector
	static public void Ax(double[] result, double[][] A, double[] x) {
		for (int i = 0 ; i < result.length ; i ++) {
			result[i] = 0.0;
			for (int j = 0 ; j < result.length ; j ++) {
				result[i] += (A[i][j] * x[j]);
			}
		}
	}

	//
	// Utility functions for loading data from the given text file
	//
	static public void loadTestDataFromFile(String file, int num_test_points, int test_dimension, double[][] x, double[] f) {
		try {
			BufferedReader brSrc = new BufferedReader(new FileReader(file));
			loadMatrix(brSrc, num_test_points, test_dimension, x);
			loadColumnVector(brSrc, num_test_points, f);
			brSrc.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	static public void loadRowVectorFromFile(String file, int columns, double[] row) {
		try {
			BufferedReader brSrc = new BufferedReader(new FileReader(file));
			loadRowVector(brSrc, columns, row);
			brSrc.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	static public void loadRowVector(BufferedReader brSrc, int columns, double[] row) throws Exception {
		String stToken;
		StringTokenizer stTokenizer = new StringTokenizer(brSrc.readLine());
		for (int i = 0 ; i < columns ; i ++) {
			stToken = stTokenizer.nextToken();
			row[i] = Double.parseDouble(stToken);
		}
	}

	static public void loadColumnVectorFromFile(String file, int rows, double[] column) {
		try {
			BufferedReader brSrc = new BufferedReader(new FileReader(file));
			loadColumnVector(brSrc, rows, column);
			brSrc.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	static public void loadColumnVector(BufferedReader brSrc, int rows, double[] column) throws Exception {
		String stToken;
		for (int i = 0 ; i < rows ; i ++) {
			StringTokenizer stTokenizer = new StringTokenizer(brSrc.readLine());
			stToken = stTokenizer.nextToken();
			column[i] = Double.parseDouble(stToken);
		}
	}

	static public void loadNMatrixFromFile(String file, int N, int rows, int columns, double[][][] matrix) {
		try {
			BufferedReader brSrc = new BufferedReader(new FileReader(file));
			for (int i = 0 ; i < N ; i ++) {
				loadMatrix(brSrc, rows, columns, matrix[i]);
			}
			brSrc.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	static public void loadMatrixFromFile(String file, int rows, int columns, double[][] matrix) {
		try {
			BufferedReader brSrc = new BufferedReader(new FileReader(file));
			loadMatrix(brSrc, rows, columns, matrix);
			brSrc.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	static public void loadMatrix(BufferedReader brSrc, int rows, int columns, double[][] matrix) throws Exception {
		for (int i = 0 ; i < rows ; i ++) {
			loadRowVector(brSrc, columns, matrix[i]);
		}
	}
}
