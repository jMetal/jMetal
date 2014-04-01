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
public abstract class HCJob {

	// Number of basic functions
	public int num_func;
	// Number of dimensions
	public int num_dim;

	// Predefined constant
	public double C;
	// Coverage range for each basic function
	public double[] sigma;
	// Biases for each basic function
	public double[] biases;
	// Stretch / compress each basic function
	public double[] lambda;
	// Estimated fmax
	public double[] fmax;
	// Shift global optimum for each basic function
	public double[][] o;
	// Linear transformation matrix for each basic function
	public double[][][] M;

	// Working areas to avoid memory allocation operations
	public double[] w;
	public double[][] z;
	public double[][] zM;

	public HCJob() {
		// Nothing
		// This class is just a place holder.
	}

	public abstract double basic_func(int func_no, double[] x);
}
