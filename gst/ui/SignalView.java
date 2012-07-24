package gst.ui;
/**
 * SignalView.java created 31.05.2012
 */

import java.util.ArrayList;
import java.util.Iterator;

import gst.data.AnnotationList;
import gst.data.DataController;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

/**
 * The graph of a signal in a diagram. At this moment just a raw hull.
 * @author Enrico Grunitz
 * @version 0.0.3 (24.07.2012)
 */
public class SignalView extends ChartPanel {

	/** default serialization ID */						private static final long serialVersionUID = 1L;
	//** the default domain axis */						private static NumberAxis domainAxis = initDomainAxis();
	/** the data accessor */							private DataController _controller = null;
	/** list of {@link gst.data.DataController}s */		private ArrayList<DataController> ctrlList; 
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
			  /*enable properties*/ true,
			  /*enable copy*/		false,
			  /*enable save*/		true,
			  /*enable print*/		false,
			  /*enable zoom*/		true,
			  /*enable tooltips*/	true);
		if(endTime < startTime) {
			// swapping start and end time if needed 
			double temp = endTime;
			endTime = startTime;
			startTime = temp;
		}
		this.startTime = startTime;
		this.endTime = endTime;
		chart.getXYPlot().getDomainAxis().setRange(startTime, endTime);
		needNewData = true;	// there is no controller yet
		ctrlList = new ArrayList<DataController>();
		this.add(this.createPopupMenu(true, false, true, false, true));
		return;
	}
	
	/* * * methods * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	/**
	 * Needed for catching width changes to this component. Refreshes data of the plot if necessary. 
	 * @see java.awt.Component#setBounds(int, int, int, int)
	 */
	@Override
	public void setBounds(int x, int y, int width, int height) {
		if(this.getWidth() != width) {
			needNewData = true;
		}
		super.setBounds(x, y, width, height);
		if(needNewData == true) {
			this.updateData();
			MainWindow.getInstance().revalidate();
			MainWindow.getInstance().repaint();
		}
		return;
	}
	
	/**
	 * Sets the ViewController for this SignalView.
	 * @param ctrl the controller to set
	 */
//	@Deprecated
//	private void setController(DataController ctrl) {
//		this.controller = ctrl;
//		if(needNewData == true) {
//			XYSeriesCollection dataset = new XYSeriesCollection();
//			XYSeries series = controller.getDataPoints(startTime, endTime, this.getWidth());
//			if(series != null) {
//				dataset.addSeries(series);
//			}
//			this.getChart().getXYPlot().setDataset(dataset);
//			if(controller.isAnnotation() == true) {
//				paintTimeAxisMarkers();
//			}
//			needNewData = false;
//		}
//		return;
//	}
	
	/**
	 * Adds the given {@link gst.data.DataController} to this {@code SignalView}. If the {@code DataController} was already added to this
	 * {@code SignalView} nothing happens. 
	 * @param dataCtrl the {@code DataController} to connect to
	 */
	public void addController(DataController dataCtrl) {
		if(ctrlList.contains(dataCtrl) == false) {
			ctrlList.add(dataCtrl);
			this.needNewData = true;
			this.updateData();
			MainWindow.getInstance().revalidate();
			MainWindow.getInstance().repaint();
		}
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
			this.startTime = start;
			this.endTime = end;
			this.needNewData = true;
			this.updateData();
			MainWindow.getInstance().revalidate();
			MainWindow.getInstance().repaint();
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
	
	/**
	 * Collects all data points from controllers and adds them to the chart. Sets {@code needNewData} to false.
	 */
	private void updateData() {
		// DEBUGCODE updateData() EDT Test
		String DBG_not = "";
		if(!javax.swing.SwingUtilities.isEventDispatchThread()) {
			DBG_not = "NOT ";
		}
		System.out.println("DEBUG\tSignalView().updateData() called and running " + DBG_not + "in EDT.");
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		Iterator<DataController> it = ctrlList.iterator();
		XYSeries curSeries;	// current series
		while(it.hasNext()) {
			DataController ctrl = it.next();
			if(ctrl.isAnnotation()) {
				this.paintTimeAxisMarkers(ctrl);
			} else {
				curSeries = ctrl.getDataPoints(startTime, endTime, this.getWidth());
				if(curSeries != null) {
					dataset.addSeries(curSeries);
				}
			}
		}
		this.getChart().getXYPlot().setDataset(dataset);
		if(this.ctrlList.isEmpty() == false) {
			// only mark as updated if there are controllers
			this.needNewData = false;
		}
		return;
	}
	
	/**
	 * Sets for all annotations given by the {@code DataController} DomainMarkers.
	 * @param ctrl
	 */
	private void paintTimeAxisMarkers(DataController ctrl) {
		removeTimeAxisMarker();
		AnnotationList annoList = ctrl.getAnnotations(startTime, endTime);
		for(int i = 0; i < annoList.size(); i++) {
			ValueMarker marker = new ValueMarker(annoList.getTime(i));
			if(i == annoList.size() - 1) {
				this.getChart().getXYPlot().addDomainMarker(0, marker, org.jfree.ui.Layer.FOREGROUND, true);
			} else {
				this.getChart().getXYPlot().addDomainMarker(0, marker, org.jfree.ui.Layer.FOREGROUND, false);
			}
		}
		return;
	}
	
	/**
	 * Sets for all annotations given by the {@code DataController} DomainMarkers.
	 */
//	@Deprecated
//	private void paintTimeAxisMarkers() {
//		if(controller == null || controller.isAnnotation() == false) {
//			return;			// nothing to do here
//		}
//		removeTimeAxisMarker();
//		AnnotationList annoList = controller.getAnnotations(startTime, endTime);
//		for(int i = 0; i < annoList.size(); i++) {
//			ValueMarker marker = new ValueMarker(annoList.getTime(i));
//			if(i == annoList.size() - 1) {
//				this.getChart().getXYPlot().addDomainMarker(0, marker, org.jfree.ui.Layer.FOREGROUND, true);
//			} else {
//				this.getChart().getXYPlot().addDomainMarker(0, marker, org.jfree.ui.Layer.FOREGROUND, false);
//			}
//		}
//		return;
//	}
	
	/**
	 * Removes all {@code DomainMarkers} from this {@code SignalView}'s chart.
	 */
	private void removeTimeAxisMarker() {
		this.getChart().getXYPlot().clearDomainMarkers();
	}
	
	/* * * static methods * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	/**
	 * Creates a new {@code SignalView} object for the given {@code ViewController}.
	 * @param controller controller controlling the data for this view
	 * @return the created SignaView
	 */
	public static final SignalView createControlledView(DataController controller) {
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
		sv.addController(controller);
		return sv;
	}

}
