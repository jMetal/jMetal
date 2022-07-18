package org.uma.jmetal.util.artificialdecisionmaker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class DecisionTreeEstimator<S extends Solution<?>> {

  private List<S> solutionList = null;
  private static final String VALUE_STRING = "value_";
  private static final String NOMINAL_STRING = "my-nominal";
  private static final String MY_STRING = "my-string";
  
  public DecisionTreeEstimator(List<S> solutionList) {
    this.solutionList = solutionList;

  }


  public double doPrediction(int index, @NotNull S testSolution) {
    var result = 0.0d;

    try {
      var numberOfObjectives = solutionList.get(0).objectives().length;
      //Attributes
      //numeric
      @NotNull Attribute attr = new Attribute("my-numeric");

      //nominal
      var myNomVals = new ArrayList<String>();
      for (var i1 = 0; i1 < numberOfObjectives; i1++) {
        var s = VALUE_STRING + i1;
        myNomVals.add(s);
      }

      var attr1 = new Attribute(NOMINAL_STRING, myNomVals);
      //System.out.println(attr1.isNominal());

      //string
      var attr2 = new Attribute(MY_STRING, (List<String>)null);
      //System.out.println(attr2.isString());

      //2.create dataset
      @NotNull ArrayList<Attribute> attrs = new ArrayList<>();
      attrs.add(attr);
      attrs.add(attr1);
      attrs.add(attr2);
      var dataset = new Instances("my_dataset", attrs, 0);

      //Add instances
      for (@NotNull S solution : solutionList) {
        //instaces
        for (var i = 0; i <numberOfObjectives ; i++) {
          var attValues = new double[dataset.numAttributes()];
          attValues[0] = solution.objectives()[i];
          attValues[1] = dataset.attribute(NOMINAL_STRING).indexOfValue(VALUE_STRING+i);
          attValues[2] = dataset.attribute(MY_STRING).addStringValue(solution.toString()+i);
          dataset.add(new DenseInstance(1.0, attValues));
        }
      }


      //DataSet test
      @NotNull Instances datasetTest = new Instances("my_dataset_test", attrs, 0);

      //Add instances
      for (var i = 0; i < numberOfObjectives; i++) {
        Instance test = new DenseInstance(3);
        test.setValue(attr, testSolution.objectives()[i]);
        test.setValue(attr1, VALUE_STRING+i);
        test.setValue(attr2, testSolution.toString()+i);
        datasetTest.add(test);
      //  dataset.add(test);
      }


      //split to 70:30 learn and test set

      //Preprocess strings (almost no classifier supports them)
      @NotNull StringToWordVector filter = new StringToWordVector();

      filter.setInputFormat(dataset);
      dataset = Filter.useFilter(dataset, filter);

      //Buid classifier
      dataset.setClassIndex(1);
      @NotNull Classifier classifier = new J48();
      classifier.buildClassifier(dataset);
      //resample if needed
      //dataset = dataset.resample(new Random(42));
      dataset.setClassIndex(1);
      datasetTest.setClassIndex(1);
      //do eval
      var eval = new Evaluation(datasetTest); //trainset
      eval.evaluateModel(classifier, datasetTest); //testset
      result = classifier.classifyInstance(datasetTest.get(index));
    } catch (Exception e) {
      result = testSolution.objectives()[index];
    }
    return result;
  }


  public double doPredictionVariable(int index, @NotNull S testSolution) {
    var result = 0.0d;

    try {
      var numberOfVariables = solutionList.get(0).variables().size();
      //Attributes
      //numeric
      @NotNull Attribute attr = new Attribute("my-numeric");

      //nominal
      var myNomVals = new ArrayList<String>();
      for (var i1 = 0; i1 < numberOfVariables; i1++) {
        var s = VALUE_STRING + i1;
        myNomVals.add(s);
      }

      var attr1 = new Attribute(NOMINAL_STRING, myNomVals);

      //string
      var attr2 = new Attribute(MY_STRING, (List<String>)null);

      //2.create dataset
      var attrs = new ArrayList<Attribute>();
      attrs.add(attr);
      attrs.add(attr1);
      attrs.add(attr2);
      var dataset = new Instances("my_dataset", attrs, 0);

      //Add instances
      for (@NotNull S solution : solutionList) {
        //instaces
        for (var i = 0; i <numberOfVariables ; i++) {
          var attValues = new double[dataset.numAttributes()];
          attValues[0] = ((DoubleSolution)solution).variables().get(i);
          attValues[1] = dataset.attribute(NOMINAL_STRING).indexOfValue(VALUE_STRING+i);
          attValues[2] = dataset.attribute(MY_STRING).addStringValue(solution.toString()+i);
          dataset.add(new DenseInstance(1.0, attValues));
        }
      }


      //DataSet test
      @NotNull Instances datasetTest = new Instances("my_dataset_test", attrs, 0);

      //Add instances
      for (var i = 0; i < numberOfVariables; i++) {
        Instance test = new DenseInstance(3);
        test.setValue(attr, ((DoubleSolution)testSolution).variables().get(i));
        test.setValue(attr1, VALUE_STRING+i);
        test.setValue(attr2, testSolution.toString()+i);
        datasetTest.add(test);
        //  dataset.add(test);
      }


      //split to 70:30 learn and test set

      //Preprocess strings (almost no classifier supports them)
      var filter = new StringToWordVector();

      filter.setInputFormat(dataset);
      dataset = Filter.useFilter(dataset, filter);

      //Buid classifier
      dataset.setClassIndex(1);
      @NotNull Classifier classifier = new J48();
      classifier.buildClassifier(dataset);
      //resample if needed
      //dataset = dataset.resample(new Random(42));
      dataset.setClassIndex(1);
      datasetTest.setClassIndex(1);
      //do eval
      var eval = new Evaluation(datasetTest); //trainset
      eval.evaluateModel(classifier, datasetTest); //testset
      result = classifier.classifyInstance(datasetTest.get(index));
    } catch (Exception e) {
      result = ((DoubleSolution)testSolution).variables().get(index);
    }
    return result;
  }

}
