package org.uma.jmetal.lab.visualization.plot.impl;

import java.lang.reflect.InvocationTargetException;
import org.uma.jmetal.lab.visualization.plot.PlotFront;
import org.uma.jmetal.util.errorchecking.Check;

public class ScatterPlot implements PlotFront {
  private double[][] matrix;
  private String plotTitle;

  public ScatterPlot(double[][] matrix) {
    this(matrix, "Front") ;
  }

  public ScatterPlot(double[][] matrix, String plotTitle) {
    Check.notNull(matrix);
    Check.that(matrix.length >= 1, "The data matrix is empty");

    this.matrix = matrix;
    this.plotTitle = plotTitle ;
  }

  @Override
  public void plot() {
    var canvas = smile.plot.swing.ScatterPlot.of(matrix).canvas() ;
    canvas.setTitle(plotTitle) ;
    try {
      canvas.window() ;
    } catch (InterruptedException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
