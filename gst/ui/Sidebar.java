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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Divider;
import org.jdesktop.swingx.MultiSplitLayout.Leaf;

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
	
	/** testing purpose */							private JButton btnSize;
	
	/**
	 * Standard Constructor.
	 */
	private Sidebar() {
		super();
		this.setLayout(new BorderLayout());

		parent = null;
		
		leftAligned = Settings.getInstance().ui.getSidebarAlignment();
		
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
		panCenter.setLayout(new BorderLayout());
		btnSize = new JButton("+");
		panCenter.add(btnSize, BorderLayout.NORTH);
		//designCenterCenterSwingX();
		this.add(panCenter, BorderLayout.CENTER);
		designCenterCenterMultiSplit();
		
		// lower panel
		panSouth = SignalOverview.getInstance();
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
	 * Adds itself to the parent Component assuming it has a BorderLayoutManager.
	 * @see javax.swing.JComponent#addNotify()
	 */
	@Override
	public void addNotify() {
		if(parent != getParent()) {
			parent = getParent();
			updateParentPosition();
		}
		super.addNotify();
		return;
	}
	
	/**
	 * Set the parent Container. Automatically adds this component to the its parent based on {@link Sidebar#leftAligned leftAligned} with
	 * java.awt.BorderLayout.LINE_START or java.awt.BorderLayout.LINE_END LINE_END. Does not call parent.validate().
	 * @param par the parent JFrame
	 */
//	public void setParent(Container par) {
//		parent = par;
//		if(parent != null) {
//			if(leftAligned == true) {
//				parent.add(this, BorderLayout.LINE_START);
//			} else {
//				parent.add(this, BorderLayout.LINE_END);
//			}
//		}
//	}
	
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
	 * Designs the center of the center panel with an SwingX MultiSplitLayout. 
	 */
	// DEBUGCODE test SwingX MultiSplitPane
	private void designCenterCenterSwingX() {
		JXMultiSplitPane msp = new JXMultiSplitPane();
		msp.setModel(new MSPLayout());
		JPanel p1 = new JPanel();
        p1.setBackground(Color.PINK);
		msp.add(p1, MSPLayout.n1);
        JPanel p2 = new JPanel();
        p2.setBackground(Color.YELLOW);
		msp.add(p2, MSPLayout.n2);
        JPanel p3 = new JPanel();
        p3.setBackground(Color.CYAN);
		msp.add(p3, MSPLayout.n3);
		MultiSplitLayout.printModel(new MSPLayout());
		panCenter.add(msp, BorderLayout.CENTER);
		return;
	}
	
	/**
	 * class needed for SwingX MultisplitLayout.
	 * 
	 * @author Enrico Grunitz
	 * @version 0.1 (06.06.2012)
	 */
	private class MSPLayout extends MultiSplitLayout.Split {
		public static final String n1 = "1";
		public static final String n2 = "2";
		public static final String n3 = "3";
		
		public MSPLayout() {
			this.setRowLayout(false);
			Leaf l1 = new Leaf(n1);
			l1.setWeight(0.33);
			Leaf l2 = new Leaf(n2);
			l2.setWeight(0.33);
			Leaf l3 = new Leaf(n3);
			l3.setWeight(0.33);
			this.setChildren(l1, new Divider(), l2, new Divider(), l3);
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
