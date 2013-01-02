/**
 * EditEventDialog.java created on 06.08.2012
 */

package gst.ui.dialog;

import gst.ui.MainWindow;

import java.awt.GridLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog for giving an annotation a label and a comment.
 * @author Enrico Grunitz
 * @version 0.1.0 (06.08.2012)
 */
public class EditEventDialog {
	/** message component for JOptionPane dialog */		private JPanel message;
	/** text filed for type */							private JTextField typeField;
	/** text field for comment */						private JTextField commentField;
	
	public EditEventDialog(String type, String comment) {
		this.message = new JPanel();
		this.message.setLayout(new GridLayout(2, 1));
		this.typeField = new JTextField(type);
		this.message.add(this.typeField);
		this.commentField = new JTextField(comment);
		this.message.add(this.commentField);
	}
	
	/**
	 * Displays the dialog.
	 * @return true if dialog is left via OK button.
	 */
	public boolean show() {
		int dialogReturn = JOptionPane.showConfirmDialog(MainWindow.getInstance(),		// parent component
														 this.message,					// message to display
														 "Annotations Editierung",		// title string
														 JOptionPane.OK_CANCEL_OPTION,	// option type
														 JOptionPane.PLAIN_MESSAGE);	// message type
		if(dialogReturn == JOptionPane.OK_OPTION) {
			return true;
		}
		return false;
	}

	/**
	 * @return text of the type field
	 */
	public String getType() {
		return this.typeField.getText();
	}
	
	/**
	 * @return text of the comment field
	 */
	public String getComment() {
		return this.commentField.getText();
	}
}
