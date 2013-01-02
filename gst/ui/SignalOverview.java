/**
 * SignalOverview.java created 24.05.2012
 */

package gst.ui;

import gst.Settings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


/**
 * This class represents the Signal-Overview-Panel of the GUI. Currently not used.
 * @version 0.2.2 (30.05.2012)
 * @author Enrico Grunitz
 */
public class SignalOverview extends JPanel {

	private static final long serialVersionUID = 1L;
	/** Singleton instance of this class*/			private static final SignalOverview myself = new SignalOverview();
	/** instance of the only MouseAdapter */		private final SOMouseAdapter mouseAdapter;
	
	/** width of panel*/							private int width;
	/** height of panel*/							private int height;
	
	/** timeseries for data */						private TimeSeries ts;
	/** collection for data */						private TimeSeriesCollection dataset;
	/** the chart */								private JFreeChart chart;
	/** panel for the chart */						private ChartPanel panel;
	
	/** XYPlot */									private XYPlot plot;
	/** x-axis */									private ValueAxis xAxis;
	/** y-axis */									private ValueAxis yAxis;

	public static SignalOverview getInstance() {
		return myself;
	}
	
	/**
	 * Standard constructor. 
	 */
	private SignalOverview() {
		width = Settings.getInstance().ui.getSignalOverviewWidth();
		height = Settings.getInstance().ui.getSignalOverviewHeight();
		
		ts = new TimeSeries("Timeseries Name");
		dataset = new TimeSeriesCollection(ts);
		chart = ChartFactory.createTimeSeriesChart(null,	// title
												   null,	// timeaxis label
												   null,	// valueaxis label
												   dataset,	// data collection
												   false,	// legend on/off
												   false,	// tooltip on/off
												   false	// urls on/off
												  );
		chart.setBackgroundPaint(Color.getColor("control"));
		
		panel = new ChartPanel(chart,	// chart
							   width,	// width
							   height,	// height
							   0,		// minimum width
							   0,		// minimum height
							   1000,	// maximum width
							   1000,	// maximum height
							   true,	// use buffer
							   true,	// enable properties on/off
							   false,	// copy on/off
							   false,	// save on/off
							   false,	// print on/off
							   false,	// zoom on/off
							   false	// tooltips on/off
							  );
	    mouseAdapter = new SOMouseAdapter();
	    panel.addMouseListener(mouseAdapter);
	    panel.addMouseMotionListener(mouseAdapter);
	      // TODO looks ugly due to lack of double buffering
	    panel.setHorizontalAxisTrace(true);
	    
		plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.black);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setDomainCrosshairPaint(Color.orange);
		plot.setDomainCrosshairVisible(true);
		plot.setDomainCrosshairLockedOnData(false);	// for smoother crosshair placement
		
		xAxis = plot.getDomainAxis();
		xAxis.setVisible(false);
		yAxis = plot.getRangeAxis();
		yAxis.setVisible(false);
		
		// data "creation"
		int numPoints = width;
		ts.add(new FixedMillisecond(0), numPoints);
		for(int i = 1; i < numPoints; i++) {
			ts.add(new FixedMillisecond(i), i);
		}
		ts.add(new FixedMillisecond(numPoints), 0);

		this.add(panel);
		this.setBackground(Color.getColor("control"));
	    this.setPreferredSize(new Dimension(width, height));

	    return;
	}
	
	/**
	 * Private implementation of the Mouseadapter for SignalOverview Panel.
	 * @author Enrico Grunitz
	 * @version 0.1 (29.05.2012)
	 */
	private class SOMouseAdapter extends MouseInputAdapter{
		/** mouse inside data area flag */		private boolean mouseInside;
		/** location and size of data area */	private Rectangle2D dataArea; 
		
		public SOMouseAdapter() {
			super();
			mouseInside = false;
			dataArea = panel.getScreenDataArea();
			return;
		}
		
		public void mouseEntered(MouseEvent event) {
			// updating dataArea every time mouse enters this panel
			dataArea = panel.getScreenDataArea();
			// DEBUG searching missing mouse events
				//System.out.println("DEBUG->\tMouse entered SignalOverview");
			return;
		}
		
		public void mouseExited(MouseEvent event) {
			return;
		}
		
		public void mouseClicked(MouseEvent event) {
			if(mouseInside == true) {
				Point p = event.getPoint();
				double dataPosition = ( p.getX() - dataArea.getX() ) / dataArea.getWidth() * ( plot.getDomainAxis().getUpperBound() - plot.getDomainAxis().getLowerBound() ) + plot.getDomainAxis().getLowerBound();
				plot.setDomainCrosshairValue(dataPosition, true);
			}
			return;
		}
		
		public void mouseMoved(MouseEvent event) {
			Point p = event.getPoint();
			if(dataArea.contains(p)) {
				mouseInside = true;
			} else {
				mouseInside = false;
			}
			return;
		}
	}
}
