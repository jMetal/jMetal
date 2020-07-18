package org.uma.jmetal.lab.visualization.plot.impl;

import org.uma.jmetal.lab.visualization.plot.PlotFront;
import org.uma.jmetal.util.checking.Check;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.Scatter3DPlot;

public class Plot3D implements PlotFront {
  private double[][] matrix;
  private String plotTitle;

  public Plot3D(double[][] matrix, String title) {
    Check.isNotNull(matrix);
    Check.that(matrix.length >= 1, "The data matrix is empty");
    Check.that(matrix[0].length == 3, "The data matrix does not have three columns");

    this.plotTitle = title ;
    this.matrix = matrix;
  }

  public Plot3D(double[][] matrix) {
    this(matrix, "Front") ;
  }

  @Override
  public void plot() {
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

    table.summary() ;

    Plot.show(Scatter3DPlot.create(plotTitle, table, "f1", "f2", "f3"));
  }
}
