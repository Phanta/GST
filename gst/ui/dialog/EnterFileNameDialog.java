/**
 * EnterFileNameDialog.java created on 06.08.2012
 */

package gst.ui.dialog;

import gst.ui.MainWindow;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Dialog that ask the user for a file name.
 * @author Enrico Grunitz
 * @version 0.1.0 (06.08.2012)
 */
public class EnterFileNameDialog {
	/** text field for filename */		private JTextField text;
	
	public EnterFileNameDialog() {
		this.text = new JTextField();
	}
	
	/**
	 * Displays the dialog.
	 * @return filename entered by the user or null if aborted
	 */
	public String show() {
		int dialogReturn = JOptionPane.showConfirmDialog(MainWindow.getInstance(),		// parent component
														 this.text,						// message to display
														 "Dateinamen eingeben",			// title string
														 JOptionPane.OK_CANCEL_OPTION,	// option type
														 JOptionPane.PLAIN_MESSAGE);	// message type
		if(dialogReturn == JOptionPane.OK_OPTION) {
			return text.getText();
		}
		return null;
	}

}
