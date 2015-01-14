package org.uma.jmetal.problem.multiobjective.cec2015OptBigDataCompetition;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by ajnebro on 14/1/15.
 */
public class BigOpt2015 {
  private List<List<Double>> mixed ;
  private List<List<Double>> matrixA ;
  private List<List<Double>> icaComponent ;

  double f1max = -1000000;
  double f2max = -1000000;
  double f1min = 10000000;
  double f2min = 10000000;
  int dTypeG;
  int scaling;


  private void loadData(String problemId, String fName, int dType, int dLength) {
    List<List<Double>> list ;
    String fileName = "cec2015Comp/"+problemId+fName ;

    InputStream inputStream = createInputStream(fileName) ;

    InputStreamReader isr = new InputStreamReader(inputStream);
    BufferedReader br = new BufferedReader(isr);
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

  public InputStream createInputStream(String fileName) {
    InputStream inputStream = getClass().getResourceAsStream(fileName);

    return inputStream ;
  }


}
