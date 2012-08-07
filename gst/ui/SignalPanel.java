/**
 * SignalPanel.java created 31.05.2012
 */

package gst.ui;

import gst.Settings;
import gst.test.Debug;
import gst.ui.layout.ComponentArrangement;
import gst.ui.layout.SignalPanelLayoutManager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JPanel;

/**
 * The panel containing all signalgraphs and the controls to resize them. Implemented as Singleton.
 * @author Enrico Grunitz
 * @version 0.2.3 (07.08.2012)
 */
public class SignalPanel extends JPanel {

	/** serialization ID */						private static final long serialVersionUID = 1L;
	/** the singleton instance */				private static final SignalPanel myself = new SignalPanel();
	
	/** collection of the signalgraphs */		private Collection<SignalView> graphs = new ArrayList<SignalView>(Settings.getInstance().getMaxSignals());
	/** collection of resize controls */		private Collection controls;
	
	/** component arranger */					private ComponentArrangement compArr = new ComponentArrangement();
	
	/**
	 * Private singleton constructor.
	 */
	private SignalPanel() {
		super(new SignalPanelLayoutManager(), false);
		this.addComponentListener(new SignalPanelComponentAdapter());
		compArr.setPattern(ComponentArrangement.EVENHEIGHTS);

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
	 * Convenience method for {@link #addSignal(SignalView, boolean)}. The added element is visible.
	 * @param element the ChartPanel to be added
	 */
	public void addSignal(SignalView element) {
		this.addSignal(element, true);
	}
	
	/**
	 * Adds the given ChartPanel to the display.
	 * @param element the ChartPanel to be added
	 * @param visible flag if the SignalView should be displayed
	 */
	public void addSignal(SignalView element, boolean visible) {
		if(element != null) {
			graphs.add(element);
			element.setPreferredSize(new Dimension(this.getWidth(), this.getHeight() / graphs.size()));
			element.setVisible(visible);
			this.add(element);
			compArr.setPreferredSizes(new ArrayList<Component>(graphs), this.getWidth(), this.getHeight());
			this.doLayout();
			//this.revalidate();
			//this.repaint();
		}
	}
	
	/**
	 * Removes the given ChartPanel from the display.
	 * @param element ChartPanel to be removed.
	 * @return true if element was removed, false if the given element was not displayed (and so cannot be removed)
	 */
	public boolean removeSignal(SignalView element) {
		if(graphs.contains(element)) {
			graphs.remove(element);
			compArr.setPreferredSizes(new ArrayList<Component>(graphs), this.getWidth(), this.getHeight());
			this.remove(element);
			revalidate();
			repaint();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes all {@link gst.ui.SignalView}s and resets the view mode to {@link gst.ui.layout.ComponentArrangement#EVENHEIGHTS}.
	 */
	public void removeAllSignals() {
		this.removeAll();
		graphs.clear();
		compArr.setPattern(ComponentArrangement.EVENHEIGHTS);
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Returns the {@code ComponentArrangement} object of this panel.
	 * @return the component arranger
	 */
	/* package visibility */ ComponentArrangement getComponentArrangement() {
		return compArr;
	}
	
	/**
	 * Returns the number of {@code SignalView}s of this panel.
	 * @return number of {@code SignalView}s
	 */
	public int getNumSignalViews() {
		return graphs.size();
	}
	
	public void updateDomainCrosshairs(double time) {
		Iterator<SignalView> it = this.graphs.iterator();
		while(it.hasNext()) {
			it.next().updateDomainCrosshair(time);
		}
	}

	/** @see javax.swing.JComponent#paintComponent(java.awt.Graphics) */
	@Override
	protected void paintComponent(Graphics g) {
		
	}
	
	/** @see java.awt.Container#validate() */
	@Override
	public void revalidate() {
		super.revalidate();
		// DEBUGCODE SignalPanel.revalidate() call signer
			String DBG_not = "";
			if(!javax.swing.SwingUtilities.isEventDispatchThread()) {
				DBG_not = "NOT ";
			}
			Debug.println(Debug.signalPanel, "SignalPanel.revalidate() called and running " + DBG_not + "in EventDispatchThread.");

		if(graphs != null) {
			// null case happens after call of super() in constructor
			compArr.setPreferredSizes(new ArrayList<Component>(graphs), this.getWidth(), this.getHeight());
		}
	}
	
	/**
	 * ComponentAdapter to save new size of panel after resizing.
	 * @author Enrico Grunitz
	 * @version 0.2 (01.06.2012)
	 */
	private class SignalPanelComponentAdapter extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent event) {
			int newHeight = getHeight();
			int newWidth = getWidth();
			/* FIXME doesn't adjusts graphs after maximizing application
			 * 		 seems like LayoutManager is informed before ComponentAdapter
			 * 		 doLayout() is a workaround
			 */
			if(newWidth > 0 && newHeight > 0 && !graphs.isEmpty()) {
				compArr.setPreferredSizes(new ArrayList<Component>(graphs), newWidth, newHeight);
				SignalPanel.this.doLayout();
				//SignalPanel.this.revalidate();
				//SignalPanel.this.repaint();
			}
		}
	}
	
	// TODO implementation of SignalPanelMouseAdapter extending NamedMouseAdapter
	private class SignalPanelMouseAdapter extends MouseAdapter {
		public void mouseEntered(MouseEvent me) {
			//	DEBUGCODE
			Debug.println(Debug.signalPanelMouseAdapter, "mouse entered SignalPanel");
		}
	}
}
