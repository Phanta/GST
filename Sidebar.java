/**
 * Sidebar.java
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Panel for Information and Signal-Overview at the side of the Main-Window. Implemented as Singleton.
 * @version 0.1.2 (24.05.2012)
 * @author Enrico Grunitz
 */
public class Sidebar extends JPanel {

	private static final long serialVersionUID = 1L;
	/** Singleton instance of this class*/			private static final Sidebar myself = new Sidebar();
	
	/** the parent container */						private Container parent;
	
	/** value for left alignment of the Sidebar */	private boolean leftAligned;

	/** JPanel for top line of buttons*/			private JPanel panNorth;
	/** JPanel for the main area*/					private JPanel panCenter;
	/** JPanel for signal overview*/				private JPanel panSouth;
	/** button for side switching*/					private JButton btnSideSwitch;
	/** filler for northern button line */			private Component glueNorth;
	
	/**
	 * Standard Constructor.
	 */
	private Sidebar() {
		super();
		this.setLayout(new BorderLayout());
		//this.setPreferredSize(new Dimension(250, 0));

		parent = null;
		
		leftAligned = Settings.getDefaults().ui.getSidebarAlignment();
		
		// upper panel
		panNorth = new JPanel();
		panNorth.setLayout(new BoxLayout(panNorth, BoxLayout.LINE_AXIS));
		glueNorth = Box.createHorizontalGlue();
		btnSideSwitch = new JButton();
		//btnSideSwitch.setEnabled(false);	// JComponent.disable() deprecated
		btnSideSwitch.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent ae) {
												alSideSwitch(ae);
											}
										});		// Listener for redesigning Sidebar
		designNorthPanel();
		this.add(panNorth, BorderLayout.NORTH);

		// center panel
		panCenter = new JPanel();
		this.add(panCenter, BorderLayout.CENTER);

		// lower panel
		panSouth = new JPanel();
		this.add(panSouth, BorderLayout.SOUTH);
		
		return;
	}
	
	/**
	 * @return Instance of the Sidebar
	 */
	public static Sidebar getInstance() {
		return myself;
	}
	
	/**
	 * Set the parent Container. Automatically adds this component to the its parent based on {@link Sidebar#leftAligned leftAligned} with
	 * java.awt.BorderLayout.LINE_START or java.awt.BorderLayout.LINE_END LINE_END. Does not call parent.validate().
	 * @param par the parent JFrame
	 */
	public void setParent(Container par) {
		parent = par;
		if(parent != null) {
			if(leftAligned == true) {
				parent.add(this, BorderLayout.LINE_START);
			} else {
				parent.add(this, BorderLayout.LINE_END);
			}
		}
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
	 * Redesignes the Layout of the Sidebar to adapt the it to the new Screenpostion. Alters the member {@link Sidebar#leftAligned leftAligned}.
	 * @param ae the ActionEvent fired
	 */
	private void alSideSwitch(ActionEvent ae) {
		leftAligned = !leftAligned;
		designNorthPanel();
		if(parent != null) {
			if(leftAligned == true) {
				parent.add(this, BorderLayout.LINE_START);
			} else {
				parent.add(this, BorderLayout.LINE_END);
			}
			parent.validate();
		} else {
			this.validate();
		}
		return;
	}
	
	/**
	 * Places the buttons in the top part in order defined by {@link Sidebar#leftAligned leftAligned}.
	 * Changes text of {@link Sidebar#btnSideSwitch btnSideSwitch}.
	 */
	private void designNorthPanel() {
		if(leftAligned == true) {
			panNorth.removeAll();
			panNorth.add(glueNorth);
			btnSideSwitch.setText(">");
			panNorth.add(btnSideSwitch);
		} else {
			panNorth.removeAll();
			btnSideSwitch.setText("<");
			panNorth.add(btnSideSwitch);
			//panNorth.add(glueNorth); not yet necessary
		}
	}
	
}
