/**
 * DataSelectionDialog.java created on 02.08.2012
 */

package gst.ui;

import gst.Main;
import gst.data.DataController;
import gst.data.UnisensDataset;
import gst.test.Debug;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

/**
 * This is a dialog for selecting specific data from all loaded datasets. 
 * @author Enrico Grunitz
 * @version 1.0 (02.08.2012)
 */
public class DataSelectionDialog {
	
	/** list of all controllers available */					private ArrayList<DataController> controller;
	/** the message part of the dialog (JList or JLabel) */		private Component message;
	/** array of selected indices */							private int[] selectedIndices;
	
	/**
	 * Constructs the dialog from all available {@link gst.data.Unisensdataset}.
	 * @param selectedController List of initially selected {@link gst.data.DataController}
	 */
	public DataSelectionDialog(List<DataController> selectedController) {
		// gather controller from Main
		controller = new ArrayList<DataController>();
		
		List<UnisensDataset> datasets = Main.getDatasets();
		if(datasets == null) {
			// no datasets available
			this.initEmptyDialog();
			return;
		}
		Iterator<UnisensDataset> itDs = datasets.iterator();
		while(itDs.hasNext()) {
			this.controller.addAll(itDs.next().getControllerList());
		}
		if(this.controller.isEmpty()) {
			// no data inside datasets available
			this.initEmptyDialog();
			return;
		}
		// select correct indices
		if(selectedController == null || selectedController.isEmpty()) {
			selectedIndices = new int[0];
		} else {
			selectedIndices = new int[selectedController.size()];
			Iterator<DataController> itCtrl = selectedController.iterator();
			int index = -1;
			int iteratorIndex = 0;
			while(itCtrl.hasNext()) {
				index = this.controller.indexOf(itCtrl.next());
				if(index != -1) {
					this.selectedIndices[iteratorIndex] = index;
				} else {
					// controller not found, set index to an ignored value
					this.selectedIndices[iteratorIndex] = this.controller.size() + 1;
					Debug.println(Debug.dataSelectionDialog, "DataSelectionDialog did not found controller in global controller list");
				}
				iteratorIndex++;
			}
		}
		// generate string array of full names
		String[] fullNames = new String[controller.size()];
		for(int i = 0; i < controller.size(); i++) {
			fullNames[i] = controller.get(i).getFullName();
		}
		// create message
		JList<String> list = new JList<String>(fullNames);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setSelectedIndices(this.selectedIndices);
		this.message = list;
	}
	
	/**
	 * Shows the data-selection-dialog and returns the {@link gst.data.DataController} selected by the user.
	 * @return list of selected {@code DataController}
	 */
	public List<DataController> show() {
		int dialogReturn = JOptionPane.showConfirmDialog(MainWindow.getInstance(),		// parent component
														 message,						// message to display
														 "Datenauswahl",				// title string
														 JOptionPane.OK_CANCEL_OPTION,	// option type
														 JOptionPane.PLAIN_MESSAGE);	// message type
		if(dialogReturn == JOptionPane.OK_OPTION && message instanceof JList) {
			// update selectedIndices
			this.selectedIndices = ((JList<String>)message).getSelectedIndices();
		}
		return this.createControllerListFromSelectedIndices();
	}
	
	/**
	 * Initializes the member variables {@link #message} and {@link #selectedIndices} if there is no data loaded.
	 */
	private void initEmptyDialog() {
		Debug.println(Debug.dataSelectionDialog, "DataSelectionDialog has no data to display");
		message = new JLabel("keine Daten zur Auswahl verfügbar");
		selectedIndices = new int[0];
	}
	
	/**
	 * Creates a list of selected controllers from {@link #selectedIndices} and {@link #controller}.
	 * @return list of selected controllers, if no controller is selected an empty list
	 */
	private List<DataController> createControllerListFromSelectedIndices() {
		ArrayList<DataController> ctrllist = new ArrayList<DataController>(this.selectedIndices.length);
		for(int i = 0; i < this.selectedIndices.length; i++) {
			ctrllist.add(this.controller.get(this.selectedIndices[i]));
		}
		return ctrllist;
	}

}
