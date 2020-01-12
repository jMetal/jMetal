package org.uma.jmetal.lab.plot.impl;

import org.uma.jmetal.lab.plot.PlotFront;
import org.uma.jmetal.util.checking.Check;
import smile.plot.PlotCanvas;
import smile.plot.ScatterPlot;

import javax.swing.*;
import java.awt.*;


public class Plot2DSmile implements PlotFront {
  private double[][] matrix;

  public Plot2DSmile(double[][] matrix) {
    Check.isNotNull(matrix);
    Check.that(matrix.length >= 1, "The data matrix is empty");
    //Check.that(matrix[0].length == 2, "The data matrix does not have two columns");

    this.matrix = matrix;
  }

  class LocalPanel extends JPanel {
    public LocalPanel(){
      super(new GridLayout(1, 1)) ;

      double[][] data = matrix ;

      PlotCanvas canvas = ScatterPlot.plot(data);
      canvas.setTitle("Front");
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
