/**
 * DatasetSelectionDialog.java created on 06.08.2012
 */

package gst.ui.dialog;

import java.awt.Component;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gst.data.DatasetList;
import gst.data.UnisensDataset;
import gst.ui.MainWindow;

/**
 * Simple dialog for user-selection of any loaded dataset.
 * @author Enrico Grunitz
 * @version 0.2.0.1 (08.10.2012)
 */
public class DatasetSelectionDialog {
	/** message component of confirmation dialog */			private Component message;
	/** dataset list */										private DatasetList datasets;
	/** Combobox for dataset selection */					private JComboBox<String> comboBox;
	
	/**
	 * Creates the dialog and its graphical components. If there are no datasets to select from a plain message is
	 * displayed instead. Loads the dataset-list from {@link gst.Main} class.
	 */
	public DatasetSelectionDialog() {
		this.datasets = DatasetList.getInstance();
		if(this.datasets.size() == 0) {
			this.message = new JLabel("keine Datensätze geladen");
			this.comboBox = null;
		} else {
			JPanel tempMsg = new JPanel();
			GroupLayout layout = new GroupLayout(tempMsg);
			JLabel label = new JLabel("Bitte wählen Sie einen Datensatz.");
			this.comboBox = new JComboBox<String>(this.datasets.getNames());
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(label)
				.addComponent(this.comboBox));
			layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(label)
				.addComponent(this.comboBox));
			tempMsg.setLayout(layout);
			message = tempMsg;
		}
		return;
	}
	
	/**
	 * Shows the dialog to the user and returns teh selected dataset-object.
	 * @return selected dataset or null if abborted by user
	 */
	public UnisensDataset show() {
		int dialogReturn = JOptionPane.showConfirmDialog(MainWindow.getInstance(),		// parent component
														 this.message,					// message to display
														 "Datensatzauswahl",			// title string
														 JOptionPane.OK_CANCEL_OPTION,	// option type
														 JOptionPane.PLAIN_MESSAGE);	// message type
		if(dialogReturn == JOptionPane.OK_OPTION && this.comboBox != null) {
			return datasets.get(this.comboBox.getSelectedIndex());
		}
		return null;
	}
	
}
