/**
 * DataSelectionDialog.java created on 02.08.2012
 */

package gst.ui.dialog;

import gst.data.DataController;
import gst.data.DatasetList;
import gst.test.Debug;
import gst.ui.MainWindow;

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
 * @version 1.0.1.1 (08.10.2012)
 */
public class DataSelectionDialog {
	
	/** list of all controllers available */					private List<DataController> controller;
	/** the message part of the dialog (JList or JLabel) */		private Component message;
	/** array of selected indices */							private int[] selectedIndices;
	
	/**
	 * Constructs the dialog from all available {@link gst.data.UnisensDataset}.
	 * @param selectedController List of initially selected {@link gst.data.DataController}
	 */
	public DataSelectionDialog(List<DataController> selectedController) {
		// gather controller from Main
		controller = new ArrayList<DataController>();
		
		DatasetList datasets = DatasetList.getInstance();
		if(datasets.isEmpty()) {
			// no datasets available
			this.initEmptyDialog();
			return;
		}
		for(int dsIndex = 0; dsIndex < datasets.size(); dsIndex++) {
			this.controller.addAll(datasets.get(dsIndex).getControllerList());
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
	 * Constructs the dialog from a given list of {@link gst.data.DataController}.
	 * @param availableController List of {@code gst.data.DataController} to display
	 * @param selectedController List of initially selected {@code gst.data.DataController}
	 */
	public DataSelectionDialog(List<DataController> availableController, List<DataController> selectedController) {
		if(availableController == null || availableController.isEmpty()) {
			this.controller = new ArrayList<DataController>(0);
			this.initEmptyDialog();
			return;
		}
		controller = availableController;
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
	 * Shows the data-selection-dialog and returns the {@link gst.data.DataController}s selected by the user.
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
	 * Shows the data-selection-dialog and returns the {@link gst.data.DataController} selected by the user.
	 * @return the selected {@code DataController} or null (if non is selected)
	 */
	public DataController showSingleSelection() {
		if(this.message instanceof JList) {
			((JList<String>)message).setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		int dialogReturn = JOptionPane.showConfirmDialog(MainWindow.getInstance(),		// parent component
														 message,						// message to display
														 "Datenauswahl",				// title string
														 JOptionPane.OK_CANCEL_OPTION,	// option type
														 JOptionPane.PLAIN_MESSAGE);	// message type
		if(dialogReturn == JOptionPane.OK_OPTION) {
			// update selectedIndices
			this.selectedIndices = ((JList<String>)message).getSelectedIndices();
		}
		if(this.selectedIndices.length == 0) {
			return null;
		}
		return this.controller.get(this.selectedIndices[0]);
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
