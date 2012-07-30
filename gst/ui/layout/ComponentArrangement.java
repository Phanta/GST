/**
 * ComponentArrangement.java created 04.06.2012
 */

package gst.ui.layout;

import gst.test.Debug;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Class that sets the preferred sizes of a Collection of Components to predefined patterns.
	 * <table>
	 * 		<tr>
	 * 			<th>EVENHEIGHTS</th>
	 * 			<th>all components have the maximum width and the same height to fill the specified area</th>
	 * 		</tr>
	 * 		<tr>
	 * 			<th>ONEBIG</th>
	 * 			<th>one component gets 60% of the available height, the rest fills the remaining space; all have maximum width<br>
	 * 				params[0] is the index of "the big one"</th>
	 * 		</tr>
	 * 		<tr>
	 * 			<th>TWOMEDIUM</th>
	 * 			<th>two components get each 35% of avialable height, the rest fills the remaining space; all have maximum width<br>
	 * 				params[1] is the index of first component<br>
	 * 				params[2] is the second components index<br>
	 * 				if both indices are the same ONEBIG size is used for the selected component</th>
	 * 		</tr>
	 * </table>
 * @author Enrico Grunitz
 * @version 0.1.3 (23.07.2012)
 */
public class ComponentArrangement {
	/* all pattern numbers must be consecutive */
	/** first pattern number */										private static final int FIRSTPATTERN = 1;
	/** all Components with even heights and maximum width */		public static final int EVENHEIGHTS = 1;
	/** one Component has 60% of available height rest is even */	public static final int ONEBIG = 2;
	/** ratio of screenheight for ONEBIG */							private static final double ONEBIGRATIO = 0.6;
	/** two Components take each 35% of the screen, rest is even */	public static final int TWOMEDIUM = 3;
	/** ration of screenheight for TWOMEDIUM */						private static final double TWOMEDIUMRATIO = 0.35;
	/** last pattern number */										private static final int LASTPATTERN = 3;
	
	/** index of the selected component for ONEBIG pattern */		public static final int INDEX_ONEBIG = 0;
	/** first component index for TWOMEDIUM pattern */				public static final int INDEX1_TWOMEDIUM = 1;
	/** second component index for TWOMEDIUM pattern */				public static final int INDEX2_TWOMEDIUM = 2;
	/** maximum component index used */								public static final int INDEX_MAX = 2;
	
	/** the pattern used for the arrangement */						private int pattern;
	/** indices of selected components for different views */		private int index[];
	
	/**
	 * Standard constructor. Default pattern is EVENHEIGHTS. First components selected.
	 */
	public ComponentArrangement() {
		pattern = EVENHEIGHTS;
		index = new int[INDEX_MAX + 1];
		index[INDEX_ONEBIG] = 0;
		index[INDEX1_TWOMEDIUM] = 0;
		index[INDEX2_TWOMEDIUM] = 1;
		return;
	}
	
	/**
	 * Sets the pattern.
	 * @param p the pattern to use
	 * @return false if the pattern is unknown and no changes are made, else true
	 */
	public boolean setPattern(int p) {
		if(p < FIRSTPATTERN || p  > LASTPATTERN) {
			return false;
		}
		pattern = p;
		return true;
	}
	
	public int getPattern() {
		return pattern;
	}
	
	/**
	 * Selects the component(s) for the pattern. 
	 * @param index the index of the index to set
	 * @param component the index of the component
	 */
	public void select(int index, int component) {
		if(index < 0 || index > INDEX_MAX) {
			throw new IndexOutOfBoundsException(index + " [0, " + INDEX_MAX + "]");
		}
		this.index[index] = component;
		return;
	}
	
	/**
	 * Returns an array of component selection indices.
	 * @return the index selection array
	 */
	public int[] getSelection() {
		return index.clone();
	}
	
