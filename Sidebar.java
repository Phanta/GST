/**
 * Sidebar.java
 */
import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * Panel for Information and signal-overview at the side of the Main-Window. Implemented as Singleton.
 * @version 0.1 (22.05.2012)
 * @author Enrico Grunitz
 */
public class Sidebar extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Sidebar myself = new Sidebar();
	
	
	/**
	 * Standard Constructor.
	 */
	private Sidebar() {
		super();
		this.setPreferredSize(new Dimension(250, 0));
	}
	
	/**
	 * @return Instance of the Sidebar
	 */
	public static Sidebar getInstance() {
		return myself;
	}

}
