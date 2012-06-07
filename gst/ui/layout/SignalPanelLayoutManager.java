/**
 * SignalPanelLayoutManager.java created 04.06.2012
 */

package gst.ui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

/** 
 * LayoutManager for the SignalPanel. It arranges the Components in a vertical arrangement. It tries to use preferred vertical sizes and if this
 * fails it resizes all elements scaling with the preferred size. In horizontal scope it tries to use preferred size or else maximum available
 * width.  
 * @version 0.2.1 (01.06.2012)
 * @author Enrico Grunitz
 */
public class SignalPanelLayoutManager implements LayoutManager {
	/**
	 * Needed by LayoutManager. Does nothing.
	 * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
	 */
	public void addLayoutComponent(String str, Component c) {
		return;	// no need for strings for our components
	}

	/**
	 * Needed by LayoutManager. Lays out the given Container.
	 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
	 */
	public void layoutContainer(Container parent) {
		if(parent != null) {
			Insets ins = parent.getInsets();
			Dimension targetDim = removeInsets(parent.getSize(), ins);
			Dimension prefDim = removeInsets(this.preferredLayoutSize(parent), ins);
			int curY = ins.top;
			Rectangle rect = new Rectangle();
			Component[] comps = parent.getComponents();
			double yScaleFactor = 1.0;
			if(prefDim.height > targetDim.height) {
				yScaleFactor = (double)targetDim.height / (double)prefDim.height; 
			}
			for(int i = 0; i < comps.length; i++) {
				rect.x = ins.left;
				rect.y = curY;
				if(comps[i].getPreferredSize().getWidth() <= targetDim.width) {
					rect.width = comps[i].getPreferredSize().width; 
				} else {
					rect.width = targetDim.width;
				}
				rect.height = (int)Math.round( (double)comps[i].getPreferredSize().getHeight() * yScaleFactor);
				comps[i].setBounds(rect);
				curY += rect.height;
			}
		}
	}

	/**
	 * Needed by LayoutManager. Returns the minimum size of the given container with this Layout.
	 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
	 */
	public Dimension minimumLayoutSize(Container parent) {
		Dimension minDim = new Dimension(0, 0);
		if(parent != null) {
			// getting sizes of components
			Component[] comps = parent.getComponents();
			for(int i = 0; i < comps.length; i++) {
				minDim.height += comps[i].getMinimumSize().height;
				minDim.width = Math.max(minDim.width, comps[i].getMinimumSize().width);
			}
			// adding sizes of insets
			minDim = addInsets(minDim, parent.getInsets());
		}
		return minDim;
	}

	/**
	 * Needed by LayoutManager. Returns the preferred size of the given container with this Layout.
	 * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
	 */
	public Dimension preferredLayoutSize(Container parent) {
		Dimension dim = new Dimension(0, 0);
		if(parent != null) {
			// getting sizes of components
			Component[] comps = parent.getComponents();
			for(int i = 0; i < comps.length; i++) {
				dim.height += comps[i].getPreferredSize().height;
				dim.width = Math.max(dim.width, comps[i].getPreferredSize().width);
			}
			// adding inset sizes
			dim = addInsets(dim, parent.getInsets());
		}
		return dim;
	}

	/**
	 * Needed by LayoutManager. Does nothing.
	 * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
	 */
	public void removeLayoutComponent(Component arg0) {
		return;	// nothing to do here - yay holidays
	}
	
	/**
	 * Adds the insets to the dimension.
	 * @param dim Dimension to add to
	 * @param ins Insets to be added
	 * @return the new calculated Dimension
	 */
	private static final Dimension addInsets(Dimension dim, Insets ins) {
		return new Dimension(dim.width + ins.left + ins.right, dim.height + ins.top + ins.bottom);
	}
	
	/**
	 * Subtracts the insets from the dimension.
	 * @param dim the Dimension
	 * @param ins Insets to be subtracted
	 * @return the new calculated Dimension
	 */
	private static final Dimension removeInsets(Dimension dim, Insets ins) {
		return new Dimension(dim.width - ins.left - ins.right, dim.height - ins.top - ins.bottom);
	}
}