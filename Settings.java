import java.awt.Dimension;

/**  
 * Settings is used to store global settings of the behavior of the program.
 * @version 0.1 (22.05.2012)
 * @author Enrico Grunitz 
 */
public class Settings {
	/**
	 * Dimension (width and height) of the Application Window.
	 */
	public Dimension mainWindowDimension;
	/**
	 * Title of the Application Window.
	 */
	public String mainWindowTitle;
	
	
	/**
	 * Initializes the Object with default settings.
	 */
	public Settings() {
		// initialize objects
		mainWindowDimension = new Dimension();
		// set all to default values
		this.defaultValues();
	}
	
	/**
	 * Resets the object to default values.
	 */
	public void defaultValues() {
		mainWindowDimension.width = 1024;
		mainWindowDimension.height = 640;
		mainWindowTitle = new String("Signal Diplay Tool");
		return;
	}
}
