/**
 * SignalOverview.java created 24.05.2012
 */

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;


/**
 * This class represents the Signal-Overview-Panel of the GUI. 
 * @version 0.1 (24.05.2012)
 * @author Enrico Grunitz
 */
public class SignalOverview extends Chart2D {

	private static final long serialVersionUID = 1L;
	/** Singleton instance of this class*/			private static final SignalOverview myself = new SignalOverview();

	public static SignalOverview getInstance() {
		return myself;
	}
	
	/**
	 * Standard constructor. 
	 */
	private SignalOverview() {
	    this.setBackground(Color.BLACK);
	    this.setPreferredSize(new Dimension(250, 80));

	    ITrace2D trace = new Trace2DSimple();
	    this.addTrace(trace);
	    trace.setColor(Color.WHITE);
	    trace.addPoint(0, 250);
	    for(int i = 1; i < 250; i++) {
	    	trace.addPoint(i, i);
	    }
	    trace.addPoint(250, 0);
	    trace.setName("");
	    
	    System.out.println(this.getSize().toString());
	    
	}

}
