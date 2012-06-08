/**
 * VerticalLayoutManager.java created on 07.06.2012
 */

package gst.ui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

/**
 * A LayoutManager that arranges all it components vertically. 
 * @author Enrico Grunitz
 * @version 1.0.1 (08.06.2012)
 */
public abstract class VerticalLayoutManager implements LayoutManager2 {

	/**
	 * Needed by Interface LayoutManger. Does nothing.
	 * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
	 */
	@Override
	public void addLayoutComponent(String name, Component component) {
		return;
	}

	/**
	 * Needed by Interface LayoutManger.
	 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
	 */
	@Override
	public abstract void layoutContainer(Container component);

	/**
	 * Needed by Interface LayoutManger.
	 * Calculates the minimum layout size for all visible Components of the parent Container.
	 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
	 */
	@Override
	public Dimension minimumLayoutSize(Container parent) {
		if(parent == null) {
			throw new NullPointerException("null has no minimum size");
		}
		synchronized(parent.getTreeLock()) {
			int numComps = parent.getComponentCount();
			Dimension minDim = new Dimension(0, 0);
			for(int i = 0; i < numComps; i++) {
				Component comp = parent.getComponent(i);
				if(comp.isVisible() == true) {
					minDim.height += comp.getMinimumSize().height;
					minDim.width = Math.max(minDim.width, comp.getMinimumSize().width);
				}
			}
			return addInsets(minDim, parent.getInsets());
		}
	}

	/**
	 * Needed by Interface LayoutManger.
	 * Calculates the preferred layout size for all visible Components of the parent Container.
	 * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
	 */
	@Override
	public Dimension preferredLayoutSize(Container parent) {
		if(parent == null) {
			throw new NullPointerException("null has no preferred layout size");
		}
		synchronized(parent.getTreeLock()) {
			int numComps = parent.getComponentCount();
			Dimension prefDim = new Dimension(0, 0);
			for(int i = 0; i < numComps; i++) {
				Component comp = parent.getComponent(i);
				if(comp.isVisible() == true) {
					prefDim.height += comp.getPreferredSize().height;
					prefDim.width = Math.max(prefDim.width, comp.getPreferredSize().width);
				}
			}
			return addInsets(prefDim, parent.getInsets());
		}
	}

	/**
	 * Needed by Interface LayoutManger. Does Nothing.
	 * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
	 */
	@Override
	public void removeLayoutComponent(Component component) {
		return;
	}

	/**
	 * Needed by Interface LayoutManger2. Does Nothing.
	 * @see java.awt.LayoutManager2#addLayoutComponent(java.awt.Component, java.lang.Object)
	 */
	@Override
	public void addLayoutComponent(Component component, Object constrains) {
		return;
	}

	/**
	 * Needed by Interface LayoutManger2. Returns 0.5 due to the fact components are centered.
	 * @see java.awt.LayoutManager2#getLayoutAlignmentX(java.awt.Container)
	 */
	@Override
	public float getLayoutAlignmentX(Container parent) {
		return 0.5F;
	}

	/**
	 * Needed by Interface LayoutManger2. Returns 0 due to the fact components are aligned from top of parent container.
	 * @see java.awt.LayoutManager2#getLayoutAlignmentY(java.awt.Container)
	 */
	@Override
	public float getLayoutAlignmentY(Container parent) {
		return 0F;
	}

	/**
	 * Needed by Interface LayoutManger2. Does Nothing. Has to be overridden when implementations caching information.
	 * @see java.awt.LayoutManager2#invalidateLayout(java.awt.Container)
	 */
	@Override
	public void invalidateLayout(Container parent) {
		return;
	}

	/**
	 * Needed by Interface LayoutManger2. Returns Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE).
	 * @see java.awt.LayoutManager2#maximumLayoutSize(java.awt.Container)
	 */
	@Override
	public Dimension maximumLayoutSize(Container parent) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Adds the insets to the dimension.
	 * @param dim Dimension to add to
	 * @param ins Insets to be added
	 * @return the new calculated Dimension
	 */
	protected static Dimension addInsets(Dimension dim, Insets ins) {
		return new Dimension(dim.width + ins.left + ins.right, dim.height + ins.top + ins.bottom);
	}
	
	/**
	 * Subtracts the insets from the dimension.
	 * @param dim the Dimension
	 * @param ins Insets to be subtracted
	 * @return the new calculated Dimension
	 */
	protected static Dimension removeInsets(Dimension dim, Insets ins) {
		return new Dimension(dim.width - ins.left - ins.right, dim.height - ins.top - ins.bottom);
	}

}
