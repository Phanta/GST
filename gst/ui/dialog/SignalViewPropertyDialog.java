/**
 * SignalViewPropertyDialog.java created on 13.12.2012
 */

package gst.ui.dialog;

import gst.ui.MainWindow;
import gst.ui.SignalView;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Dialog for enabling and disabling coordinated zooming and scrolling for {@link gst.ui.SignalView SignalViews}.
 * @author Enrico Grunitz
 * @version 0.0.0.1 (13.12.2012)
 */
public class SignalViewPropertyDialog {
	/** SignalView object die change settings for */		private SignalView signalView;
	/** panel to display */									private Component message;
	/** checkbox for zoom-lock */							private JCheckBox zoomBox;
	/** checkbox for scroll-lock */							private JCheckBox scrollBox;

	public SignalViewPropertyDialog() {
		this.zoomBox = new JCheckBox("verbundenes Zoomen", true);
		this.scrollBox = new JCheckBox("verbundenes Scrollen", true);
		JPanel msg = new JPanel();
		msg.setLayout(new GridLayout(2, 1, 10, 10));
		msg.add(this.zoomBox);
		msg.add(this.scrollBox);
		this.message = msg;
	}
	
	/**
	 * Shows the {@code SignalViewPropertyDialog} to the user for the given {@link gst.ui.SignalView}.
	 * @param view the {@code SignalView} to show the dialog for
	 */
	public void showDialog(SignalView view) {
		if(view == null) {
			throw new NullPointerException("cannot create options-dialog for null SignalView");
		}
		// update data
		this.signalView = view;
		this.zoomBox.setSelected(this.signalView.isZoomLocked());
		this.scrollBox.setSelected(this.signalView.isScrollLocked());
		// show dialog
		int dialogReturn = JOptionPane.showConfirmDialog(MainWindow.getInstance(),		// parent component
														 message,						// message to display
														 "Signalansicht: Einstellungen",// title string
														 JOptionPane.OK_OPTION,	// option type
														 JOptionPane.PLAIN_MESSAGE);	// message type
		// update SignalView
		if(dialogReturn == JOptionPane.OK_OPTION) {
			if(this.zoomBox.getSelectedObjects() != null) { // null returned if checkbox is not selected
				this.signalView.setZoomLock(true);
			} else {
				this.signalView.setZoomLock(false);
			}
			if(this.scrollBox.getSelectedObjects() != null) { // null returned if checkbox is not selected
				this.signalView.setScrollLock(true);
			} else {
				this.signalView.setScrollLock(false);
			}
		}
		this.signalView = null;
		return;
	}

}
