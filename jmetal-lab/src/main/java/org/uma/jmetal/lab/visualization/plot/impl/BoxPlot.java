package org.uma.jmetal.lab.visualization.plot.impl;

import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.BoxTrace;

public class BoxPlot {
  public static void main(String[] args) {
    Object[] x = {
      "sheep",
      "cows",
      "fish",
      "tree sloths",
      "sheep",
      "cows",
      "fish",
      "tree sloths",
      "sheep",
      "cows",
      "fish",
      "tree sloths"
    };
    double[] y = {1, 4, 9, 16, 3, 6, 8, 8, 2, 4, 7, 11};

    BoxTrace trace = BoxTrace.builder(x, y).build();
    Plot.show(new Figure(trace));
  }
}
