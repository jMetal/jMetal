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

public class ChartContainer {

    private XYChart chart;
    private SwingWrapper<XYChart> sw;
    private String name;
    private int delay;

    public ChartContainer(String name) {
        this(name, 0, 600, 400);
    }

    public ChartContainer(String name, int delay) {
        this(name, delay, 600, 400);
    }

    public ChartContainer(String name, int delay, int width, int height) {
        this.name = name;
        this.delay = delay;
        this.chart = new XYChartBuilder().xAxisTitle("Objective 1").yAxisTitle("Objective 2").width(width)
                .height(height).build();
        this.chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter).setMarkerSize(5);
    }

    public void InitChart(double[] xData, double[] yData) {
        XYSeries series = this.chart.addSeries(this.name, xData, yData);
        series.setMarkerColor(Color.blue);
        this.sw = new SwingWrapper<XYChart>(this.chart);
        this.sw.displayChart(this.name);
    }

    public void RefreshChart(double[] xData, double[] yData) {
        this.RefreshChart(xData, yData, this.delay);
    }

    public void RefreshChart(double[] xData, double[] yData, int delay) {
        this.chart.updateXYSeries(this.name, xData, yData, null);
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

    public void AddSeries(String seriesName, double[] xData, double[] yData) {
        this.chart.addSeries(seriesName, xData, yData);
    }

    public void UpdateSeries(String seriesName, double[] xData, double[] yData) {
        this.chart.updateXYSeries(seriesName, xData, yData, null);
    }

    public void RemoveSeries(String seriesName) {
        this.chart.removeSeries(seriesName);
    }

    public void Repaint() {
        try {
            this.sw.repaintChart();
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
        XYSeries referenceFront = this.chart.addSeries(name, xData, yData);
        referenceFront.setMarkerColor(Color.red);
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
        BitmapEncoder.saveBitmap(this.chart, fileName, format);
    }

    public String getName() {
        return this.name;
    }

    public ChartContainer setName(String name) {
        this.name = name;
        return this;
    }

    public int getDelay() {
        return this.delay;
    }

    public ChartContainer setDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public XYChart getChart() {
        return this.chart;
    }

    public ChartContainer setChart(XYChart chart) {
        this.chart = chart;
        return this;
    }
}
