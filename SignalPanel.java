/**
 * SignalPanel.java created 31.05.2012
 */

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

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
	
//	/** height of the Panel */					private int height;
//	/** width of the Panel */					private int width;
	
	/**
	 * Only used constructor.
	 */
	private SignalPanel() {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		graphs = new ArrayList<SignalView>(12);
//		height = this.getHeight();
//		width = this.getWidth();
		Sidebar.getInstance().addDbgButtonAL(new ActionListener() {
												public void actionPerformed(ActionEvent ae) {
													SignalView sv = graphs.iterator().next();
													Dimension d = sv.getSize();
													d.height += 20;
													sv.setSize(d);
													recalculateSizes();
												}
											});
		this.addComponentListener(new SignalPanelComponentAdapter());
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
			element.setSize(this.getWidth(), this.getHeight() / graphs.size());
//			element.setPreferredSize(new Dimension(this.getWidth(), this.getHeight() / graphs.size()));
			this.add(element);
			recalculateSizes();
			System.out.println("Added signal - size: " + element.getSize() + "; preffered size: " + element.getPreferredSize());
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
			recalculateSizes();
			return true;
		} else {
			return false;
		}
	}

	
	/**
	 * ComponentAdapter to save new size of panel after resizing.
	 * @author Enrico Grunitz
	 * @version 0.1 (31.05.2012)
	 */
	private class SignalPanelComponentAdapter extends ComponentAdapter {
		public void componentResized(ComponentEvent event) {
			int newHeight = getHeight();
			int newWidth = getWidth();
			System.out.println("new size: " + newWidth + "x" + newHeight);
			if(!(newWidth > 0 && newHeight > 0 && !graphs.isEmpty())) {
//				height = newHeight;
//				width = newWidth;
				recalculateSizes();
			}
		}
	}
	
	/**
	 * recalculates the sizes of the SignalView panels
	 */
	private void recalculateSizes() {
		SignalView sv = null;
		Iterator<SignalView> i = graphs.iterator();
		int sumHeights = 0;
		while(i.hasNext()) {
			sv = i.next();
			sumHeights += sv.getHeight();
		}
		if(sumHeights == 0) {
			sumHeights = 100;	// random default value
		}
		i = graphs.iterator();
		System.out.print("Recalc Sizes");
		while(i.hasNext()) {
			sv = i.next();
			Dimension dim = new Dimension(getWidth(), Math.round((float)this.getHeight() / (float)sumHeights * (float)sv.getHeight()));
			sv.setSize(dim);
			System.out.print("\n\t" + dim);
		}
		System.out.println("\n\t old dumHeights: "+sumHeights);
		this.validate();
	}
}
