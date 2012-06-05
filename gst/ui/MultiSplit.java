/**
 * MultiSplit.java created on 05.06.2012
 */

package gst.ui;

import gst.Settings;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A basic class that draws a MultiSplitPanel
 * @author Enrico Grunitz
 * @version 0.1 (05.06.2012)
 */
public class MultiSplit extends JPanel {
	/** serialVersionUID */											private static final long serialVersionUID = 1L;
	/** Collection of the visual components */						private ArrayList<Component> components;
	/** Collection of dividers */									private ArrayList<Divider> controls;
	
	/**
	 * Standard Constructor.
	 */
	public MultiSplit() {
		super(true);	// use double buffering
		components = new ArrayList<Component>(Settings.getInstance().getMaxSignals());
		controls = new ArrayList<Divider>(Settings.getInstance().getMaxSignals() - 1);
		return;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Container#addImpl(java.awt.Component, java.lang.Object, int)
	 */
	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		int superIndex = -1;
		boolean isFirstItem = components.isEmpty();
		if(index != -1) {
			superIndex = 2 * index;
			components.add(index, comp);
		} else {
			components.add(comp);
		}
		super.addImpl(comp, constraints, superIndex);
		if(!isFirstItem) {
			Divider ctrl = new Divider();
			if(index != -1) {
				controls.add(index, ctrl);
			} else {
				controls.add(ctrl);
			}
			super.addImpl(ctrl, constraints, superIndex + 1);
		}
		return;
	}

	// TODO implement super.removeImpl oder wie das heissen mag

	private class Divider extends JButton {
		// TODO fill me with content, please
	}
	
}
