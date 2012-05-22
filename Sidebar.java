/**
 * Sidebar.java
 */
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Panel for Information and Signal-Overview at the side of the Main-Window. Implemented as Singleton.
 * @version 0.1 (22.05.2012)
 * @author Enrico Grunitz
 */
public class Sidebar extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Sidebar myself = new Sidebar();

	private JButton btnSideSwitch;
	
	/**
	 * Standard Constructor.
	 */
	private Sidebar() {
		super();
		btnSideSwitch = new JButton(">");
		btnSideSwitch.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
												switchSides();
											}
										});
		this.add(btnSideSwitch);
		this.setPreferredSize(new Dimension(250, 0));
	}
	
	/**
	 * @return Instance of the Sidebar
	 */
	public static Sidebar getInstance() {
		return myself;
	}
	
	
	/**
	 * Method that is called, when the Side should be diplayed on the other side of the Main Window.
	 */
	public void switchSides() {
		System.out.println("how to do?");
		return;
	}

}
