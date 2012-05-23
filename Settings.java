import java.awt.Dimension;

/**  
 * Settings is used to store global settings of the behavior of the program.
 * @version 0.1.2 (23.05.2012)
 * @author Enrico Grunitz 
 */
public class Settings {
	public UI ui;
	
	public static Settings getDefaults() {
		return new Settings();
	}
	
	/**
	 * Only Constructor. Initializes the Object with default settings.
	 */
	public Settings() {
		ui = new UI();
		this.defaultValues();
	}
	
	/**
	 * Resets settings to default values.
	 */
	public void defaultValues() {
		ui.defaultValues();
		return;
	}
	
	/**
	 * Wrapper class for settings for the User Interface of the application.
	 * @version 0.1.1 (23.05.2012)
	 * @author Enrico Grunitz
	 */
	public static class UI {
		/**
		 * Value for left aligned Sidebar.
		 */
		public static final Boolean SIDEBAR_LEFT = true;
		/**
		 * Value for right aligned Sidebar.
		 */
		public static final Boolean SIDEBAR_RIGHT = false;
		
		private Dimension dimMainWindow;
		private String titleMainWindow;
		
		private boolean sidebarAlignment;
		
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
	}
}
