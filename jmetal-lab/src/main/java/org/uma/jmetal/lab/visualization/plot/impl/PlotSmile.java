package org.uma.jmetal.lab.visualization.plot.impl;

import org.uma.jmetal.lab.visualization.plot.PlotFront;
import org.uma.jmetal.util.checking.Check;
import smile.plot.PlotCanvas;
import smile.plot.ScatterPlot;

import javax.swing.*;
import java.awt.*;


public class PlotSmile implements PlotFront {
  private double[][] matrix;
  private String plotTitle;

  public PlotSmile(double[][] matrix) {
    this(matrix, "Front") ;
  }

  public PlotSmile(double[][] matrix, String plotTitle) {
    Check.isNotNull(matrix);
    Check.that(matrix.length >= 1, "The data matrix is empty");

    this.matrix = matrix;
    this.plotTitle = plotTitle ;
  }

  @SuppressWarnings("serial")
  class LocalPanel extends JPanel {
    public LocalPanel(){
      super(new GridLayout(1, 1)) ;

      double[][] data = matrix ;

      PlotCanvas canvas = ScatterPlot.plot(data);
      canvas.setTitle(plotTitle);
      add(canvas);

    }
  }

  @Override
  public void plot() {
    JFrame frame = new JFrame() ;
    frame.setSize(500, 500);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.getContentPane().add(new LocalPanel());
    frame.setVisible(true);
  }
}
