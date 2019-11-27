package org.uma.jmetal.lab.plot.impl;

import org.uma.jmetal.lab.plot.PlotFront;
import org.uma.jmetal.util.checking.Check;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.Scatter3DPlot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.Scatter3DTrace;

public class Plot3D implements PlotFront {
  private double[][] matrix;

  public Plot3D(double[][] matrix) {
    Check.isNotNull(matrix);
    Check.that(matrix.length >= 1, "The data matrix is empty");
    Check.that(matrix[0].length == 3, "The data matrix does not have three columns");

    this.matrix = matrix;
  }

  @Override
  public void plot() {

    DoubleColumn xData = DoubleColumn.create("x", new double[]{2, 2, 1});
    DoubleColumn yData = DoubleColumn.create("y", new double[]{1, 2, 3});
    DoubleColumn zData = DoubleColumn.create("z", new double[]{1, 4, 1});

    Table data = Table.create().addColumns(xData, yData, zData) ;

    Plot.show(Scatter3DPlot.create("3D", data, "x", "y", "z"));

    Scatter3DTrace trace = Scatter3DTrace.builder(xData, yData, zData).build() ;

    Plot.show(new Figure(trace));



    int numberOfRows = matrix.length;
    double[] f1 = new double[numberOfRows];
    double[] f2 = new double[numberOfRows];
    double[] f3 = new double[numberOfRows];

    for (int i = 0; i < numberOfRows; i++) {
      f1[i] = matrix[i][0];
      f2[i] = matrix[i][1];
      f3[i] = matrix[i][2];
    }

    Table table =
        Table.create("table")
            .addColumns(DoubleColumn.create("f1", f1), DoubleColumn.create("f2", f2), DoubleColumn.create("f3", f3));

    Plot.show(Scatter3DPlot.create("Front", table, "f1", "f2", "f3"));
  }
}
