/**
 * SignalPanel.java created 31.05.2012
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * The panel containing all signalgraphs and the controls to resize them. Implemented as Singleton.
 * @author Enrico Grunitz
 * @version 0.1 (31.05.2012)
 */
public class SignalPanel extends JPanel {

	/** serialization ID */						private static final long serialVersionUID = 1L;
	/** the singleton instance */				private static final SignalPanel myself = new SignalPanel();
	
	/** collection of the signalgraphs */		private Collection<SignalView> graphs;
	/** collection of resize controls */		private Collection controls;
	
//	/** height of the Panel */					private int height;
//	/** width of the Panel */					private int width;
//testcomment
	
	/**
	 * Only used constructor.
	 */
	private SignalPanel() {
		super();
//		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setLayout(new SignalPanelLayoutManager());
		graphs = new ArrayList<SignalView>(12);
		Sidebar.getInstance().addDbgButtonAL(new ActionListener() {
												public void actionPerformed(ActionEvent ae) {
													SignalView sv = graphs.iterator().next();
													Dimension d = sv.getPreferredSize();
													d.height += 20;
													sv.setPreferredSize(d);
													//recalculateSizes();
												}
											});
		this.addComponentListener(new SignalPanelComponentAdapter());
		return;
	}
	
	/**
	 * Returns the singleton instance of this class.
	 * @return instance of SignalPanel
	 */
	public static SignalPanel getInstance() {
		return myself;
	}
	
	/**
	 * Adds the given ChartPanel to the display.
	 * @param element the ChartPanel to be added
	 */
	public void addSignal(SignalView element) {
		if(element != null) {
			graphs.add(element);
			element.setPreferredSize(new Dimension(this.getWidth(), this.getHeight() / graphs.size()));
			this.add(element);
			this.validate();
			//recalculateSizes();
			System.out.println("Added signal - size: " + element.getSize() + "; preffered size: " + element.getPreferredSize());
		}
	}
	
	/**
	 * Removes the given ChartPanel from the diplay.
	 * @param element ChartPanel to be removed.
	 * @return true if element was removed, false if the given element was not displayed (and so cannot be removed)
	 */
	public boolean removeSignal(SignalView element) {
		if(graphs.contains(element)) {
			graphs.remove(element);
			this.remove(element);
			//recalculateSizes();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ComponentAdapter to save new size of panel after resizing.
	 * @author Enrico Grunitz
	 * @version 0.1 (31.05.2012)
	 */
	private class SignalPanelComponentAdapter extends ComponentAdapter {
		public void componentResized(ComponentEvent event) {
			int newHeight = getHeight();
			int newWidth = getWidth();
			System.out.println("new size: " + newWidth + "x" + newHeight);
			if(!(newWidth > 0 && newHeight > 0 && !graphs.isEmpty())) {
				validate();
				//recalculateSizes();
			}
		}
	}
	
	/**
	 * recalculates the sizes of the SignalView panels
	 */
/*	private void recalculateSizes() {
		SignalView sv = null;
		Iterator<SignalView> i = graphs.iterator();
		int sumHeights = 0;
		while(i.hasNext()) {
			sv = i.next();
			sumHeights += sv.getHeight();
		}
		if(sumHeights == 0) {
			sumHeights = 100;	// random default value
		}
		i = graphs.iterator();
		System.out.print("Recalc Sizes");
		while(i.hasNext()) {
			sv = i.next();
			Dimension dim = new Dimension(getWidth(), Math.round((float)this.getHeight() / (float)sumHeights * (float)sv.getHeight()));
			sv.setSize(dim);
			System.out.print("\n\t" + dim);
		}
		System.out.println("\n\t old dumHeights: "+sumHeights);
		this.validate();
	}
*/
	/** 
	 * LayoutManager for the SignalPanel. It arranges the Components in a vertical arrangement. It tries to use preferred sizes.
	 * If this fails it resizes all elements scaling with the preferred size.
	 * @version 0.1 (31.05.2012)
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
			// TODO Auto-generated method stub
			if(parent != null) {
				Dimension targetDim = parent.getSize();
				Dimension prefDim = this.preferredLayoutSize(parent);
				int curY = 0;
				Rectangle rect = new Rectangle();
				Component[] comps = parent.getComponents();
				double yScaleFactor = 1.0;
				if(prefDim.height > targetDim.height) {
					yScaleFactor = (double)targetDim.height / (double)prefDim.height; 
				}
				for(int i = 0; i < comps.length; i++) {
					rect.x = 0;
					rect.y = curY;
					if(comps[i].getPreferredSize().getWidth() <= targetDim.width) {
						rect.width = comps[i].getPreferredSize().width; 
					} else {
						rect.width = targetDim.width;
					}
					rect.height = (int)Math.round( (double)comps[i].getPreferredSize().getHeight() * yScaleFactor);
					comps[i].setBounds(rect);
					comps[i].validate();
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
				Component[] comps = parent.getComponents();
				for(int i = 0; i < comps.length; i++) {
					minDim.height += comps[i].getMinimumSize().height;
					if(comps[i].getMinimumSize().width > minDim.width) {
						minDim.width = comps[i].getMinimumSize().width;
					}
				}
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
				Component[] comps = parent.getComponents();
				for(int i = 0; i < comps.length; i++) {
					dim.height += comps[i].getPreferredSize().height;
					if(comps[i].getPreferredSize().width > dim.width) {
						dim.width = comps[i].getPreferredSize().width;
					}
				}
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
		
	}
}
