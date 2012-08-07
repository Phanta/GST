package gst.ui;
/**
 * SignalView.java created 31.05.2012
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JColorChooser;

import gst.Main;
import gst.Settings;
import gst.data.AnnotationList;
import gst.data.DataController;
import gst.test.Debug;
import gst.ui.dialog.EditEventDialog;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;

import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

/**
 * The graph of a signal in a diagram. At this moment just a raw hull.
 * @author Enrico Grunitz
 * @version 0.1.1 (07.08.2012)
 */
public class SignalView extends ChartPanel {

	/** default serialization ID */						private static final long serialVersionUID = 1L;
	//** the default domain axis */						private static NumberAxis domainAxis = initDomainAxis();
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
			  /*enable zoom*/		false,		// function implemented, no need for built-in version
			  /*enable tooltips*/	false);
		// NOTE tooltips cause an unhandled class cast exception for MainWindow cast to JComponent in context of mouse event forwarding
		//																													in MainWindow
		if(endTime < startTime) {
			// swapping start and end time if needed 
			double temp = endTime;
			endTime = startTime;
			startTime = temp;
		}
		this.startTime = startTime;
		this.endTime = endTime;
		chart.getXYPlot().getDomainAxis().setRange(startTime, endTime);
		needNewData = false;	// there is no controller yet
		//this.addPropertyChangeListener(NEW_DATA_PROP, this);
		ctrlList = new ArrayList<DataController>();
		//this.add(this.createPopupMenu(true, false, true, false, true));
		SignalViewMouseAdapter mAdapt = new SignalViewMouseAdapter("");
		this.addMouseListener(mAdapt);
		this.addMouseWheelListener(mAdapt);
		this.addKeyListener(new SignalViewKeyAdapter());
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
		return;
	}
	
	/**
	 * Adds the given {@link gst.data.DataController} to this {@code SignalView}. If the {@code DataController} was already added to this
	 * {@code SignalView} nothing happens. 
	 * @param dataCtrl the {@code DataController} to connect to
	 */
	public void addController(DataController dataCtrl) {
		if(ctrlList.contains(dataCtrl) == false) {
			ctrlList.add(dataCtrl);
			this.needNewData = true;
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
			this.getChart().getXYPlot().getDomainAxis().setRange(this.startTime, this.endTime);
			this.needNewData = true;
		}
		return;
	}
	
	/**
	 * Sets the numerical range for the time-axis. Convenience method for {@link #setTimeAxisBounds(double, double)}.
	 */
	public void setTimeAxisBounds(Range range) {
		this.setTimeAxisBounds(range.getLowerBound(), range.getUpperBound());
	}
	
	/**
	 * Returns the numerical range for the time-axis.
	 * @return upper and lower bound of the time axis of this chart
	 */
	public Range getTimeAxisBounds() {
		return new Range(this.startTime, this.endTime);
	}
	
	/**
	 * Moves the view over the time axis by given percentage of displayed range. Positive values increase the bounds, negatives decrease the
	 * values.<br>
	 * Example:<br>
	 * time axis bounds are 40 .. 60; value = 50; new bounds -> (40 .. 60)  +  (60 - 40) * 50 / 100 = 50 .. 70
	 * @param value percentage value of displayed time axis range to shift view for-/backward
	 */
	public void shiftTimeAxisRelative(double value) {
		Range axisBounds = this.getTimeAxisBounds();
		double range = axisBounds.getUpperBound() - axisBounds.getLowerBound();
		axisBounds = Range.shift(axisBounds, range * value / 100, true);
		this.setTimeAxisBounds(axisBounds);
	}
	
	/**
	 * Shrinks or increases the range of the time axis by a relative value (percent point) of the displayed range. Positive values decreases
	 * and negative values increases the range ignoring values {@code <= -100} and {@code >= 100}.<br>
	 * Example:<br>
	 * time axis bounds are 40 .. 60; value = 50; new bounds -> (40 .. 60)  +-  (60 - 40) * (50/2) / 100 = 45 .. 55
	 * @param value percentage value of displayed time axis range to zoom in/out
	 */
	public void zoomTimeAxisRelative(double value) {
		if(value <= -100 || value >= 100) {
			return;
		}
		Range axisBounds = this.getTimeAxisBounds();
		double range = axisBounds.getUpperBound() - axisBounds.getLowerBound();
		double newStart = axisBounds.getLowerBound() + value / 100 * range / 2;
		double newEnd = axisBounds.getUpperBound() - value / 100 * range / 2;
		this.setTimeAxisBounds(newStart, newEnd);
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
	 * Requests new data from controller if necessary before painting this component.
	 * @see org.jfree.chart.ChartPanel#paintComponent(java.awt.Graphics)
	 * @see javax.swing.JComponent#paintComponents(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		if(needNewData == true) {
			this.updateData();
		}
		super.paintComponent(g);
	}
	
	/**
	 * Opens a dialog for selecting data to display.
	 */
	public void openDataSelection() {
		DataSelectionDialog dialog = new DataSelectionDialog(this.ctrlList);
		List<DataController> newList = dialog.show();
		if(newList.isEmpty()) {
			// ignore zero selection
			return;
		}
		ctrlList.clear();
		Iterator<DataController> it = newList.iterator();
		while(it.hasNext()) {
			this.addController(it.next());
		}
		this.removeTimeAxisMarker();		// needed for removed ValueController
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Ask the user for a new color for a specific data.
	 */
	public void openColorSelection() {
		DataSelectionDialog dialog = new DataSelectionDialog(this.ctrlList, null);
		DataController selectedController = dialog.showSingleSelection();
		if(selectedController == null) {
			return;
		}
		Color newColor = JColorChooser.showDialog(MainWindow.getInstance(), "Farbauswahl", null);
		if(newColor == null) {
			return;
		}
		selectedController.setPreferredColor(newColor);
	}
	
	/**
	 * Changes the background color of view.
	 * @param on true -> highlight color, false -> default background color
	 */
	public void focusHighlight(boolean on) {
		if(on == true) {
			this.getChart().setBackgroundPaint(Settings.getInstance().ui.getHighlightColor());
		} else {
			this.getChart().setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
		}
	}
	
	/**
	 * Switches the display of the legend.
	 * @param on if true legend will be shown
	 */
	public void showLegend(boolean on) {
		if(on == true && this.getChart().getLegend() == null) {
			this.getChart().addLegend(new LegendTitle(this.getChart().getXYPlot()));
		}
		if(on == false) {
			this.getChart().removeLegend();
		}
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
		Debug.println(Debug.signalView, "SignalView().updateData() called and running " + DBG_not + "in EventDispatchThread.");
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		Iterator<DataController> it = ctrlList.iterator();
		XYSeries curSeries;	// current series
		
		// TODO implement custom color
		removeTimeAxisMarker();
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
	
	public void updateTimeAxisMarkers() {
		Iterator<DataController> it = ctrlList.iterator();
		removeTimeAxisMarker();
		while(it.hasNext()) {
			DataController ctrl = it.next();
			if(ctrl.isAnnotation()) {
				this.paintTimeAxisMarkers(ctrl);
			}
		}
	}
	
	/**
	 * Sets for all annotations given by the {@code DataController} DomainMarkers.
	 * @param ctrl
	 */
	private void paintTimeAxisMarkers(DataController ctrl) {
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

	/* * * intern classes * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	/**
	 * Keyboard event handler for {@code SignalView}.
	 * @author Enrico Grunitz
	 * @version 0.1.0 (06.08.2012)
	 */
	protected static class SignalViewKeyAdapter extends KeyAdapter {
		private static final int ALL_MODIFIERS = InputEvent.ALT_DOWN_MASK |
												 InputEvent.CTRL_DOWN_MASK |
												 InputEvent.META_DOWN_MASK |
												 InputEvent.SHIFT_DOWN_MASK;
		/** @see java.awt.event.KeyAdapter#keyReleased(java.awt.event.KeyEvent) */
		@Override
		public void keyReleased(KeyEvent event) {
			if((event.getComponent() instanceof SignalView) == false) {
				Debug.println(Debug.signalViewKeyAdapter, "target of key-release event is not a signalview. Event: " + event.toString());
				return;
			}
			SignalView target = (SignalView)event.getComponent();
			int modifiers = event.getModifiersEx();
			boolean noModifier = ((modifiers & ALL_MODIFIERS) == 0);
			switch(event.getKeyCode()) {
			case KeyEvent.VK_E:
				if(noModifier == true) {
					target.openDataSelection();
				}
				break;
			case KeyEvent.VK_C:
				if(noModifier == true) {
					target.openColorSelection();
				}
				break;
			case KeyEvent.VK_Q:
				if(noModifier == true) {
					SignalPanel.getInstance().removeSignal(target);
				}
				break;
			case KeyEvent.VK_L:
				if(noModifier == true) {
					target.showLegend(false);
				}
				break;
			}
			return;
		}
		
		/** @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent) */
		@Override
		public void keyPressed(KeyEvent event) {
			if((event.getComponent() instanceof SignalView) == false) {
				Debug.println(Debug.signalViewKeyAdapter, "target of key-press event is not a signalview. Event: " + event.toString());
				return;
			}
			SignalView target = (SignalView)event.getComponent();
			int modifiers = event.getModifiersEx();
			boolean noModifier = ((modifiers & ALL_MODIFIERS) == 0);
			switch(event.getKeyCode()) {
			case KeyEvent.VK_L:
				if(noModifier == true) {
					target.showLegend(true);
				}
				break;
			}
			return;
		}
	}
	
	/**
	 * Mouse action handler for {@code SignalView}.
	 * @author Enrico Grunitz
	 * @version 0.1.0 (06.08.2012)
	 */
	protected static class SignalViewMouseAdapter extends NamedMouseAdapter {
		private static String eventType;
		private static String eventComment;
		
		public SignalViewMouseAdapter(String nameExtension) {
			super("SignalView" + nameExtension);
			SignalViewMouseAdapter.eventType = "N";
			SignalViewMouseAdapter.eventComment = "";
			StatusBar.getInstance().updateText(SignalViewMouseAdapter.eventType, SignalViewMouseAdapter.eventComment);
		}
		
		/** @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent) */
		@Override
		public void mouseClicked(MouseEvent event) {
			Debug.println(Debug.signalViewMouseAdapter, "mouse entered " + this.getComponentName());
			if((event.getComponent() instanceof SignalView) == false) {
				Debug.println(Debug.signalViewMouseAdapter, "target of mouse event is not a signalview. Event: " + event.toString());
				return;
			}
			SignalView target = (SignalView)event.getComponent();
			int modifiers = event.getModifiersEx();
			switch(event.getButton()) {
			case MouseEvent.BUTTON2:
				if(Main.getSelectedAnnotation() != null) {
					if((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {		// CTRL down
						Rectangle2D dataRect = target.getScreenDataArea();
						if(dataRect.contains(event.getPoint())) {
							// calculate time
							Range timeAxis = target.getTimeAxisBounds();
							double time = timeAxis.getLength() / dataRect.getWidth() * (event.getX() - dataRect.getX()) + timeAxis.getLowerBound();
							Main.getSelectedAnnotation().removeAnnotation(Main.getSelectedAnnotation().getAnnotation(time));
							target.updateTimeAxisMarkers();
						}
						return;	// CTRL means only delete, no adding
					}
					if((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {		// SHIFT down
						EditEventDialog eed = new EditEventDialog(SignalViewMouseAdapter.eventType, SignalViewMouseAdapter.eventComment);
						if(eed.show() == true) {
							SignalViewMouseAdapter.eventType = eed.getType();
							SignalViewMouseAdapter.eventComment = eed.getComment();
							StatusBar.getInstance().updateText(SignalViewMouseAdapter.eventType, SignalViewMouseAdapter.eventComment);
						}
					}	// no else, so the event is added even when editing
					Rectangle2D dataRect = target.getScreenDataArea();
					if(dataRect.contains(event.getPoint())) {
						// calculate time
						Range timeAxis = target.getTimeAxisBounds();
						double time = timeAxis.getLength() / dataRect.getWidth() * (event.getX() - dataRect.getX()) + timeAxis.getLowerBound();
						Main.getSelectedAnnotation().addAnnotation(time, SignalViewMouseAdapter.eventType, SignalViewMouseAdapter.eventComment);
						// hack updateing without checking
						target.updateTimeAxisMarkers();
					}
				} else {
					Debug.println(Debug.signalViewMouseAdapter, "adding canceled due to missing annotation selection");
				}
				break;
			default:
				Debug.println(Debug.signalViewMouseAdapter, "unhandled button click");
			}
			
		}
		
		/** @see gst.ui.NamedMouseAdapter#mouseEntered(java.awt.event.MouseEvent) */
		@Override
		public void mouseEntered(MouseEvent event) {
			Debug.println(Debug.signalViewMouseAdapter, "mouse entered " + this.getComponentName());
			if((event.getComponent() instanceof SignalView) == false) {
				Debug.println(Debug.signalViewMouseAdapter, "target of mouse event is not a signalview. Event: " + event.toString());
				return;
			}
			SignalView target = (SignalView)event.getComponent();
			// focus request
			boolean retval = target.requestFocusInWindow();
			if(retval == false) {
				Debug.println(Debug.signalViewMouseAdapter, "focus will fail/failed");
			}
			// change background
			target.focusHighlight(true);
		}
		
		/** @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent) */
		@Override
		public void mouseExited(MouseEvent event) {
			if((event.getComponent() instanceof SignalView) == false) {
				Debug.println(Debug.signalViewMouseAdapter, "target of mouse event is not a signalview. Event: " + event.toString());
				return;
			}
			SignalView target = (SignalView)event.getComponent();
			// change background
			target.focusHighlight(false);
		}
		
		/** @see java.awt.event.MouseAdapter#mouseWheelMoved(java.awt.event.MouseWheelEvent) */
		@Override
		public void mouseWheelMoved(MouseWheelEvent event) {
			Debug.println(Debug.signalViewMouseAdapter, this.getComponentName() + " detected mousewheel motion -> " + event.toString());
			if((event.getComponent() instanceof SignalView) == false) {
				Debug.println(Debug.signalViewMouseAdapter, "target of mouse event is not a signalview. Event: " + event.toString());
				return;
			}
			SignalView target = (SignalView)event.getComponent();
			int modifiers = event.getModifiersEx();
			if((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
				// shift was pressed -> zoom view
				double shiftValue = event.getWheelRotation() * Settings.getInstance().ui.getRelativeAxisZooming();
				target.zoomTimeAxisRelative(shiftValue);
				target.revalidate();
				target.repaint();
			} else {
				// shift wasn't pressed -> scroll view
				double shiftValue = event.getWheelRotation() * Settings.getInstance().ui.getRelativeAxisScrolling();
				target.shiftTimeAxisRelative(shiftValue);
				target.revalidate();
				target.repaint();
			}
		}
	}

}
