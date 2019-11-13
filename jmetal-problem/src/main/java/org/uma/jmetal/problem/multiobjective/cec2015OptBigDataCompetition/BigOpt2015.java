package org.uma.jmetal.problem.multiobjective.cec2015OptBigDataCompetition;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by ajnebro on 14/1/15.
 */
@SuppressWarnings("serial")
public class BigOpt2015 extends AbstractDoubleProblem {
  private List<List<Double>> mixed ;
  private List<List<Double>> matrixA ;
  private List<List<Double>> icaComponent ;

  double f1max = -1000000;
  double f2max = -1000000;
  double f1min = 10000000;
  double f2min = 10000000;
  int dTypeG;
  boolean scaling;

  /** Constructor */
  public BigOpt2015(String instanceName)  {
    loadData(instanceName);

    scaling = false ;

    setNumberOfVariables(dTypeG*256);
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("BigOpt2015");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-8.0);
      upperLimit.add(8.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    List<List<Double>> s1 ;
    List<Double> s1Temp ;

    s1 = new ArrayList<>() ;

    for (int i = 0 ; i < dTypeG; i++) {
      s1Temp = new ArrayList<>() ;
      for (int j = 0 ; j < icaComponent.get(0).size(); j++) {
        s1Temp.add(solution.getVariableValue(i*(icaComponent.get(0).size())+j)) ;
      }
      s1.add(s1Temp) ;
    }

    List<List<Double>> x1 = multiplyWithOutAMP(matrixA, s1) ;
    List<List<Double>> cor1 = correlation(x1, mixed) ;

    double sum = 0.0 ;
    for (int i = 0 ; i < icaComponent.size(); i++) {
      for (int j = 0 ; j < icaComponent.get(i).size(); j++) {
        sum += Math.pow(icaComponent.get(i).get(j) - s1.get(i).get(j), 2) ;
      }
    }

    double obj1 = diagonal1(cor1)+diagonal2(cor1) ;
    double obj2 = sum/(icaComponent.size() * icaComponent.get(0).size()) ;

    if (obj1 > f1max) {
      f1max = obj1 ;
    }
    if (obj1 < f1min) {
      f1min = obj1 ;
    }
    if (obj2 > f1max) {
      f1max = obj2 ;
    }
    if (obj2 < f1min) {
      f1min = obj2 ;
    }

    if (scaling) {
      obj2 = (obj2 - f2min)*(f1max - f1min)/(f2max - f2min)+f1min ;
    }

    solution.setObjective(0, obj1);
    solution.setObjective(1, obj2);
  }

  private void loadData(String problemId, String fName, int dType, int dLength) {
    List<List<Double>> list ;
    String fileName = "/cec2015Comp/"+problemId+fName ;

    InputStream inputStream = createInputStream(fileName) ;

    InputStreamReader isr = new InputStreamReader(inputStream);
    BufferedReader br = new BufferedReader(isr);

    list = new ArrayList<>() ;
    String aux ;
    try {
      aux = br.readLine();

      while (aux != null) {
        StringTokenizer tokenizer = new StringTokenizer(aux);
        List<Double> doubleList = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
          double value = new Double(tokenizer.nextToken());
          doubleList.add(value) ;
        }
        list.add(doubleList);
        aux = br.readLine();
      }
      br.close();
    } catch (IOException e) {
      throw new JMetalException("Error reading file", e);
    } catch (NumberFormatException e) {
      throw new JMetalException("Format number exception when reading file", e);
    }

    if (fName.equals("X.txt")) {
      mixed = list ;
    } else if (fName.equals("S.txt")) {
      icaComponent = list ;
    } else if (fName.equals("A.txt")) {
      matrixA = list ;
    } else {
      throw new JMetalException("Wrong name: " + fName) ;
    }
  }

  private void loadData(String problemId){
    int dType = 4 ;

    if(problemId.equals("D4")){
      dType=4;
    }
    else if(problemId.equals("D4N")){
      dType=4;
    }
    else if(problemId.equals("D12")){
      dType=12;
    }
    else if(problemId.equals("D12N")){
      dType=12;
    }
    else if(problemId.equals("D19")){
      dType=19;
    }
    else if(problemId.equals("D19N")){
      dType=19;
    }
    dTypeG = dType;

    loadData(problemId, "X.txt", dType, 256);
    loadData(problemId, "S.txt", dType, 256);
    loadData(problemId, "A.txt", dType, dType);
  }

  private InputStream createInputStream(String fileName) {
    InputStream inputStream = getClass().getResourceAsStream(fileName);

    return inputStream ;
  }

  List<Double> newMeanStandardDeviation(List<Double> list) {
    List<Double> result = new ArrayList<>();

    double sum = 0 ;
    for (double value : list) {
      sum += value ;
    }

    double mean = sum / list.size() ;

    double accum = 0 ;
    for (double value : list) {
      accum += (value - mean) * (value - mean) ;
    }

    double stdev = Math.sqrt(accum/(list.size()-1)) ;
    result.add(mean) ;
    result.add(stdev) ;

    return result ;
  }

  double vectorCorrelation(List<Double> list1, List<Double> list2) {
    List<Double> a1 = newMeanStandardDeviation(list1) ;
    List<Double> b1 = newMeanStandardDeviation(list2) ;

    double c1 = 0;
    double temp1, temp2;

    double a = a1.get(1) * b1.get(1) ;
    if (Math.abs(a) > 0.00001){
      for (int i = 0; i < list1.size(); i++){
        temp1 = ((list1.get(i) - list1.get(0)) );
        temp2 = ((list2.get(i) - list2.get(0)) );

        c1 += temp1*temp2;

      }
      c1 /= (list1.size()*a);
      return c1;
    }
    else
      return 0;
  }

  List<List<Double>> correlation(List<List<Double>> list1, List<List<Double>> list2) {
    List<List<Double>> m ;
    List<Double> temp ;

    m = new ArrayList<>() ;

    for (List<Double> i : list1) {
      temp = new ArrayList<>() ;
      for (List<Double> j : list2) {
        temp.add(vectorCorrelation(i, j)) ;
      }

      m.add(temp) ;
    }

    return m ;
  }

  double diagonal1(List<List<Double>> list){
    double sum = 0;

    for (int i = 0; i < list.size(); i++){
      for (int j = 0; j < list.size(); j++){
        if (i == j){
          sum += Math.pow(1 - list.get(i).get(j), 2);
        }
      }
    }
    return sum/list.size();
  }

  double diagonal2(List<List<Double>> list){
    double sum = 0;

    for (int i = 0; i < list.size(); i++){
      for (int j = 0; j < list.size(); j++){
        if (i == j){

        }
        else{
          sum += Math.pow(list.get(i).get(j), 2);

        }
      }
    }
    return sum/list.size()/(list.size()-1);
  }

  List<List<Double>> multiplyWithOutAMP(List<List<Double>> list1, List<List<Double>> list2) {
    List<List<Double>> c ;
    List<Double> cTemp;

    c = new ArrayList<>() ;
    for (int row = 0; row < list1.size(); row++) {
      cTemp = new ArrayList<>() ;
      for (int col = 0; col < list2.get(0).size(); col++) {
        cTemp.add(0.0);
      }
      c.add(cTemp);
    }

    for (int row = 0; row < list1.size(); row++) {
      for (int col = 0; col < list2.get(row).size(); col++) {
        for (int inner = 0; inner < list1.get(0).size(); inner++) {
          double val = c.get(row).get(col) ;
          c.get(row).set(col, val + list1.get(row).get(inner) * list2.get(inner).get(col));
        }
      }
    }
    return c;
  }
}
