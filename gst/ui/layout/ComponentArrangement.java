/**
 * ComponentArrangement.java created 04.06.2012
 */

package gst.ui.layout;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Class that sets the preferred sizes of a Collection of Components to predefined patterns.
 * @author Enrico Grunitz
 * @version 0.1.1 (04.06.2012)
 */
public class ComponentArrangement {
	/* all pattern numbers must be consecutive */
	/** first pattern number */										private static final int FIRSTPATTERN = 1;
	/** all Components with even heights and maximum width */		public static final int EVENHEIGHTS = 1;
	/** one Component has 60% of available height rest is even */	public static final int ONEBIG = 2;
	/** ratio of screenheight for ONEBIG */							private static final double ONEBIGRATIO = 0.6;
	/** last pattern number */										private static final int LASTPATTERN = 2;
	
	/** the pattern used for the arrangement */						private int pattern;
	
	/**
	 * Standard constructor. Default pattern is EVENHEIGHTS.
	 */
	public ComponentArrangement() {
		pattern = EVENHEIGHTS;
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
	
	/**
	 * Sets the preferred sizes of all visible components given to the previously specified pattern.
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
	 * </table>
	 * @param coll Collection of Components
	 * @param width	width of the area to fill
	 * @param height height of the area to fill
	 * @param params[] additional parameters used for specific patterns
	 */
	public void setPreferredSizes(Collection<Component> coll, int width, int height, int[] params) {
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
			if(params == null) {
				throw new NullPointerException("ONEBIG pattern needs index");
			}
			if(params[0] < 0 || params[0] >= visibleComponents.size()) {
				throw new IndexOutOfBoundsException("ONEBIG index out of bounds: " + params[0] + " [0 - " + visibleComponents.size() + "[");
			}
			setOneBig(visibleComponents, width, height, params[0]);
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
	 * @param index index of the big component
	 */
	private void setOneBig(Collection<Component> coll, int width, int height, int index) {
		int smallCompHeight = (int)Math.round(height / (coll.size() - 1) * (1 - ONEBIGRATIO));
		int bigCompHeight = (int)Math.round(height * ONEBIGRATIO);
		Iterator<Component> it = coll.iterator();
		int i = 0;
		while(it.hasNext()) {
			if(i == index) {
				it.next().setPreferredSize(new Dimension(width, bigCompHeight));
			} else {
				it.next().setPreferredSize(new Dimension(width, smallCompHeight));
			}
			i++;
		}
		return;
	}
}
