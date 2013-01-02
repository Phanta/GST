/**
 * Settings.java created 22.05.2012
 */

package gst;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

/**  
 * Settings is used to store global settings of the behavior of the program. Implemented as Singleton.
 * @version 0.2.2 (02.08.2012)
 * @author Enrico Grunitz 
 */
public class Settings {
	/** Singleton instance of this class. */			private static final Settings myself = new Settings();
	
	/** sub settings of the user interface */			public UI ui;
	
	/** default number of maximum signals */			private int signalCount;
	/** name of the application */						private String applicationName;
	/** version number of application */				private String applicationId;

	/**
	 * Access to the instance of the Settings object.
	 * @return the Instance
	 */
	public static Settings getInstance() {
		return myself;
	}
	
	/**
	 * Only Constructor. Initializes the Object with default settings.
	 */
	private Settings() {
		ui = new UI();
		this.defaultValues();
	}
	
	/**
	 * Resets settings to default values.
	 */
	public void defaultValues() {
		signalCount = 16;		// 16 channel ecg data possible
		this.applicationName = "GST";
		this.applicationId = "GST v0.1 (10.10.2012)";
		ui.defaultValues();
		return;
	}
	
	/**
	 * Returns the number of maximum signals used. Should be used to initialize Collections.
	 * @return maximum of signals
	 */
	public int getMaxSignals() {
		return signalCount;
	}
	
	/** @return {@link #applicationName} */
	public String getAppName() {
		return this.applicationName;
	}
	/** @return {@link #applicationId} */
	public String getAppId() {
		return this.applicationId;
	}
	
	/**
	 * Wrapper class for settings for the User Interface of the application.
	 * @version 0.1.4.2 (19.10.2012)
	 * @author Enrico Grunitz
	 */
	public static class UI {
		/** Value for left aligned Sidebar. */				public static final boolean SIDEBAR_LEFT = true;
		/** Value for right aligned Sidebar. */				public static final boolean SIDEBAR_RIGHT = false;
		
//		/** switch for debug messages of SignalView */	public static final boolean showSignalViewDebugMessages = true;

		
		/** dimension of the main window */					private Dimension dimMainWindow;
		/** title string of main window */					private String titleMainWindow;

		/** show text labels in toolbar */					private boolean toolbarShowLabels;
		/** modifiers needed for for close all views
		 * @see java.awt.event.ActionEvent */				private int toolbarCloseViewsModifier;
		
		/** alignment of the sidebar */						private boolean sidebarAlignment;
		/** width in pixel of sidebar */					private int sidebarWidth;
		
		/** highlight color for SignalView */				private Color signalViewHighlightColor;
		/** highlight color for SignalView crosshair */ 	private Color signalViewHighlightCrosshairColor;
		/** normal color for SignalView crosshair */		private Color signalViewCrosshairColor;
		
		/** width of signaloverview */						private int signalOverviewWidth;
		/** height of signaloverview */						private int signalOverviewHeight;
		
		/** percentage of axis movement on scrollwheel */	private double relativeAxisScrolling;
		/** percentage of axis zooming on scrollwheel */	private double relativeAxisZooming;
		/** portion of view to search for annotations */	private double relativeSnapPortion;
		/** height of area to allow annotation dragging */	private int annotationDragAreaHeight;
		
		/**
		 * Standard Constructor. Initializes UI with default Values.
		 */
		public UI() {
			this.defaultValues();
			return;
		}
		
		/**
		 * Resets the UI object to default Values.
		 */
		private void defaultValues() {
			dimMainWindow = new Dimension(1024, 680);
			titleMainWindow = new String("Signal Display Tool");
			
			toolbarShowLabels = true;
			toolbarCloseViewsModifier = ActionEvent.CTRL_MASK;
			
			sidebarAlignment = SIDEBAR_LEFT;
			sidebarWidth = 250;
			
			signalViewHighlightColor = new Color(183, 208, 176);
			this.signalViewHighlightCrosshairColor = Color.red;
			this.signalViewCrosshairColor = Color.black;
			
			signalOverviewWidth = sidebarWidth;
			signalOverviewHeight = 80;
			
			relativeAxisScrolling = 15.0;
			relativeAxisZooming = 15.0;
			this.relativeSnapPortion = 0.01;
			this.annotationDragAreaHeight = 20;
			return;
		}
		
		/**
		 * @return The Dimension (height and width) of the application Main Window.
		 */
		public Dimension getMainWindowDimension() {
			return new Dimension(dimMainWindow);
		}
		
		/**
		 * @return Title string of the Application.
		 */
		public String getMainWindowTitle() {
			return new String(titleMainWindow);
		}
		
		/**
		 * @return Alignment of the Sidebar
		 * @see Settings.UI#SIDEBAR_LEFT SIDEBAR_LEFT
		 * @see Settings.UI#SIDEBAR_RIGHT SIDEBAR_RIGHT
		 */
		public boolean getSidebarAlignment() {
			return sidebarAlignment;
		}
		
		/**
		 * @return Width of the Sidebar Panel
		 */
		public int getSidebarWidth() {
			return sidebarWidth;
		}
		
		/**
		 * @return Width of the Signaloverview Panel
		 */
		public int getSignalOverviewWidth() {
			return signalOverviewWidth;
		}
		
		/**
		 * @return Height of Signaloverview Panel
		 */
		public int getSignalOverviewHeight() {
			return signalOverviewHeight;
		}

		/** @return {@link #signalViewHighlightColor} */
		public Color getHighlightColor() {
			return signalViewHighlightColor;
		}
		/** @return {@link #signalViewCrosshairColor} */
		public Color getCrosshairColor() {
			return this.signalViewCrosshairColor;
		}
		/** @return {@link #signalViewHighlightCrosshairColor} */
		public Color getHighlightCrosshairColor() {
			return this.signalViewHighlightCrosshairColor;
		}
		/** @return {@link #relativeAxisScrolling} */
		public double getRelativeAxisScrolling() {
			return relativeAxisScrolling;
		}
		/** @return {@link #relativeAxisZooming} */
		public double getRelativeAxisZooming() {
			return relativeAxisZooming;
		}
		/** @return {@link #relativeSnapPortion} */
		public double getSignalViewRelativeSnap() {
			return this.relativeSnapPortion;
		}
		/** @return	{@link #annotationDragAreaHeight} */
		public int getAnnotationDragAreaHeight() {
			return this.annotationDragAreaHeight;
		}
		
		/** @return {@link #toolbarShowLabels} */
		public boolean showToolbarLabels() {
			return this.toolbarShowLabels;
		}
		
		/**
		 * Sets the switch for showing and hiding toolbar text labels.
		 * @param enable
		 */
		public void showToolbarLabels(boolean enable) {
			this.toolbarShowLabels = enable;
			return;
		}
		
		/** @return {@link #toolbarCloseViewsModifier} */
		public int getCloseViewsModifier() {
			return this.toolbarCloseViewsModifier;
		}
	}
}
