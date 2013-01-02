/**
 * MultiSplit.java created on 05.06.2012
 */

package gst.ui.layout;

import gst.Settings;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A basic class that draws a MultiSplitPanel. Not used - testing purpose.
 * @author Enrico Grunitz
 * @version 0.1.1 (07.06.2012)
 */
public class MultiSplit extends JPanel {
	/** serialVersionUID */											private static final long serialVersionUID = 1L;
	/** Collection of visual components and their dividers */		private ArrayList<MultiSplitEntry> comps;
	
	/**
	 * Standard Constructor.
	 */
	public MultiSplit() {
		super(true);	// use double buffering
		comps = new ArrayList<MultiSplitEntry>(Settings.getInstance().getMaxSignals());
		this.setLayout(new MultiSplitLayoutManager());
		return;
	}
	
	/**
	 * Overrides the Container implementation to add the given component and an divider, instead of the single component.
	 * @see java.awt.Container#addImpl(java.awt.Component, java.lang.Object, int)
	 */
	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		if(comp == null) {
			throw new NullPointerException("component was null");
		}
		if(index < -1 || index > comps.size()) {
			throw new IndexOutOfBoundsException(index + " out of [-1, " + comps.size() + "]");
		}
		int collectionIndex;	// index used for comps-collection
		if(index == -1) {
			// translate -1 to last element
			collectionIndex = comps.size();
		} else {
			collectionIndex = index; 
		}
		MultiSplitEntry entry = new MultiSplitEntry(comp);
		MultiSplitEntry succ = null;
		MultiSplitEntry pred = null;
		if(collectionIndex != 0) {
			// init predecessor
			pred = comps.get(collectionIndex - 1);
			pred.setSuccessor(entry);
			entry.setPredecessor(pred);
		}
		if(collectionIndex != comps.size()) {
			// init successor
			succ = comps.get(collectionIndex);
			succ.setPredecessor(entry);
			entry.setSuccessor(succ);
		}
		comps.add(collectionIndex, entry);
		super.addImpl(entry, constraints, index);
		return;
	}

	/**
	 * Removes the given Component and it's corresponding divider from this Container.
	 * @see java.awt.Container#remove(java.awt.Component)
	 */
	@Override
	public void remove(Component comp) {
		if(comp == null) {
			throw new NullPointerException("cannot remove null");
		}
		Iterator<MultiSplitEntry> it = comps.iterator();
		int i = 0, index = 0;
		while(it.hasNext()) {
			if(it.next().getComponent().equals(comp)) {
				index = i;
			}
			i++;
		}
		this.remove(index);
		return;
	}

	/**
	 * Removes the given Component and it's corresponding divider from this Container.
	 * @see java.awt.Container#remove(int)
	 */
	@Override
	public void remove(int index) {
		comps.remove(index);
		super.remove(index);
		return;
	}

	/**
	 * Removes all Components from this Container.
	 * @see java.awt.Container#removeAll()
	 */
	@Override
	public void removeAll() {
		super.removeAll();
		comps.clear();
		return;
	}

	/**
	 * A pair of a Component and a Divider Button. Also stores its predecessing and successing pairs. If successor is null, then the Divider is
	 * invisible.
	 * @author Enrico Grunitz
	 * @version 0.1.1 (07.06.2012)
	 */
	private class MultiSplitEntry extends Container {
		/** generated serialization ID */			private static final long serialVersionUID = -1018800692300416075L;
		/** the component */						private Component component;
		/** the devider following the component */	private Divider divider;
		/** the entry above this */					private MultiSplitEntry predecessor;
		/** the entry below this */					private MultiSplitEntry successor;
		
		/**
		 * Convenience constructor initializing only it's Component.
		 * @param component the component
		 */
		public MultiSplitEntry(Component component) {
			this(component, null, null);
		}

		/**
		 * Convenience constructor initializing this object with it's component and the MSEntry before.
		 * @param component the component
		 * @param predecessor the predecessing MSEntry
		 */
		public MultiSplitEntry(Component component, MultiSplitEntry predecessor) {
			this(component, predecessor, null);
		}

		/**
		 * Constructor initializing this object with the component and the MSEntry before and following this element. 
		 * @param component the Component to hold
		 * @param predecessor the MSEntry above this entry
		 * @param successor the MSEntry following this entry
		 */
		public MultiSplitEntry(Component component, MultiSplitEntry predecessor, MultiSplitEntry successor) {
			if(component == null) {
				throw new NullPointerException();
			}
			this.component = component;
			this.divider = new Divider();
			this.predecessor = predecessor;
			this.successor = successor;
			if(this.successor == null) {
				divider.setVisible(false);
			} else {
				divider.setVisible(true);
			}
			this.setLayout(new MultiSplitEntryLayoutManager());
			this.add(component);
			this.add(divider);
		}

		/**
		 * @return the component
		 */
		public Component getComponent() {
			return component;
		}
		
		/**
		 * @return the divider
		 */
		public Divider getDivider() {
			return divider;
		}
		
		/**
		 * @param predecessor the predecessor to set
		 */
		public void setPredecessor(MultiSplitEntry predecessor) {
			this.predecessor = predecessor;
		}

		/**
		 * @param successor the successor to set
		 */
		public void setSuccessor(MultiSplitEntry successor) {
			this.successor = successor;
			if(this.successor == null) {
				divider.setVisible(false);
			} else {
				divider.setVisible(true);
			}
		}

		/**
		 * @return true if this entry has a predecessor, else false
		 */
		public boolean hasPredecessor() {
			if(this.predecessor == null) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * @return true if this entry has a successor, else false
		 */
		public boolean hasSuccessor() {
			if(this.successor == null) {
				return true;
			} else {
				return false;
			}
		}

		
		/**
		 * LayoutManager to layout the component and it's divider.
		 * @author Enrico Grunitz
		 * @version 1.0.1 (08.06.2012)
		 */
		private class MultiSplitEntryLayoutManager extends VerticalLayoutManager {
			/**
			 * Needed by Interface LayoutManager.
			 * Lays out two Components vertically. The second component (if visible) at the bottom and horizontal centered with their preferred
			 * size. The first component gets the remaining space.  
			 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
			 */
			@Override
			public void layoutContainer(Container parent) {
				if(parent == null) {
					throw new NullPointerException("cannot layout null");
				}
				synchronized(parent.getTreeLock()) {
					Component[] comps = parent.getComponents();
					if(comps.length != 2) {
						System.out.println ("this LayoutManager can only layout 2 Components");
						return;
					}
					// calculate available space
					Insets ins = parent.getInsets();
					Dimension availableSpace = parent.getSize();
					availableSpace = removeInsets(availableSpace, ins);
					// layout Divider first
					Rectangle rect = new Rectangle();
					if(comps[1].isVisible() == true) {
						Dimension prefSize = comps[1].getPreferredSize();
						if(prefSize.width < availableSpace.width && prefSize.height < availableSpace.height) {
							rect.width = prefSize.width;
							rect.height = prefSize.height;
						} else {
							// trying minimum Size
							Dimension minSize = comps[1].getMinimumSize();
							if(minSize.width < availableSpace.width && minSize.height < availableSpace.height) {
								rect.width = minSize.width;
								rect.height = minSize.height;
							} else {
								// area to small for minimum size -> abort
								return;
							}
						}
						rect.x = (availableSpace.width - rect.width) / 2 + ins.left;
						rect.y = ins.top + availableSpace.height - rect.height;
						comps[1].setBounds(rect);
						availableSpace.height -= rect.height;	// update availableSpace
					}
					if(comps[0].isVisible() == true) {
						rect.x = ins.left;
						rect.y = ins.top;
						rect.width = availableSpace.width;
						rect.height = availableSpace.height;
						comps[0].setBounds(rect);
					}
					return;
				}
			}
		}
	}
	
	/**
	 * Divider button used to resize the elements of MultiSplit.
	 * @author Enrico Grunitz
	 * @version 0.1 (07.06.2012)
	 */
	private class Divider extends JButton {
		/** generated serialization ID */					private static final long serialVersionUID = -9116895657099562652L;

		// TODO Mouselistener
		// TODO resize of components
		/**
		 * Standard Constructor.
		 */
		public Divider() {
			super("-");
			Dimension minDim = new Dimension(200, 10);
			this.setMinimumSize(minDim);
			this.setPreferredSize(minDim);
			this.setSize(minDim);
			this.setMaximumSize(minDim);
			return;
		}
		
		public void startDrag() {
			// TODO implementation
			return;
		}
	}
	
	/**
	 * Arranges all components vertically in the container. It respects minimum sizes and visibility. If there is not enough space for
	 * preferred sizes it scales heights with their preferred heights. All components width are set to parents width. 
	 * @author Enrico Grunitz
	 * @version 0.1 (07.06.2012)
	 */
	private class MultiSplitLayoutManager extends VerticalLayoutManager {
		/**
		 * lays out the container
		 * @see gst.ui.layout.VerticalLayoutManager#layoutContainer(java.awt.Container)
		 */
		@Override
		public void layoutContainer(Container parent) {
			if(parent == null) {
				throw new NullPointerException("null has no layout");
			}
			synchronized(parent.getTreeLock()) {
				Insets ins = parent.getInsets();
				Dimension targetDim = removeInsets(parent.getSize(), ins);
				Dimension prefDim = removeInsets(preferredLayoutSize(parent), ins);
				Dimension minDim = removeInsets(minimumLayoutSize(parent), ins);
				int curY = ins.top;
				Rectangle rect = new Rectangle();
				Component[] comps = parent.getComponents();
				double yScaleFactor = 1.0;
				if(prefDim.height > targetDim.height) {
					yScaleFactor = (double)(targetDim.height - minDim.height) / (double)prefDim.height; 
				}
				for(int i = 0; i < comps.length; i++) {
					if(comps[i].isVisible() == true) {
						rect.x = ins.left;
						rect.y = curY;
						rect.width = targetDim.width;
						rect.height = (int)Math.round( (double)comps[i].getPreferredSize().height * yScaleFactor) + comps[i].getMinimumSize().height;
						comps[i].setBounds(rect);
						curY += rect.height;
					}
				}
			}
		}
		
	}
}
