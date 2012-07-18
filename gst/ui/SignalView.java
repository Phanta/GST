package gst.ui;
/**
 * SignalView.java created 31.05.2012
 */

import gst.data.ViewController;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

/**
 * The graph of a signal in a diagram. At this moment just a raw hull.
 * @author Enrico Grunitz
 * @version 0.0.3 (18.07.2012)
 */
public class SignalView extends ChartPanel {

	/** default serialization ID */						private static final long serialVersionUID = 1L;
	//** the default domain axis */						private static NumberAxis domainAxis = initDomainAxis();
	/** the data accessor */							private ViewController controller = null;
	/** starting time of x-axis in seconds*/			private double startTime;
	/** ending time of x-axis in seconds*/				private double endTime;
	/** new data required */							private boolean needNewData;

	/* * * Constructors * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	/**
	 * Constructor using a chart. X-Axis range is set to 0.0 - 30.0.
	 * @param chart the chart to use
	 * @param useBuffer boolean for double buffered chart
	 */
	public SignalView(JFreeChart chart, boolean useBuffer) {
		this(chart, useBuffer, 0.0, 30.0);
		return;
	}
	
	/**
	 * Base constructor using full version of ChartPanel constructor.
	 * @param chart chart to display
	 * @param useBuffer use double buffering?
	 * @param startTime starting value of x-axis
	 * @param endTime end value of x-axis
	 */
	private SignalView(JFreeChart chart, boolean useBuffer, double startTime, double endTime) {
		super(/*the chart*/			chart,
			  /*width, height*/		0, 0,
			  /*min width, heigth*/	0, 0,
			  /*max width, heigth*/	Integer.MAX_VALUE, Integer.MAX_VALUE,
			  /*use buffer*/		useBuffer,
			  /*enable properties*/ false,
			  /*enable copy*/		false,
			  /*enable save*/		true,
			  /*enable print*/		false,
			  /*enable zoom*/		true,
			  /*enable tooltips*/	false);
		if(endTime < startTime) {
			double temp = endTime;
			endTime = startTime;
			startTime = temp;
		}
		this.startTime = startTime;
		this.endTime = endTime;
		chart.getXYPlot().getDomainAxis().setRange(startTime, endTime);
		needNewData = true;	// there is no controller yet
		return;
	}
	
	/* * * methods * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	/**
	 * Needed for catching width changes to this component. Refreshes data of the plot if necessary. 
	 * @see java.awt.Component#setBounds(int, int, int, int)
	 */
	@Override
	public void setBounds(int x, int y, int width, int height) {
		if(controller != null && (width != this.getWidth() || needNewData == true)) {
			this.getChart().getXYPlot().setDataset(controller.getDataPoints(startTime, endTime, width));
			needNewData = false;
		}
		super.setBounds(x, y, width, height);
	}
	
	/**
	 * Sets the ViewController for this SignalView.
	 * @param ctrl the controller to set
	 */
	private void setController(ViewController ctrl) {
		this.controller = ctrl;
		if(needNewData == true) {
			this.getChart().getXYPlot().setDataset(controller.getDataPoints(startTime, endTime, this.getWidth()));
		}
		return;
	}
	
	/**
	 * Sets the upper and lower bound of the time axis.
	 * @param start starting time
	 * @param end ending time
	 */
	public void setTimeAxisBounds(double start, double end) {
		if(end < start) {
			double temp = end;
			end = start;
			start = temp;
		}
		if(start != startTime || end != endTime) {
			startTime = start;
			endTime = end;
			needNewData = true;
			if(controller != null) {
				this.getChart().getXYPlot().setDataset(controller.getDataPoints(startTime, endTime, this.getWidth()));
				needNewData = false;
			}
			this.getChart().getXYPlot().getDomainAxis().setRange(start, end);
		}
		return;
	}
	
	/**
	 * Convenience method for {@link #setYAxisLabel(int, String) setYAxisLabel(0, labelText)}
	 * @param labelText the label String
	 */
	public void setYAxisLabel(String labelText) {
		this.setYAxisLabel(0, labelText);
		return;
	}
	
	/**
	 * Sets the label text for the specified y-axis.
	 * @param index index of the y-axis
	 * @param labelText label String 
	 */
	public void setYAxisLabel(int index, String labelText) {
		this.getChart().getXYPlot().getRangeAxis(index).setLabel(labelText);
		return;
	}
	
	/* * * static methods * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	/**
	 * Creates a new {@code SignalView} object for the given {@code ViewController}.
	 * @param controller controller controlling the data for this view
	 * @return the created SignaView
	 */
	public static final SignalView createControlledView(ViewController controller) {
		if(controller == null) {
			throw new NullPointerException("controller for controlled SignalView must be non-null");
		}
		// create and configure chart
		XYItemRenderer renderer;
		NumberAxis xAxis, yAxis;
		JFreeChart chart;
		XYPlot plot;

		renderer = new SamplingXYLineRenderer();

		xAxis = new NumberAxis();
		xAxis.setAutoRangeIncludesZero(false);
		xAxis.setAutoRangeStickyZero(false);
		
		yAxis = new NumberAxis();
		yAxis.setAutoRangeIncludesZero(false);
		yAxis.setAutoRangeStickyZero(false);

		plot = new XYPlot(/*dataset*/null, xAxis, yAxis, renderer);

		chart = new JFreeChart(null, plot);
		chart.getXYPlot().getRangeAxis().setLabelInsets(new RectangleInsets(1, 1, 1, 1));
		chart.getXYPlot().getRangeAxis().setTickLabelInsets(new RectangleInsets(1, 1, 1, 1));
		chart.getXYPlot().getRangeAxis().setLabel(controller.getPhysicalUnit());
		chart.setAntiAlias(false);			// clean charts ...
		chart.setTextAntiAlias(true);		// ... but fancy fonts
		AxisSpace as = new AxisSpace();
		as.add(50, RectangleEdge.LEFT);
		chart.removeLegend();
		chart.getXYPlot().setFixedRangeAxisSpace(as);

		// create SignalView
		SignalView sv = new SignalView(chart, true, 0.0, 30.0);
		sv.setController(controller);
		return sv;
	}

}
