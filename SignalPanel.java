/**
 * SignalPanel.java created 31.05.2012
 */

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;


/**
 * The panel containing all signalgraphs and the controls to resize them. Implemented as Singleton.
 * @author Enrico Grunitz
 * @version 0.1 (31.05.2012)
 */
public class SignalPanel extends JPanel {

	/** serialization ID */						private static final long serialVersionUID = 1L;
	/** the singleton instance */				private static final SignalPanel myself = new SignalPanel();
	
	/** collection of the signalgraphs */		private Collection<SignalView> graphs;
	/** collection of resize controls */		private Collection controls;

	/**
	 * Only used constructor.
	 */
	private SignalPanel() {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		graphs = new ArrayList<SignalView>(12);
		return;
	}
	
	/**
	 * Returns the singleton instance of this class.
	 * @return instance of SignalPanel
	 */
	public static SignalPanel getInstance() {
		return myself;
	}
	
	/**
	 * Adds the given ChartPanel to the display.
	 * @param element the ChartPanel to be added
	 */
	public void addSignal(SignalView element) {
		if(element != null) {
			graphs.add(element);
			this.add(element);
		}
	}
	
	/**
	 * Removes the given ChartPanel from the diplay.
	 * @param element ChartPanel to be removed.
	 * @return true if element was removed, false if the given element was not displayed (and so cannot be removed)
	 */
	public boolean removeSignal(SignalView element) {
		if(graphs.contains(element)) {
			graphs.remove(element);
			this.remove(element);
			return true;
		} else {
			return false;
		}
	}

}
