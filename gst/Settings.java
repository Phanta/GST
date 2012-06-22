/**
 * Settings.java created 22.05.2012
 */

package gst;

import java.awt.Dimension;

/**  
 * Settings is used to store global settings of the behavior of the program. Implemented as Singleton.
 * @version 0.2.0 (04 06.2012)
 * @author Enrico Grunitz 
 */
public class Settings {
	/** Singleton instance of this class. */			private static Settings myself = new Settings();
	
	/** sub settings of the user interface */			public UI ui;
	
	/** default number of maximum signals */			private int signalCount;
	

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
	
	/**
	 * Wrapper class for settings for the User Interface of the application.
	 * @version 0.1.2 (29.05.2012)
	 * @author Enrico Grunitz
	 */
	public static class UI {
		/** Value for left aligned Sidebar. */			public static final boolean SIDEBAR_LEFT = true;
		/** Value for right aligned Sidebar. */			public static final boolean SIDEBAR_RIGHT = false;
		
		/** dimension of the main window */				private Dimension dimMainWindow;
		/** title string of main window */				private String titleMainWindow;
		
		/** alignment of the sidebar */					private boolean sidebarAlignment;
		/** width in pixel of sidebar */				private int sidebarWidth;
		
		/** width of signaloverview */					private int signalOverviewWidth;
		/** height of signaloverview */					private int signalOverviewHeight;
		
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
			
			sidebarAlignment = SIDEBAR_LEFT;
			sidebarWidth = 250;
			
			signalOverviewWidth = sidebarWidth;
			signalOverviewHeight = 80;
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
	}
}
