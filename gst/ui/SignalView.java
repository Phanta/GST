package gst.ui;
/**
 * SignalView.java created 31.05.2012
 */

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.RangeType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 * The graph of a signal in a diagram. At this moment just a raw hull.
 * @author Enrico Grunitz
 * @version 0.0.2 (13.06.2012)
 */
public class SignalView extends ChartPanel {

	/** default serialization ID */						private static final long serialVersionUID = 1L;
	/** the default domain axis */						private static NumberAxis domainAxis = initDomainAxis();

	/* * * Constructors * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	/**
	 * @param chart
	 */
	public SignalView(JFreeChart chart) {
		super(chart);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param chart
	 * @param useBuffer
	 */
	public SignalView(JFreeChart chart, boolean useBuffer) {
		super(chart, true, false, false, true, true);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param chart
	 * @param properties
	 * @param save
	 * @param print
	 * @param zoom
	 * @param tooltips
	 */
	public SignalView(JFreeChart chart, boolean properties, boolean save,
			boolean print, boolean zoom, boolean tooltips) {
		super(chart, properties, save, print, zoom, tooltips);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param chart
	 * @param width
	 * @param height
	 * @param minimumDrawWidth
	 * @param minimumDrawHeight
	 * @param maximumDrawWidth
	 * @param maximumDrawHeight
	 * @param useBuffer
	 * @param properties
	 * @param save
	 * @param print
	 * @param zoom
	 * @param tooltips
	 */
	public SignalView(JFreeChart chart, int width, int height,
			int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth,
			int maximumDrawHeight, boolean useBuffer, boolean properties,
			boolean save, boolean print, boolean zoom, boolean tooltips) {
		super(chart, width, height, minimumDrawWidth, minimumDrawHeight,
				maximumDrawWidth, maximumDrawHeight, useBuffer, properties,
				save, print, zoom, tooltips);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param chart
	 * @param width
	 * @param height
	 * @param minimumDrawWidth
	 * @param minimumDrawHeight
	 * @param maximumDrawWidth
	 * @param maximumDrawHeight
	 * @param useBuffer
	 * @param properties
	 * @param copy
	 * @param save
	 * @param print
	 * @param zoom
	 * @param tooltips
	 */
	public SignalView(JFreeChart chart, int width, int height,
			int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth,
			int maximumDrawHeight, boolean useBuffer, boolean properties,
			boolean copy, boolean save, boolean print, boolean zoom,
			boolean tooltips) {
		super(chart, width, height, minimumDrawWidth, minimumDrawHeight,
				maximumDrawWidth, maximumDrawHeight, useBuffer, properties,
				copy, save, print, zoom, tooltips);
		// TODO Auto-generated constructor stub
	}

	/* * * static methods * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	public static SignalView generateRandomChart(int numDataPoints) {
		XYSeries series = new XYSeries("new random series");
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		// data "creation"
		double lastDataPoint = 0;
		for(int i = 0; i < numDataPoints; i++) {
			lastDataPoint = lastDataPoint + Math.random() - 0.5;
			series.add((double) i, lastDataPoint);
		}
		JFreeChart chart = ChartFactory.createXYLineChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, false, false);
		chart.getXYPlot().setDomainAxis(domainAxis);
		chart.getXYPlot().getRangeAxis().setLabelInsets(new RectangleInsets(0, 0, 0, 0));
		chart.getXYPlot().getRangeAxis().setTickLabelInsets(new RectangleInsets(1, 1, 1, 1));
		System.out.println(chart.getXYPlot().getRangeAxis().getTickLabelInsets());
		//domainAxis.setRange(0.0, 2000.0);
		return new SignalView(chart, false);
	}
	
	public static SignalView generateRandomCombinedChart(int numDataPoints, int numCharts, int[] weights) {
		XYSeries series;
		XYSeriesCollection dataset;
		XYItemRenderer renderer;
		NumberAxis xAxis, yAxis;
		XYPlot subplot;
		CombinedDomainXYPlot mainplot;
		double lastDataPoint;
		
		//setting up the combined plot
		xAxis = new NumberAxis();
		xAxis.setAutoRangeIncludesZero(false);
		xAxis.setAutoRangeStickyZero(false);
		mainplot = new CombinedDomainXYPlot(xAxis);
		mainplot.setGap(10.0);
		mainplot.setOrientation(PlotOrientation.VERTICAL);
		
		// setting up subplots
		for(int i = 0; i < numCharts; i++) {
			series = new XYSeries("Series " + i);
			dataset = new XYSeriesCollection(series);
			renderer = new StandardXYItemRenderer();
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
		return new SignalView(chart, true);
	}
	
	private static NumberAxis initDomainAxis() {
		NumberAxis axis = new NumberAxis();
		axis.setAutoRangeIncludesZero(false);
		axis.setAutoRangeStickyZero(false);
		axis.setAutoRange(true);
		axis.setRangeType(RangeType.FULL);
		return axis;
	}
}
