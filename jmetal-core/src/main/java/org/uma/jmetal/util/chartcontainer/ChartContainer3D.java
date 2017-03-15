//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.util.chartcontainer;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;

/**
 * Class for configuring and displaying a XChart.
 *
 * @author Jorge Rodriguez Ordonez
 */

public class ChartContainer3D {

    private List<XYChart> charts;
    private SwingWrapper<XYChart> sw;
    private String name;
    private int delay;

    public ChartContainer3D(String name) {
        this(name, 0, 600, 400);
    }

    public ChartContainer3D(String name, int delay) {
        this(name, delay, 600, 400);
    }

    public ChartContainer3D(String name, int delay, int width, int height) {
        this.name = name;
        this.delay = delay;
        this.charts = new ArrayList<XYChart>();
        XYChart chart1 = new XYChartBuilder().xAxisTitle("Objective 1").yAxisTitle("Objective 2").width(width)
                .height(height).build();
        chart1.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter).setMarkerSize(5);
        this.charts.add(chart1);

        XYChart chart2 = new XYChartBuilder().xAxisTitle("Objective 1").yAxisTitle("Objective 3").width(width)
                .height(height).build();
        chart2.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter).setMarkerSize(5);
        this.charts.add(chart2);

        XYChart chart3 = new XYChartBuilder().xAxisTitle("Objective 2").yAxisTitle("Objective 3").width(width)
                .height(height).build();
        chart3.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter).setMarkerSize(5);
        this.charts.add(chart3);
    }

    public void InitChart(double[] xData, double[] yData, double[] zData) {
        XYSeries series1 = this.charts.get(0).addSeries(this.name, xData, yData);
        series1.setMarkerColor(Color.blue);

        XYSeries series2 = this.charts.get(1).addSeries(this.name, xData, zData);
        series2.setMarkerColor(Color.blue);

        XYSeries series3 = this.charts.get(2).addSeries(this.name, yData, zData);
        series3.setMarkerColor(Color.blue);

        this.sw = new SwingWrapper<XYChart>(this.charts);
        this.sw.displayChartMatrix(this.name);
    }

    public void RefreshChart(double[] xData, double[] yData, double[] zData) {
        this.RefreshChart(xData, yData, zData, this.delay);
    }

    public void RefreshChart(double[] xData, double[] yData, double[] zData, int delay) {
        this.charts.get(0).updateXYSeries(this.name, xData, yData, null);
        this.charts.get(1).updateXYSeries(this.name, xData, zData, null);
        this.charts.get(2).updateXYSeries(this.name, yData, zData, null);
        if (delay > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(delay);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        this.Repaint();
    }

    public void Repaint() {
        try {
            this.sw.repaintChart(0);
            this.sw.repaintChart(1);
            this.sw.repaintChart(2);
        } catch (IndexOutOfBoundsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void DisplayFront(String name, String fileName) throws FileNotFoundException {
        ArrayFront front = new ArrayFront(fileName);
        double[][] data = FrontUtils.convertFrontToArray(front);
        double[] xData = getObjectiveValues(data, 0);
        double[] yData = getObjectiveValues(data, 1);
        double[] zData = getObjectiveValues(data, 2);
        XYSeries referenceFront1 = this.charts.get(0).addSeries(name, xData, yData);
        referenceFront1.setMarkerColor(Color.red);
        XYSeries referenceFront2 = this.charts.get(1).addSeries(name, xData, zData);
        referenceFront2.setMarkerColor(Color.red);
        XYSeries referenceFront3 = this.charts.get(2).addSeries(name, yData, zData);
        referenceFront3.setMarkerColor(Color.red);
    }

    public void DisplayReferenceFront(String fileName) throws FileNotFoundException {
        this.DisplayFront("Reference Front", fileName);
    }

    private double[] getObjectiveValues(double[][] data, int obj) {
        double[] values = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            values[i] = data[i][obj];
        }
        return values;
    }

    public void SaveChart(String fileName, BitmapFormat format) throws IOException {
        BitmapEncoder.saveBitmap(this.charts.get(0), fileName + "_1", format);
        BitmapEncoder.saveBitmap(this.charts.get(1), fileName + "_2", format);
        BitmapEncoder.saveBitmap(this.charts.get(2), fileName + "_3", format);
    }

    public String getName() {
        return this.name;
    }

    public ChartContainer3D setName(String name) {
        this.name = name;
        return this;
    }

    public int getDelay() {
        return this.delay;
    }

    public ChartContainer3D setDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public XYChart getChart(int index) {
        return this.charts.get(index);
    }

    public ChartContainer3D setChart(XYChart chart, int index) {
        this.charts.set(index, chart);
        return this;
    }
}
