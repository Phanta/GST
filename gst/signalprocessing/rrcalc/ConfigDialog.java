/**
 * ConfigDialog.java created on 12.10.2012
 */

package gst.signalprocessing.rrcalc;

import gst.data.DataController;
import gst.data.DatasetList;
import gst.data.UnisensDataset;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;

/**
 * 
 * @author Enrico Grunitz
 * @version 0.0.0.1 (12.10.2012)
 */
public class ConfigDialog extends JDialog implements ActionListener {

	/** combobox for dataset selection */				private JComboBox<String> cbDataset;
	/** combobox of available annotations */			private JComboBox<String> cbAnnotation;
	/** textfield for new value entry */				private JTextField tfValue;
	/** button for accepting choice */					private JButton btnOk;
	/** button for canceling operation */				private JButton btnCancel;
	
	public ConfigDialog() {
		super();
		DatasetList dsl = DatasetList.getInstance();
		// create gui elements
		this.cbDataset = new JComboBox<String>(dsl.getNames());
		// TODO
		this.cbAnnotation = new JComboBox<String>(dsl.getNames(0));
		this.tfValue = new JTextField(".csv");
		this.btnOk = new JButton("Annehmen");
		this.btnOk.addActionListener(this);
		this.btnCancel = new JButton("Abbrechen");
		this.btnCancel.addActionListener(this);
		
		// add elements to the dialog
		this.add(this.cbDataset);
		this.add(this.cbAnnotation);
		this.add(this.tfValue);
		this.add(this.btnOk);
		this.add(this.btnCancel);
	}
	
	/** @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) */
	@Override public void actionPerformed(ActionEvent event) {
		if(event.getSource() == this.btnOk) {
			// get settings and perform signal processing
			this.setVisible(false);
			// TODO get settings
			// TODO implement signal processing
		} else if(event.getSource() == this.btnCancel) {
			// close dialog without action
			this.setVisible(false);
		} else {
			// dialog is called
			// TODO implement update combo boxes
			this.setVisible(true);
		}
		
	}

}
