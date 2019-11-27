package org.uma.jmetal.lab.plot.impl;

import org.uma.jmetal.lab.plot.PlotFront;
import org.uma.jmetal.util.checking.Check;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.ScatterPlot;

public class Plot2D implements PlotFront {
  private double[][] matrix;

  public Plot2D(double[][] matrix) {
    Check.isNotNull(matrix);
    Check.that(matrix.length >= 1, "The data matrix is empty");
    Check.that(matrix[0].length == 2, "The data matrix does not have two columns");

    this.matrix = matrix;
  }

  @Override
  public void plot() {
    int numberOfRows = matrix.length;
    double[] f1 = new double[numberOfRows];
    double[] f2 = new double[numberOfRows];

    for (int i = 0; i < numberOfRows; i++) {
      f1[i] = matrix[i][0];
      f2[i] = matrix[i][1];
    }

    Table table =
        Table.create("table")
            .addColumns(DoubleColumn.create("f1", f1), DoubleColumn.create("f2", f2));

    Plot.show(ScatterPlot.create("Front", table, "f1", "f2"));
  }
}
