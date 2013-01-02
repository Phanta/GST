/**
 * Sidebar.java created on 22.05.2012
 */

package gst.ui;

import gst.Settings;
import gst.ui.layout.MultiSplit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Currently not used/disabled.
 * Panel for Information and Signal-Overview at the side of the Main-Window. Implemented as Singleton.
 * @version 0.1.3 (13.06.2012)
 * @author Enrico Grunitz
 */
public class Sidebar extends JPanel {

	private static final long serialVersionUID = 1L;
	/** Singleton instance of this class*/			private static final Sidebar myself = new Sidebar();
	
	/** the parent container */						private Container oldParent;
	
	/** value for left alignment of the Sidebar */	private boolean leftAligned;

	/** JPanel for top line of buttons*/			private JPanel panNorth;
	/** JPanel for the main area*/					private JPanel panCenter;
	/** JPanel for signal overview*/				private JPanel panSouth;
	/** button for side switching*/					private JButton btnSideSwitch;
	/** filler for northern button line */			private Component glueNorth;
	
	/** testing purpose */							private JButton btnSize;
	/** testing purpose */							private JButton btnPause;
	
	/**
	 * Standard Constructor.
	 */
	private Sidebar() {
		super();
		this.setLayout(new BorderLayout());

		oldParent = null;
		
		leftAligned = Settings.getInstance().ui.getSidebarAlignment();
		
		// upper panel
/*		panNorth = new JPanel();
		panNorth.setLayout(new BoxLayout(panNorth, BoxLayout.LINE_AXIS));
		glueNorth = Box.createHorizontalGlue();
		btnSideSwitch = new JButton();
		btnSideSwitch.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent ae) {
												alSideSwitch(ae);
											}
										});		// Listener for redesigning Sidebar
		designNorthPanel();
		this.add(panNorth, BorderLayout.NORTH);
*/
		// center panel
/*		panCenter = new JPanel();
		panCenter.setLayout(new BorderLayout());
		btnSize = new JButton("+");
		panCenter.add(btnSize, BorderLayout.NORTH);
		btnPause = new JButton("Pause");
		panCenter.add(btnPause, BorderLayout.SOUTH);
		btnPause.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent ae) {
											return;
										}
									});
		this.add(panCenter, BorderLayout.CENTER);
		designCenterCenterMultiSplit();
*/		
		// lower panel
/*		panSouth = SignalOverview.getInstance();
		this.add(panSouth, BorderLayout.SOUTH);
*/	
		return;
	}
	
	
	/**
	 * @return Instance of the Sidebar
	 */
	public static Sidebar getInstance() {
		return myself;
	}
	
	/**
	 * Adds itself to the parent Component assuming it has a BorderLayoutManager.
	 * @see javax.swing.JComponent#addNotify()
	 */
	@Override
	public void addNotify() {
		if(oldParent != getParent()) {
			oldParent = getParent();
			updateParentPosition();
		}
		super.addNotify();
		return;
	}
	
	/**
	 * @return Alignment of the Sidebar.
	 * @see gst.Settings.UI#SIDEBAR_LEFT Settings.UI.SIDEBAR_LEFT
	 * @see gst.Settings.UI#SIDEBAR_RIGHT Settings.UI.SIDEBAR_RIGHT
	 */
	public boolean getAlignment() {
		return leftAligned;
	}
	
	public void addDbgButtonAL(ActionListener al) {
		btnSize.addActionListener(al);
		return;
	}
	
	/**
	 * Redesignes the Layout of the Sidebar to adapt the it to the new Screenpostion. Alters the member {@link Sidebar#leftAligned leftAligned}.
	 * @param ae the ActionEvent fired
	 */
	private void alSideSwitch(ActionEvent ae) {
		leftAligned = !leftAligned;
		designNorthPanel();
		updateParentPosition();
		return;
	}
	
	/**
	 * Updates this Components Position in the parent component. Assumes it (the parent) has BorderLayout. 
	 */
	private void updateParentPosition() {
		if(getParent() != null) {
			if(leftAligned == true) {
				getParent().add(this, BorderLayout.LINE_START);
			} else {
				getParent().add(this, BorderLayout.LINE_END);
			}
		}
		revalidate();
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
	
	/**
	 * Designs the center of the center panel with own MultiSplit. 
	 */
	// DEBUGCODE test MultiSplit
	private void designCenterCenterMultiSplit() {
		MultiSplit ms = new MultiSplit();
		JPanel p1 = new JPanel();
        p1.setBackground(Color.PINK);
		ms.add(p1);
        JPanel p2 = new JPanel();
        p2.setBackground(Color.YELLOW);
		ms.add(p2);
        JPanel p3 = new JPanel();
        p3.setBackground(Color.CYAN);
		ms.add(p3);
		ms.setSize(ms.getMaximumSize());
		panCenter.add(ms, BorderLayout.CENTER);
		return;
	}
}
