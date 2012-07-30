/**
 * DefaultMouseAdapter.java created on 27.07.2012
 */

package gst.ui;

import gst.test.Debug;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 
 * @author Enrico Grunitz
 * @version 0.1 (27.07.2012)
 */
public class NamedMouseAdapter extends MouseAdapter {
	private String compName;
	
	public NamedMouseAdapter(String componentName) {
		super();
		this.compName = componentName;
	}

	public void mouseEntered(MouseEvent event) {
		Debug.println(Debug.namedMouseAdapter, "MouseEntered " + this.compName + " with default mouse adapter");
	}
	
	public String getComponentName() {
		return this.compName;
	}
}
