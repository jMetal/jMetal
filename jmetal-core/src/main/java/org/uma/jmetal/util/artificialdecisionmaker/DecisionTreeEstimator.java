package org.uma.jmetal.util.artificialdecisionmaker;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.ArrayList;
import java.util.List;

public class DecisionTreeEstimator<S extends Solution<?>> {

  private List<S> solutionList = null;
  private static final String VALUE_STRING = "value_";
  private static final String NOMINAL_STRING = "my-nominal";
  private static final String MY_STRING = "my-string";
  
  public DecisionTreeEstimator(List<S> solutionList) {
    this.solutionList = solutionList;

  }


  public double doPrediction(int index,S testSolution) {
    double result = 0.0d;

    try {
      int numberOfObjectives = solutionList.get(0).getNumberOfObjectives();
      //Attributes
      //numeric
      Attribute attr = new Attribute("my-numeric");

      //nominal
      ArrayList<String> myNomVals = new ArrayList<>();

      for (int i=0; i<numberOfObjectives; i++)
        myNomVals.add(VALUE_STRING+i);
      Attribute attr1 = new Attribute(NOMINAL_STRING, myNomVals);
      //System.out.println(attr1.isNominal());

      //string
      Attribute attr2 = new Attribute(MY_STRING, (List)null);
      //System.out.println(attr2.isString());

      //2.create dataset
      ArrayList<Attribute> attrs = new ArrayList<>();
      attrs.add(attr);
      attrs.add(attr1);
      attrs.add(attr2);
      Instances dataset = new Instances("my_dataset", attrs, 0);

      //Add instances
      int j=0;
      for (S solution : solutionList) {
        //instaces
        for (int i = 0; i <numberOfObjectives ; i++) {
          double[] attValues = new double[dataset.numAttributes()];
          attValues[0] = solution.getObjective(i);
          attValues[1] = dataset.attribute(NOMINAL_STRING).indexOfValue(VALUE_STRING+i);
          attValues[2] = dataset.attribute(MY_STRING).addStringValue(solution.toString()+i);
          dataset.add(new DenseInstance(1.0, attValues));
        }
        j++;
      }


      //DataSet test
      Instances datasetTest = new Instances("my_dataset_test", attrs, 0);

      //Add instances
      for (int i = 0; i < numberOfObjectives; i++) {
        Instance test = new DenseInstance(3);
        test.setValue(attr, testSolution.getObjective(i));
        test.setValue(attr1, VALUE_STRING+i);
        test.setValue(attr2, testSolution.toString()+i);
        datasetTest.add(test);
      //  dataset.add(test);
      }


      //split to 70:30 learn and test set
      double percent = 70.0;
      int trainSize = (int) Math.round(dataset.numInstances() * percent / 100);
      int testSize = dataset.numInstances() - trainSize;

      //Preprocess strings (almost no classifier supports them)
      StringToWordVector filter = new StringToWordVector();

      filter.setInputFormat(dataset);
      dataset = Filter.useFilter(dataset, filter);

      //Buid classifier
      dataset.setClassIndex(1);
      Classifier classifier = new J48();
      classifier.buildClassifier(dataset);
      //resample if needed
      //dataset = dataset.resample(new Random(42));
     // Instances train = new Instances(dataset, 0, trainSize);
     // Instances test = new Instances(dataset, trainSize, testSize);
      dataset.setClassIndex(1);
      datasetTest.setClassIndex(1);
      //do eval
      Evaluation eval = new Evaluation(datasetTest); //trainset
      eval.evaluateModel(classifier, datasetTest); //testset
      result = classifier.classifyInstance(datasetTest.get(index));
    } catch (Exception e) {
      result = testSolution.getObjective(index);
    }
    return result;
  }


  public double doPredictionVariable(int index,S testSolution) {
    double result = 0.0d;

    try {
      int numberOfVariables = solutionList.get(0).getNumberOfVariables();
      //Attributes
      //numeric
      Attribute attr = new Attribute("my-numeric");

      //nominal
      ArrayList<String> myNomVals = new ArrayList<>();

      for (int i=0; i<numberOfVariables; i++)
        myNomVals.add(VALUE_STRING+i);
      Attribute attr1 = new Attribute(NOMINAL_STRING, myNomVals);

      //string
      Attribute attr2 = new Attribute(MY_STRING, (List)null);

      //2.create dataset
      ArrayList<Attribute> attrs = new ArrayList<>();
      attrs.add(attr);
      attrs.add(attr1);
      attrs.add(attr2);
      Instances dataset = new Instances("my_dataset", attrs, 0);

      //Add instances
      int j=0;
      for (S solution : solutionList) {
        //instaces
        for (int i = 0; i <numberOfVariables ; i++) {
          double[] attValues = new double[dataset.numAttributes()];
          attValues[0] = ((DoubleSolution)solution).getVariableValue(i);
          attValues[1] = dataset.attribute(NOMINAL_STRING).indexOfValue(VALUE_STRING+i);
          attValues[2] = dataset.attribute(MY_STRING).addStringValue(solution.toString()+i);
          dataset.add(new DenseInstance(1.0, attValues));
        }
        j++;
      }


      //DataSet test
      Instances datasetTest = new Instances("my_dataset_test", attrs, 0);

      //Add instances
      for (int i = 0; i < numberOfVariables; i++) {
        Instance test = new DenseInstance(3);
        test.setValue(attr, ((DoubleSolution)testSolution).getVariableValue(i));
        test.setValue(attr1, VALUE_STRING+i);
        test.setValue(attr2, testSolution.toString()+i);
        datasetTest.add(test);
        //  dataset.add(test);
      }


      //split to 70:30 learn and test set
      double percent = 70.0;
      int trainSize = (int) Math.round(dataset.numInstances() * percent / 100);
      int testSize = dataset.numInstances() - trainSize;

      //Preprocess strings (almost no classifier supports them)
      StringToWordVector filter = new StringToWordVector();

      filter.setInputFormat(dataset);
      dataset = Filter.useFilter(dataset, filter);

      //Buid classifier
      dataset.setClassIndex(1);
      Classifier classifier = new J48();
      classifier.buildClassifier(dataset);
      //resample if needed
      //dataset = dataset.resample(new Random(42));
      // Instances train = new Instances(dataset, 0, trainSize);
      // Instances test = new Instances(dataset, trainSize, testSize);
      dataset.setClassIndex(1);
      datasetTest.setClassIndex(1);
      //do eval
      Evaluation eval = new Evaluation(datasetTest); //trainset
      eval.evaluateModel(classifier, datasetTest); //testset
      result = classifier.classifyInstance(datasetTest.get(index));
    } catch (Exception e) {
      result = ((DoubleSolution)testSolution).getVariableValue(index);
    }
    return result;
  }

}
