
Special Session on Real-Parameter Optimization at CEC-05
Edinburgh, UK, 2-5 Sept. 2005

Organizers:
	Prof. Kalyanmoy Deb
		deb@iitk.ac.in
		http:www.iitk.ac.in/kangal/deb.htm
	A/Prof. P. N. Suganthan
		epnsugan@ntu.edu.sg
		http:www.ntu.edu.sg/home/EPNSugan

Java version of the test functions

Matlab reference code
	http:www.ntu.edu.sg/home/EPNSugan

Java version developer:
	Assistant Prof. Ying-ping Chen
		Department of Computer Science
		National Chiao Tung University
		HsinChu City, Taiwan
		ypchen@csie.nctu.edu.tw
		http:www.csie.nctu.edu.tw/~ypchen/

Version 0.90
		Currently, this version cannot handle any numbers of dimensions.
		It cannot generate the shifted global optima and rotation matrices
		that are not provided with the Matlab reference code.
		It can handle all cases whose data files are provided with
		the Matlab reference code.
Version 0.91
		Revised according to the Matlab reference code and the PDF document
		dated March 8, 2005.

1. FILES:
	README.txt
		This file
	benchmark.java
		The main class for the whole benchmark
	test_func.java
		The parent class for the actual test functions
	HCJob.java
		The storage class for hybrid composition functions
	F??_*.java
		The test function class for each test function
	00-tests.txt
		The computational results obtained by the Java version on the check points

2. DIRECTORIES:
	supportData/*.txt
		The files are provided with the Matlab reference code
	testData/*.txt
		The files are split from "test_data.mat" and save as ASCII files

3. Typical Usage
	// Create a benchmark object
	benchmark theBenchmark = new benchmark();
	// Use the factory function call to create a test function object
	//		test function 3 with 50 dimension
	//		the object class is "test_func"
	test_func aTestFunc = theBenchmark.testFunctionFactory(3, 50);
	// Invoke the function with x
	double result = aTestFunc.f(x);

4. To-do
	(1) Verify the correctness of the code translation.
	(2) Support shifted global optima and rotation matrices generation.
