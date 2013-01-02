/**
 * DefaultMouseAdapter.java created on 27.07.2012
 */

package gst.ui;

import gst.test.Debug;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MouseAdapter implementation with debug messages.
 * @author Enrico Grunitz
 * @version 0.1 (31.07.2012)
 */
public class NamedMouseAdapter extends MouseAdapter {
	private String compName;
	
	public NamedMouseAdapter(String name) {
		super();
		this.setComponentName(name);
	}

	/** @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent) */
	@Override
	public void mouseEntered(MouseEvent event) {
		Debug.println(Debug.namedMouseAdapter, "MouseEntered " + this.compName + " with default mouse adapter");
	}
	/** @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent) */
	@Override
	public void mouseReleased(MouseEvent event) {
		Debug.println(Debug.namedMouseAdapter, "mouse click on " + this.compName);
	}

	/** @return {@link #compName} */
	public String getComponentName() {
		return this.compName;
	}
	
	/**
	 * Sets the name for this {@link java.awt.event.MouseAdapter}. If the new name is {@code null} or empty it is changed to '-?-' instead. 
	 * @param name the new name
	 */
	public void setComponentName(String name) {
		this.compName = name;
		if(this.compName == null || this.compName.length() == 0) {
			this.compName = "-?-";
		}
	}
}
