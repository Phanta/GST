import java.awt.Dimension;

/**  
 * Settings is used to store global settings of the behavior of the program.
 * @version 0.1.1 (22.05.2012)
 * @author Enrico Grunitz 
 */
public class Settings {
	public UI ui;
	
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
	 * @version 0.1 (22.05.2012)
	 * @author Enrico Grunitz
	 */
	public class UI {
		private Dimension dimMainWindow;
		private String titleMainWindow;
		
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
			titleMainWindow = new String("Signal Diaplay Tool");
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
	}
}
