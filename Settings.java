import java.awt.Dimension;

/**  
 * Settings is used to store global settings of the behavior of the program. 
 * @author: Enrico Grunitz
 */
public class Settings {
	public Dimension mainWindowDimension;
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
	
	public void defaultValues() {
		mainWindowDimension.width = 1024;
		mainWindowDimension.height = 640;
		mainWindowTitle = new String("Signal Diplay Tool");
		return;
	}
}