	/**
	 * Sets the preferred sizes of all visible components given to the previously specified pattern.
	 * @param coll Collection of Components
	 * @param width	width of the area to fill
	 * @param height height of the area to fill
	 */
	public void setPreferredSizes(Collection<Component> coll, int width, int height) {
		// DEBUGCODE setPreferredSizes() EDT check
			String DBG_not = "";
			if(!javax.swing.SwingUtilities.isEventDispatchThread()) {
				DBG_not = "NOT ";
			}
			Debug.println(Debug.componentArrangement, "ComponentArrangement.setPreferredSizes() "+ DBG_not + "running in EventDispatchThread.");

			if(coll == null) {
			throw new NullPointerException();
		}
		if(coll.isEmpty()) {
			return;
		}
		Iterator<Component> it = coll.iterator();
		int numVisibleComponents = 0;
		Collection<Component> visibleComponents = new ArrayList<Component>(coll.size());
		while(it.hasNext()) {
			Component comp = it.next();
			if(comp.isVisible() == true) {
				numVisibleComponents++;
				visibleComponents.add(comp);
			}
		}
		if(numVisibleComponents == 0) {
			return;
		}
		switch(pattern) {
		case EVENHEIGHTS:
			setEvenHeights(visibleComponents, width, height);
			break;
		case ONEBIG:
			if(index[INDEX_ONEBIG] < 0 || index[INDEX_ONEBIG] >= visibleComponents.size()) {
				throw new IndexOutOfBoundsException("ONEBIG index out of bounds: " + index[0] + " [0 - " + visibleComponents.size() + "[");
			}
			setOneBig(visibleComponents, width, height);
			break;
		case TWOMEDIUM:
			if(index[INDEX1_TWOMEDIUM] < 0 || index[INDEX1_TWOMEDIUM] >= visibleComponents.size() || index[INDEX2_TWOMEDIUM] < 0 || index[INDEX2_TWOMEDIUM] >= visibleComponents.size()) {
				throw new IndexOutOfBoundsException("TWOMEDIUM index out of bounds: " + index[INDEX1_TWOMEDIUM] + ", " + index[INDEX2_TWOMEDIUM] + " [0 - " + visibleComponents.size() + "[\n");
			}
			setTwoMedium(visibleComponents, width, height);
			break;
		default:
			System.out.println("Unknown pattern detected.");
			break;
		}
		return;
	}
	
	
	/**
	 * Adjust the sizes of the components for the EVENHEIGTHS pattern.
	 * @param coll collection of visible components
	 * @param width width to uses
	 * @param height height to use
	 */
	private void setEvenHeights(Collection<Component> coll, int width, int height) {
		int compHeight = height / coll.size();
		Iterator<Component> it = coll.iterator();
		while(it.hasNext()) {
			it.next().setPreferredSize(new Dimension(width, compHeight));
		}
		return;
	}
	
	/**
	 * Adjust the sizes of the components for the ONEBUIG pattern.
	 * @param coll collection of visible components
	 * @param width width to uses
	 * @param height height to use
	 */
	private void setOneBig(Collection<Component> coll, int width, int height) {
		int smallCompHeight = (int)Math.round(height / (coll.size() - 1) * (1 - ONEBIGRATIO));
		int bigCompHeight = (int)Math.round(height * ONEBIGRATIO);
		Iterator<Component> it = coll.iterator();
		int i = 0;
		while(it.hasNext()) {
			if(i == index[INDEX_ONEBIG]) {
				it.next().setPreferredSize(new Dimension(width, bigCompHeight));
			} else {
				it.next().setPreferredSize(new Dimension(width, smallCompHeight));
			}
			i++;
		}
		return;
	}
	
	/**
	 * Adjust the sizes of the components for the TWOMEDIUM pattern.
	 * @param coll collection of visible components
	 * @param width width to uses
	 * @param height height to use
	 */
	private void setTwoMedium(Collection<Component> coll, int width, int height) {
		int smallCompHeight;
		if(index[INDEX1_TWOMEDIUM] == index[INDEX2_TWOMEDIUM]) {
			// if both indices are same we have a "ONEMEDIUM" pattern
			smallCompHeight = (int)Math.round(height / (coll.size() - 1) * (1 - TWOMEDIUMRATIO * 1));
		} else {
			smallCompHeight = (int)Math.round(height / (coll.size() - 2) * (1 - TWOMEDIUMRATIO * 2));
		}
		int bigCompHeight = (int)Math.round(height * TWOMEDIUMRATIO);
		Iterator<Component> it = coll.iterator();
		int i = 0;
		while(it.hasNext()) {
			if(i == index[INDEX1_TWOMEDIUM] || i == index[INDEX2_TWOMEDIUM]) {
				it.next().setPreferredSize(new Dimension(width, bigCompHeight));
			} else {
				it.next().setPreferredSize(new Dimension(width, smallCompHeight));
			}
			i++;
		}
	}
}
