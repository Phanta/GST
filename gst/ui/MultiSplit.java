/**
 * MultiSplit.java created on 05.06.2012
 */

package gst.ui;

import gst.Settings;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A basic class that draws a MultiSplitPanel
 * @author Enrico Grunitz
 * @version 0.1 (05.06.2012)
 */
public class MultiSplit extends JPanel {
	/** serialVersionUID */											private static final long serialVersionUID = 1L;
	/** Collection of visual components and their dividers */		private ArrayList<MSEntry> comps;
	
	/**
	 * Standard Constructor.
	 */
	public MultiSplit() {
		super(true);	// use double buffering
		comps = new ArrayList<MSEntry>(Settings.getInstance().getMaxSignals());
		return;
	}
	
	/* (non-Javadoc)
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
		MSEntry entry = new MSEntry(comp);
		MSEntry succ = null;
		MSEntry pred = null;
		if(collectionIndex != 0) {
			// init predecessor
			pred = comps.get(collectionIndex);
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

	// TODO implement super.removeImpl oder wie das heissen mag
	/* (non-Javadoc)
	 * @see java.awt.Container#remove(java.awt.Component)
	 */
	@Override
	public void remove(Component comp) {
		if(comp == null) {
			throw new NullPointerException("cannot remove null");
		}
		Iterator<MSEntry> it = comps.iterator();
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

	/* (non-Javadoc)
	 * @see java.awt.Container#remove(int)
	 */
	@Override
	public void remove(int index) {
		comps.remove(index);
		super.remove(index);
		return;
	}

	/* (non-Javadoc)
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
	 * @version 0.1 (06.06.2012)
	 */
	private class MSEntry extends Container{
		/** the component */						private Component component;
		/** the devider following the component */	private Divider divider;
		/** the entry above this */					private MSEntry predecessor;
		/** the entry below this */					private MSEntry successor;
		
		/**
		 * Convenience constructor initializing only it's Component.
		 * @param component the component
		 */
		public MSEntry(Component component) {
			this(component, null, null);
		}

		/**
		 * Convenience constructor initializing this object with it's component and the MSEntry before.
		 * @param component the component
		 * @param predecessor the predecessing MSEntry
		 */
		public MSEntry(Component component, MSEntry predecessor) {
			this(component, predecessor, null);
		}

		/**
		 * Constructor initializing this object with the component and the MSEntry before and following this element. 
		 * @param component the Component to hold
		 * @param predecessor the MSEntry above this entry
		 * @param successor the MSEntry following this entry
		 */
		public MSEntry(Component component, MSEntry predecessor, MSEntry successor) {
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
		public void setPredecessor(MSEntry predecessor) {
			this.predecessor = predecessor;
		}

		/**
		 * @param successor the successor to set
		 */
		public void setSuccessor(MSEntry successor) {
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

	}
	
	private class Divider extends JButton {
		// TODO fill me with content, please
	}
	
}
