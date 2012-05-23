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
 * @version 0.1.1 (23.05.2012)
 * @author Enrico Grunitz
 */
public class Sidebar extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Sidebar myself = new Sidebar();
	
	private boolean leftAligned;

	private JButton btnSideSwitch;
	
	/**
	 * Standard Constructor.
	 */
	private Sidebar() {
		super();
		leftAligned = Settings.getDefaults().ui.getSidebarAlignment();
		btnSideSwitch = new JButton(">");
		btnSideSwitch.setEnabled(false);	// JComponent.disable() deprecated
		this.add(btnSideSwitch);
		this.setPreferredSize(new Dimension(250, 0));
		return;
	}
	
	/**
	 * @return Instance of the Sidebar
	 */
	public static Sidebar getInstance() {
		return myself;
	}
	
	/**
	 * Adds an @link java.awt.event#ActionListener ActionListener to the Button for Bar-Side-Switching 
	 * @param al the ActionListener to add
	 */
	public void addSideSwitchActionListener(ActionListener al) {
		btnSideSwitch.setEnabled(true);
		btnSideSwitch.addActionListener(al);	// Listener for move the whole Sidebar from outside
		btnSideSwitch.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent ae) {
												alSideSwitch(ae);
											}
										});		// Listener for redesigning Sidebar
		return;
	}
	
	/**
	 * @return Alignment of the Sidebar.
	 * @see Settings.UI#SIDEBAR_LEFT Settings.UI.SIDEBAR_LEFT
	 * @see Settings.UI#SIDEBAR_RIGHT Settings.UI.SIDEBAR_RIGHT
	 */
	public boolean getAlignment() {
		return leftAligned;
	}
	
	/**
	 * Redesignes the Layout of the Sidebar to adapt the it to the new Screenpostion.
	 * @param ae the ActionEvent fired
	 */
	private void alSideSwitch(ActionEvent ae) {
		leftAligned = !leftAligned;
		if(leftAligned == Settings.UI.SIDEBAR_LEFT) {
			// Sidebar on left side
			btnSideSwitch.setText(">");
		} else {
			// Sidebar on right side
			btnSideSwitch.setText("<");
		}
		return;
	}
	
}
