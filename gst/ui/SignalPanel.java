/**
 * SignalPanel.java created 31.05.2012
 */

package gst.ui;

import gst.Settings;
import gst.test.Debug;
import gst.ui.layout.ComponentArrangement;
import gst.ui.layout.SignalPanelLayoutManager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JPanel;

/**
 * The panel containing all signalgraphs and the controls to resize them. Implemented as Singleton. Supports ActionListener.
 * @author Enrico Grunitz
 * @version 0.2.5.1 (15.10.2012)
 */
public class SignalPanel extends JPanel {

	/** serialization ID */						private static final long serialVersionUID = 1L;
	/** the singleton instance */				private static final SignalPanel myself = new SignalPanel();
	
	/** collection of ActionListeners */		private Collection<ActionListener> actionListenerCollection;
	
	/** collection of the signalgraphs */		private Collection<SignalView> graphs = new ArrayList<SignalView>(Settings.getInstance().getMaxSignals());
	//** collection of resize controls */		private Collection controls;
	
	/** component arranger */					private ComponentArrangement compArr = new ComponentArrangement();
	
	/**
	 * Private singleton constructor.
	 */
	private SignalPanel() {
		super(new SignalPanelLayoutManager(), false);
		this.actionListenerCollection = new ArrayList<ActionListener>();
		this.addComponentListener(new SignalPanelComponentAdapter());
		this.addActionListener(new ScrollLockManager());
		this.addActionListener(new ZoomLockManager());
		compArr.setPattern(ComponentArrangement.EVENHEIGHTS);
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
	 * Convenience method for {@link #addSignal(SignalView, boolean)}. The added element is visible.
	 * @param element the ChartPanel to be added
	 */
	public void addSignal(SignalView element) {
		this.addSignal(element, true);
	}
	
	/**
	 * Adds the given ChartPanel to the display.
	 * @param element the ChartPanel to be added
	 * @param visible flag if the SignalView should be displayed
	 */
	public void addSignal(SignalView element, boolean visible) {
		if(element != null) {
			graphs.add(element);
			element.setPreferredSize(new Dimension(this.getWidth(), this.getHeight() / graphs.size()));
			element.setVisible(visible);
			this.add(element);
			compArr.setPreferredSizes(new ArrayList<Component>(graphs), this.getWidth(), this.getHeight());
			this.doLayout();
		}
	}
	
	/**
	 * Removes the given ChartPanel from the display.
	 * @param element ChartPanel to be removed.
	 * @return true if element was removed, false if the given element was not displayed (and so cannot be removed)
	 */
	public boolean removeSignal(SignalView element) {
		if(graphs.contains(element)) {
			graphs.remove(element);
			compArr.setPreferredSizes(new ArrayList<Component>(graphs), this.getWidth(), this.getHeight());
			this.remove(element);
			revalidate();
			repaint();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes all {@link gst.ui.SignalView}s and resets the view mode to {@link gst.ui.layout.ComponentArrangement#EVENHEIGHTS}.
	 */
	public void removeAllSignals() {
		this.removeAll();
		graphs.clear();
		compArr.setPattern(ComponentArrangement.EVENHEIGHTS);
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Returns the {@code ComponentArrangement} object of this panel.
	 * @return the component arranger
	 */
	/* package visibility */ ComponentArrangement getComponentArrangement() {
		return compArr;
	}
	
	/**
	 * Returns the number of {@code SignalView}s of this panel.
	 * @return number of {@code SignalView}s
	 */
	public int getNumSignalViews() {
		return graphs.size();
	}
	
	/**
	 * Prompts all {@link gst.ui.SignalView}s to update their crosshair to the given time.
	 * @param time point in time
	 */
	public void updateDomainCrosshairs(double time) {
		Iterator<SignalView> it = this.graphs.iterator();
		while(it.hasNext()) {
			it.next().updateDomainCrosshair(time);
		}
	}
	
	/**
	 * Prompts all {@link gst.ui.SignalView}s to center their view on the given point in time.
	 * @param time point in time
	 */
	public void centerViews(double time) {
		Iterator<SignalView> it = this.graphs.iterator();
		while(it.hasNext()) {
			it.next().centerTimeAxisOn(time);
		}
	}

	/**
	 * Adds the {@code ActionListener} to this {@code JPanel}.
	 * @param al the {@code ActionListener} to add
	 */
	public void addActionListener(ActionListener al) {
		if(!this.actionListenerCollection.contains(al)) {
			this.actionListenerCollection.add(al);
		}
	}
	
	/**
	 * Removes the given {@code ActionListener} from this {@link gst.ui.SignalPanel}.
	 * @param al the {@code ActionListener} to remove
	 */
	public void removeActionListener(ActionListener al) {
		this.actionListenerCollection.remove(al);
	}
	
	/**
	 * Notifies all registered {@code ActionListener} of the given {@code ActionEvent}.
	 * @param event the {@code ActionEvent}
	 */
	public void fireActionEvent(ActionEvent event) {
		Debug.println(Debug.signalPanel, "firering AE: " + event.toString());
		Iterator<ActionListener> it = this.actionListenerCollection.iterator();
		while(it.hasNext()) {
			it.next().actionPerformed(event);
		}
	}
	
	/** @see java.awt.Container#validate() */
	@Override public void revalidate() {
		super.revalidate();
		// DEBUGCODE SignalPanel.revalidate() call signer
			String DBG_not = "";
			if(!javax.swing.SwingUtilities.isEventDispatchThread()) {
				DBG_not = "NOT ";
			}
			Debug.println(Debug.signalPanel, "SignalPanel.revalidate() called and running " + DBG_not + "in EventDispatchThread.");

		if(graphs != null) {
			// null case happens after call of super() in constructor
			compArr.setPreferredSizes(new ArrayList<Component>(graphs), this.getWidth(), this.getHeight());
		}
	}
	
	/**
	 * ComponentAdapter to save new size of panel after resizing.
	 * @author Enrico Grunitz
	 * @version 0.2 (01.06.2012)
	 */
	private class SignalPanelComponentAdapter extends ComponentAdapter {
		@Override public void componentResized(ComponentEvent event) {
			int newHeight = getHeight();
			int newWidth = getWidth();
			/* FIXME doesn't adjusts graphs after maximizing application
			 * 		 seems like LayoutManager is informed before ComponentAdapter
			 * 		 doLayout() is a workaround
			 */
			if(newWidth > 0 && newHeight > 0 && !graphs.isEmpty()) {
				compArr.setPreferredSizes(new ArrayList<Component>(graphs), newWidth, newHeight);
				SignalPanel.this.doLayout();
				//SignalPanel.this.revalidate();
				//SignalPanel.this.repaint();
			}
		}
	}
	
	/**
	 * {@code ActionListener} that reacts on {@code ScrollActionEvent}s and updates all {@link gst.ui.SignalView}s that are scroll locked.
	 * @author Enrico Grunitz
	 * @version 0.1.0 (08.08.2012)
	 */
	private class ScrollLockManager implements ActionListener {
		/** @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) */
		@Override public void actionPerformed(ActionEvent event) {
			if(!(event instanceof ScrollToActionEvent)) {
				return;	// nothing to do here
			}
			if(!(event.getSource() instanceof SignalView)) {
				Debug.println(Debug.signalPanelScrollLockManager, "source of ScrollToActionEvent is not a SignalView");
				return;	// nothing to do here
			}
			SignalView source = (SignalView)event.getSource();
			if(source.isScrollLocked()) {
				Iterator<SignalView> it = graphs.iterator();
				while(it.hasNext()) {
					SignalView view = it.next();
					if(view.isScrollLocked()) {
						view.centerTimeAxisOn(((ScrollToActionEvent)event).getTime());
					}
				}
			} else {	// source SignalView not scrollLocked
				source.centerTimeAxisOn(((ScrollToActionEvent)event).getTime());
			}
		}
	}
	
	/**
	 * {@code ActionListener} that reacts on {@code ZoomActionEvent}s and updates all {@link gst.ui.SignalView}s that are zoom locked.
	 * @author Enrico Grunitz
	 * @version 0.1.0 (09.08.2012)
	 */
	private class ZoomLockManager implements ActionListener {
		/** @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) */
		@Override public void actionPerformed(ActionEvent event) {
			if(!(event instanceof ZoomActionEvent)) {
				return;	// nothing to do here
			}
			if(!(event.getSource() instanceof SignalView)) {
				Debug.println(Debug.signalPanelZoomLockManager, "source of ZoomActionEvent is not a SignalView");
				return;	// nothing to do here
			}
			SignalView source = (SignalView)event.getSource();
			if(source.isZoomLocked()) {		// source SignalView is zoom-locked -> update all zoom-locked views
				Iterator<SignalView> it = graphs.iterator();
				while(it.hasNext()) {
					SignalView view = it.next();
					if(view.isZoomLocked()) {
						view.zoomTimeAxis(((ZoomActionEvent)event).getRange());
					}
				}
			} else {	// source SignalView not zoomLocked -> update only source
				source.zoomTimeAxis(((ZoomActionEvent)event).getRange());
			}
		}
	}
	
	/**
	 * An {@code ActionEvent} that occurs when a {@link SignalView} should scroll to a specific point in time (domain axis wise). The source
	 * of this event should be the {@code SignalView}.
	 * @author Enrico Grunitz
	 * @version 0.1.0 (08.08.2012)
	 */
	public static class ScrollToActionEvent extends ActionEvent {
		/** serialization ID */					private static final long serialVersionUID = 208364504719887182L;
		/** default command string */			private static final String ACTIONCOMMAND = "scrollTo";
		/** point in time to scroll to */		private double time;
		
		/**
		 * Constructor implementation.
		 * @param source object that the action originates from
		 * @param when point in time the action occured
		 * @param modifiers modifier keys down during event
		 * @param targetTime point in time to scroll to
		 */
		public ScrollToActionEvent(Object source, long when, int modifiers, double targetTime) {
			super(source, ActionEvent.ACTION_PERFORMED, ACTIONCOMMAND, when, modifiers);
			this.time = targetTime;
		}
		
		/**
		 * Convenience constructor for events at the current point in time.
		 * @see gst.ui.SignalPanel.ScrollToActionEvent#SignalPanel.ScrollToActionEvent(Object, long, int, double)
		 */
		public ScrollToActionEvent(Object source, int modifiers, double targetTime) {
			this(source, System.currentTimeMillis(), modifiers, targetTime);
		}
		
		/**
		 * Convenience constructor for events at the current point in time and without any modifiers.
		 * @see gst.ui.SignalPanel.ScrollToActionEvent#SignalPanel.ScrollToActionEvent(Object, long, int, double)
		 */
		public ScrollToActionEvent(Object source, double targetTime) {
			this(source, System.currentTimeMillis(), 0, targetTime);
		}
		
		/**
		 * @return the central time value to scroll to
		 */
		public double getTime() {
			return this.time;
		}
	}

	/**
	 * An {@code ActionEvent} that occurs when a {@link SignalView} should zoom to a specific time range. The source of this event should be
	 * the {@code SignalView}.
	 * @author Enrico Grunitz
	 * @version 0.1.0 (09.08.2012)
	 */
	public static class ZoomActionEvent extends ActionEvent {
		/** serialization ID */					private static final long serialVersionUID = 2966316271281983633L;
		/** default command string */			private static final String ACTIONCOMMAND = "scrollTo";
		/** point in time to scroll to */		private double rangeValue;
		
		/**
		 * Constructor implementation.
		 * @param source object that the action originates from
		 * @param when point in time the action occured
		 * @param modifiers modifier keys down during event
		 * @param targetRange time in milliseconds the time axis should have as range
		 */
		public ZoomActionEvent(Object source, long when, int modifiers, double targetRange) {
			super(source, ActionEvent.ACTION_PERFORMED, ACTIONCOMMAND, when, modifiers);
			this.rangeValue = targetRange;
		}
		
		/**
		 * Convenience constructor for events at the current point in time.
		 * @see gst.ui.SignalPanel.ZoomActionEvent#SignalPanel.ZoomActionEvent(Object, long, int, double)
		 */
		public ZoomActionEvent(Object source, int modifiers, double targetRange) {
			this(source, System.currentTimeMillis(), modifiers, targetRange);
		}
		
		/**
		 * Convenience constructor for events at the current point in time and without any modifiers.
		 * @see gst.ui.SignalPanel.ZoomActionEvent#SignalPanel.ZoomActionEvent(Object, long, int, double)
		 */
		public ZoomActionEvent(Object source, double targetRange) {
			this(source, System.currentTimeMillis(), 0, targetRange);
		}
		
		/**
		 * @return the range value to zoom into
		 */
		public double getRange() {
			return this.rangeValue;
		}
	}
}
