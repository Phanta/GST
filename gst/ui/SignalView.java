package gst.ui;
/**
 * SignalView.java created 31.05.2012
 */

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * The graph of a signal in a diagram. At this moment just a raw hull.
 * @author Enrico Grunitz
 * @version 0.0.2 (13.06.2012)
 */
public class SignalView extends ChartPanel {

	/** default serialization ID */						private static final long serialVersionUID = 1L;
	//** the default domain axis */						private static NumberAxis domainAxis = initDomainAxis();
	/** name */											private String name;

	/* * * Constructors * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	public SignalView(JFreeChart chart, boolean useBuffer) {
		super(chart, useBuffer);
		return;
	}
	
	public SignalView(String name, String yAxisLabel) {
		super(null);
		this.name = name;
		this.getChart().getXYPlot().getRangeAxis().setLabel(yAxisLabel);
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

}
