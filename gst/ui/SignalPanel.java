/**
 * SignalPanel.java created 31.05.2012
 */

package gst.ui;

import gst.Settings;
import gst.ui.layout.ComponentArrangement;
import gst.ui.layout.SignalPanelLayoutManager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPanel;

/**
 * The panel containing all signalgraphs and the controls to resize them. Implemented as Singleton.
 * @author Enrico Grunitz
 * @version 0.2 (04.06.2012)
 */
public class SignalPanel extends JPanel {

	/** serialization ID */						private static final long serialVersionUID = 1L;
	/** the singleton instance */				private static final SignalPanel myself = new SignalPanel();
	
	/** collection of the signalgraphs */		private Collection<SignalView> graphs;
	/** collection of resize controls */		private Collection controls;
	
	/** component arranger */					private ComponentArrangement compArr;
	/** last used additional args */			private int[] lastUsedArgs;
	
	/**
	 * Only used constructor.
	 */
	private SignalPanel() {
		super();
		this.addComponentListener(new SignalPanelComponentAdapter());
		this.setLayout(new SignalPanelLayoutManager());
		graphs = new ArrayList<SignalView>(Settings.getInstance().getMaxSignals());
		compArr = new ComponentArrangement();
		compArr.setPattern(ComponentArrangement.EVENHEIGHTS);
		lastUsedArgs = new int[2];
		lastUsedArgs[0] = 0;
		lastUsedArgs[1] = 0;
		// DEBUG this button only serves debug purposes
		Sidebar.getInstance().addDbgButtonAL(new ActionListener() {
												public void actionPerformed(ActionEvent ae) {
													compArr.setPattern(ComponentArrangement.TWOMEDIUM);
													lastUsedArgs[0]++;
													if(lastUsedArgs[0] == 4) {
														lastUsedArgs[0] = 0;
													}
													compArr.setPreferredSizes(new ArrayList<Component>(graphs), getWidth(), getHeight(), lastUsedArgs);
													doLayout();
												}
											});
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
	 * Adds the given SignalView to the display.
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
			compArr.setPreferredSizes(new ArrayList<Component>(graphs), this.getWidth(), this.getHeight(), lastUsedArgs);
			this.add(element);
			this.validate();
			// DEBUG console message for adding signals to signalpanel 
			System.out.println("Added signal - preffered size: " + element.getPreferredSize());
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
			compArr.setPreferredSizes(new ArrayList<Component>(graphs), this.getWidth(), this.getHeight(), lastUsedArgs);
			this.remove(element);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ComponentAdapter to save new size of panel after resizing.
	 * @author Enrico Grunitz
	 * @version 0.2 (01.06.2012)
	 */
	private class SignalPanelComponentAdapter extends ComponentAdapter {
		public void componentResized(ComponentEvent event) {
			int newHeight = getHeight();
			int newWidth = getWidth();
			/* FIXME doesn't adjusts graphs after maximizing application
			 * 		 seems like LayoutManager is informed before ComponentAdapter
			 * 		 doLayout() is a workaround
			 */
			// DEBUG system message for resizing signalpanel 
			System.out.println("new size: " + newWidth + "x" + newHeight);
			if(newWidth > 0 && newHeight > 0 && !graphs.isEmpty()) {
				compArr.setPreferredSizes(new ArrayList<Component>(graphs), newWidth, newHeight, lastUsedArgs);
				doLayout();
			}
		}
	}
}
