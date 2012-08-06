/**
 * DatasetSelectionDialog.java created on 06.08.2012
 */

package gst.ui.dialog;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import gst.Main;
import gst.data.UnisensDataset;
import gst.ui.MainWindow;

/**
 * 
 * @author Enrico Grunitz
 * @version 0.1.0 (06.08.2012)
 */
public class DatasetSelectionDialog {
	private Component message;
	private List<UnisensDataset> datasets;
	//private String[] dsStrings;
	
	public DatasetSelectionDialog() {
		this.datasets = Main.getDatasets();
		if(this.datasets.size() == 0) {
			message = new JLabel("keine Datensätze geladen");
		} else {
			message = new JComboBox<String>(this.createStringArray()); 
		}
	}
	
	public UnisensDataset show() {
		int dialogReturn = JOptionPane.showConfirmDialog(MainWindow.getInstance(),		// parent component
														 this.message,					// message to display
														 "Datensatzauswahl",			// title string
														 JOptionPane.OK_CANCEL_OPTION,	// option type
														 JOptionPane.PLAIN_MESSAGE);	// message type
		if(dialogReturn == JOptionPane.OK_OPTION && message instanceof JComboBox) {
			return datasets.get(((JComboBox<String>)message).getSelectedIndex());
		}
		return null;
	}
	
	private String[] createStringArray() {
		String[] str = new String[this.datasets.size()];
		Iterator<UnisensDataset> it = datasets.iterator();
		int i = 0;
		while(it.hasNext()) {
			str[i] = it.next().getName();
		}
		return str;
	}
}
