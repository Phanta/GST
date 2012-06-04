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
 * @version 0.1 (04.06.2012)
 */
public class ComponentArrangement {
	/* all pattern numbers must be consecutive */
	/** first pattern number */									private static final int FIRSTPATTERN = 1;
	/** all Components with even heights and maximum width */	public static final int EVENHEIGHTS = 1;
	/** last pattern number */									private static final int LASTPATTERN = 1;
	
	/** the pattern used for the arrangement */					private int pattern;
	
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
	 * </table>
	 * @param coll Collection of Components
	 * @param width	width of the area to fill
	 * @param height height of the area to fill
	 */
	public void setPreferredSizes(Collection<Component> coll, int width, int height) {
		switch(pattern) {
		case EVENHEIGHTS:
			this.setPreferredSizes(coll, width, height, null);
			break;
		default:
			System.out.println("Unknown pattern detected.");
			break;
		}
		return;
	}
	
	
	/**
	 * Sets the preferred sizes of all visible components given to the previously specified pattern.
	 * <table>
	 * 		<tr>
	 * 			<th>EVENHEIGHTS</th>
	 * 			<th>all components have the maximum width and the same height to fill the specified area</th>
	 * 		</tr>
	 * </table>
	 * @param coll Collection of Components
	 * @param width	width of the area to fill
	 * @param height height of the area to fill
	 * @param parameters additional parameters used for specific patterns
	 */
	public void setPreferredSizes(Collection<Component> coll, int width, int height, int parameters[]) {
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
		it = visibleComponents.iterator();
		switch(pattern) {
		case EVENHEIGHTS:
			int compHeight = height / numVisibleComponents;
			while(it.hasNext()) {
				it.next().setPreferredSize(new Dimension(width, compHeight));
			}
			break;
		default:
			System.out.println("Unknown pattern detected.");
			break;
		}
		return;
	}
	
}
