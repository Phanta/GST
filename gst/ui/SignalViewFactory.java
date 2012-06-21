/**
 * SignalViewFactory.java created on 14.06.2012
 */

package gst.ui;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

/**
 * This class provides static method to generate SignalViews from scratch or given data.
 * @author Enrico Grunitz
 * @version 0.1 (20.06.2012)
 */
public abstract class SignalViewFactory {
	/** number of the generated charts */							private static long chartNumber = 0;
	
	/**
	 * Generates a random walk chart with specified number of data points.
	 * @param numDataPoints number of data points.
	 * @return the SignalView of the generated chart
	 */
	public static final SignalView generateRandomChart(int numDataPoints) {
		XYSeries series = new XYSeries("random walk series");
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		XYItemRenderer renderer;
		NumberAxis xAxis, yAxis;
		JFreeChart chart;
		XYPlot plot;
		// data "creation"
		double lastDataPoint = 0;
		for(int i = 0; i < numDataPoints; i++) {
			lastDataPoint = lastDataPoint + Math.random() - 0.5;
			series.add((double) i, lastDataPoint);
		}
		renderer = new SamplingXYLineRenderer();
		xAxis = new NumberAxis();
		xAxis.setAutoRangeIncludesZero(false);
		xAxis.setAutoRangeStickyZero(false);
		
		yAxis = new NumberAxis();
		yAxis.setAutoRangeIncludesZero(false);
		yAxis.setAutoRangeStickyZero(false);
		plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		chart = new JFreeChart(null, plot);
		
		//JFreeChart chart = ChartFactory.createXYLineChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, false, false);
		//chart.getXYPlot().setDomainAxis(domainAxis);
		chart.getXYPlot().getRangeAxis().setLabelInsets(new RectangleInsets(1, 1, 1, 1));
		chart.getXYPlot().getRangeAxis().setTickLabelInsets(new RectangleInsets(1, 1, 1, 1));
		AxisSpace as = new AxisSpace();
		as.add(50, RectangleEdge.LEFT);
		chart.removeLegend();
		chart.getXYPlot().setFixedRangeAxisSpace(as);
		chart.setAntiAlias(false);			// clean charts ...
		chart.setTextAntiAlias(true);		// ... but fancy fonts
		//domainAxis.setRange(0.0, 2000.0);
		
		chartNumber++;
		return new SignalView(chart, false);
	}
	
	/**
	 * Convenience method for {@link #generateRandomCombinedChart(int, int, int[]) generateRandomCombinedChart(numDataPoints, numCharts, null)}.
	 * All sub-charts have the same size. 
	 * @param numDataPoints number of data point per chart
	 * @param numCharts number of charts
	 * @return the SignalView of the generated chart
	 */
	public static final SignalView generateRandomCombinedChart(int numDataPoints, int numCharts) {
		return generateRandomCombinedChart(numDataPoints, numCharts, null);
	}
	
	/**
	 * Generates a SignalView with a specified number of data points and multiple charts. Their heights are adapted by given weights.
	 * These charts share the same x-axis. 
	 * @param numDataPoints number of data points per chart
	 * @param numCharts number of charts to generate
	 * @param weights weights for the heights of the charts, if null all charts have the same height
	 * @return the SignalView of the generated chart
	 */
	public static final SignalView generateRandomCombinedChart(int numDataPoints, int numCharts, int[] weights) {
		XYSeries series;
		XYSeriesCollection dataset;
		XYItemRenderer renderer;
		NumberAxis xAxis, yAxis;
		XYPlot subplot;
		CombinedDomainXYPlot mainplot;
		double lastDataPoint;
		
		// validating parameters
		if(numCharts <= 0 || numDataPoints <= 0) {
			return null;
		}
		if(weights != null) {
			if(weights.length != numCharts) {
				weights = generateWeights(numCharts);
			}
		} else {
			weights = generateWeights(numCharts);
		}
		
		//setting up the combined plot
		xAxis = new NumberAxis();
		xAxis.setAutoRangeIncludesZero(false);
		xAxis.setAutoRangeStickyZero(false);
		mainplot = new CombinedDomainXYPlot(xAxis);
		mainplot.setDomainAxisLocation(0, AxisLocation.BOTTOM_OR_LEFT);
		mainplot.setGap(0.0);		// gap between subplots
		mainplot.setOrientation(PlotOrientation.VERTICAL);
		
		// setting up subplots
		for(int i = 0; i < numCharts; i++) {
			series = new XYSeries("Series " + i);
			dataset = new XYSeriesCollection(series);
			renderer = new SamplingXYLineRenderer();
			yAxis = new NumberAxis();
			yAxis.setAutoRangeIncludesZero(false);
			yAxis.setAutoRangeStickyZero(false);
			// generate data
			lastDataPoint = 0;
			for(int j = 0; j < numDataPoints; j++) {
				lastDataPoint = lastDataPoint + Math.random() - 0.5;
				series.add((double) j, lastDataPoint);
			}
			// packing all in a subplot
			subplot = new XYPlot(dataset, null, yAxis, renderer);
			subplot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
			// add subplot to mainplot
			mainplot.add(subplot, weights[i]);
		}
		xAxis.setRange(0.0, numDataPoints);

		// generate chart from mainplot
		JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, mainplot, false);
		chart.setAntiAlias(false);			// clean charts ...
		chart.setTextAntiAlias(true);		// ... but fancy fonts
		
		chartNumber++;
		return new SignalView(chart, true);
	}
	
	/**
	 * Generates a new weight array with specified number of elements. 
	 * @param numWeights number of elements
	 * @return the weight array
	 */
	private static int[] generateWeights(int numWeights) {
		int[] weights = new int[numWeights];
		for(int i = 0; i < numWeights; i++) {
			weights[i] = 1;
		}
		return weights;
	}

}
