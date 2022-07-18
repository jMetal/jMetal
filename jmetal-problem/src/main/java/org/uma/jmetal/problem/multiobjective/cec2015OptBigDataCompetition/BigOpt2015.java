package org.uma.jmetal.problem.multiobjective.cec2015OptBigDataCompetition;

import static java.lang.Double.parseDouble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/** Created by ajnebro on 14/1/15. */
@SuppressWarnings("serial")
public class BigOpt2015 extends AbstractDoubleProblem {
  private List<List<Double>> mixed;
  private List<List<Double>> matrixA;
  private List<List<Double>> icaComponent;

  double f1max = -1000000;
  double f2max = -1000000;
  double f1min = 10000000;
  double f2min = 10000000;
  int dTypeG;
  boolean scaling;

  /** Constructor */
  public BigOpt2015(String instanceName) {
    loadData(instanceName);

    scaling = false;

    var numberOfVariables = dTypeG * 256;
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("BigOpt2015");

    @NotNull List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    @NotNull List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    for (var i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(-8.0);
      upperLimit.add(8.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    List<Double> s1Temp;

    List<List<Double>> s1 = new ArrayList<>();

    for (var i = 0; i < dTypeG; i++) {
      s1Temp = new ArrayList<>();
      for (var j = 0; j < icaComponent.get(0).size(); j++) {
        s1Temp.add(solution.variables().get(i * (icaComponent.get(0).size()) + j));
      }
      s1.add(s1Temp);
    }

    var x1 = multiplyWithOutAMP(matrixA, s1);
    var cor1 = correlation(x1, mixed);

    var sum = 0.0;
    for (var i = 0; i < icaComponent.size(); i++) {
      for (var j = 0; j < icaComponent.get(i).size(); j++) {
        sum += Math.pow(icaComponent.get(i).get(j) - s1.get(i).get(j), 2);
      }
    }

    var obj1 = diagonal1(cor1) + diagonal2(cor1);
    var obj2 = sum / (icaComponent.size() * icaComponent.get(0).size());

    if (obj1 > f1max) {
      f1max = obj1;
    }
    if (obj1 < f1min) {
      f1min = obj1;
    }
    if (obj2 > f1max) {
      f1max = obj2;
    }
    if (obj2 < f1min) {
      f1min = obj2;
    }

    if (scaling) {
      obj2 = (obj2 - f2min) * (f1max - f1min) / (f2max - f2min) + f1min;
    }

    solution.objectives()[0] = obj1;
    solution.objectives()[1] = obj2;

    return solution;
  }

  private void loadData(String problemId, @NotNull String fName, int dType, int dLength) {
    @NotNull String fileName = "/cec2015Comp/" + problemId + fName;

    var inputStream = createInputStream(fileName);

    var isr = new InputStreamReader(inputStream);
    @NotNull BufferedReader br = new BufferedReader(isr);

    List<List<Double>> list = new ArrayList<>();
    String aux;
    try {
      aux = br.readLine();

      while (aux != null) {
        @NotNull StringTokenizer tokenizer = new StringTokenizer(aux);
        List<Double> doubleList = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
          var value = parseDouble(tokenizer.nextToken());
          doubleList.add(value);
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
      mixed = list;
    } else if (fName.equals("S.txt")) {
      icaComponent = list;
    } else if (fName.equals("A.txt")) {
      matrixA = list;
    } else {
      throw new JMetalException("Wrong name: " + fName);
    }
  }

  private void loadData(String problemId) {
    var dType = 4;

    if (problemId.equals("D4")) {
      dType = 4;
    } else if (problemId.equals("D4N")) {
      dType = 4;
    } else if (problemId.equals("D12")) {
      dType = 12;
    } else if (problemId.equals("D12N")) {
      dType = 12;
    } else if (problemId.equals("D19")) {
      dType = 19;
    } else if (problemId.equals("D19N")) {
      dType = 19;
    }
    dTypeG = dType;

    loadData(problemId, "X.txt", dType, 256);
    loadData(problemId, "S.txt", dType, 256);
    loadData(problemId, "A.txt", dType, dType);
  }

  private InputStream createInputStream(@NotNull String fileName) {
    @Nullable InputStream inputStream = getClass().getResourceAsStream(fileName);

    return inputStream;
  }

  List<Double> newMeanStandardDeviation(List<Double> list) {
    @NotNull List<Double> result = new ArrayList<>();

    var sum = 0.0;
    for (var aDouble : list) {
      double value1 = aDouble;
      sum += value1;
    }

    var mean = sum / list.size();

    var accum = 0.0;
    for (var value : list) {
      double v = value;
      var v1 = (v - mean) * (v - mean);
      accum += v1;
    }

    var stdev = Math.sqrt(accum / (list.size() - 1));
    result.add(mean);
    result.add(stdev);

    return result;
  }

  double vectorCorrelation(@NotNull List<Double> list1, List<Double> list2) {
    var a1 = newMeanStandardDeviation(list1);
    var b1 = newMeanStandardDeviation(list2);

    double c1 = 0;
    double temp1, temp2;

    var a = a1.get(1) * b1.get(1);
    if (Math.abs(a) > 0.00001) {
      for (var i = 0; i < list1.size(); i++) {
        temp1 = ((list1.get(i) - list1.get(0)));
        temp2 = ((list2.get(i) - list2.get(0)));

        c1 += temp1 * temp2;
      }
      c1 /= (list1.size() * a);
      return c1;
    } else return 0;
  }

  List<List<Double>> correlation(@NotNull List<List<Double>> list1, List<List<Double>> list2) {
    List<Double> temp;

    List<List<Double>> m = new ArrayList<>();

    for (@NotNull List<Double> i : list1) {
      temp = new ArrayList<>();
      for (var j : list2) {
        temp.add(vectorCorrelation(i, j));
      }

      m.add(temp);
    }

    return m;
  }

  double diagonal1(@NotNull List<List<Double>> list) {
    double sum = 0;

    for (var i = 0; i < list.size(); i++) {
      for (var j = 0; j < list.size(); j++) {
        if (i == j) {
          sum += Math.pow(1 - list.get(i).get(j), 2);
        }
      }
    }
    return sum / list.size();
  }

  double diagonal2(List<List<Double>> list) {
    double sum = 0;

    for (var i = 0; i < list.size(); i++) {
      for (var j = 0; j < list.size(); j++) {
        if (i == j) {

        } else {
          sum += Math.pow(list.get(i).get(j), 2);
        }
      }
    }
    return sum / list.size() / (list.size() - 1);
  }

  List<List<Double>> multiplyWithOutAMP(List<List<Double>> list1, @NotNull List<List<Double>> list2) {
    List<Double> cTemp;

    List<List<Double>> c = new ArrayList<>();
    for (var row = 0; row < list1.size(); row++) {
      cTemp = new ArrayList<>();
      for (var col = 0; col < list2.get(0).size(); col++) {
        cTemp.add(0.0);
      }
      c.add(cTemp);
    }

    for (var row = 0; row < list1.size(); row++) {
      for (var col = 0; col < list2.get(row).size(); col++) {
        for (var inner = 0; inner < list1.get(0).size(); inner++) {
          double val = c.get(row).get(col);
          c.get(row).set(col, val + list1.get(row).get(inner) * list2.get(inner).get(col));
        }
      }
    }
    return c;
  }
}
