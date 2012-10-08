/**
 * AnnotationSelectionDialog.java created on 06.08.2012
 */

package gst.ui.dialog;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gst.data.AnnotationController;
import gst.data.DataController;
import gst.data.DatasetList;
import gst.data.UnisensDataset;
import gst.test.Debug;
import gst.ui.MainWindow;

/**
 * A dialog that asks the user to select an annotation-entry of all datasets.
 * @author Enrico Grunitz
 * @version 0.1.1.1 (08.10.2012)
 */
public class AnnotationSelectionDialog {
	
	/** The message to display */						private Component message;
	/** List that contains lists of {@link gst.data.AnnotationController}. Each sub-list has only elements of one {@link UnisensDataset}. */
														private ArrayList<ArrayList<AnnotationController>> annotation;
	/** list of string arrays for second combo box */	private ArrayList<String[]> annotationNames;
	/** selected dataset index */						private int selectedDsIndex;
	/** {@code JComboBox} of annotations */				private JComboBox<String> annoComboBox; 
														
	/** Ctor */
	public AnnotationSelectionDialog() {
		// collect all AnnotationController
		DatasetList datasets = DatasetList.getInstance();
		this.annotation = new ArrayList<ArrayList<AnnotationController>>(datasets.size());
		this.selectedDsIndex = 0;
		if(datasets.isEmpty()) {
			this.initEmptyDialog();
			return;
		}
		List<DataController> tempControllerList = null;
		ArrayList<AnnotationController> tempAnnoList = null;
		//int indexDs = 0;
		//Iterator<UnisensDataset> itDs = datasets.iterator();
		//while(itDs.hasNext()) {
		for(int indexDs = 0; indexDs < datasets.size(); indexDs++) {
			// for all datasets
			UnisensDataset ds = datasets.get(indexDs);
			tempControllerList = ds.getControllerList();
			tempAnnoList = new ArrayList<AnnotationController>();
			Iterator<DataController> itCtrl = tempControllerList.iterator();
			while(itCtrl.hasNext()) {
				// for each datacontroller in dataset
				DataController ctrl = itCtrl.next();
				if(ctrl.isAnnotation() && !ctrl.isReadOnly()) { // only display writeable annotations
					tempAnnoList.add((AnnotationController)ctrl);
				}
			}
			this.annotation.add(tempAnnoList);
		}
		// setup dialog message
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1, 10, 10));	// 2 columns, 1 row, 10 pixel horizontal and vertical gap
		// dataset combobox
		JComboBox<String> comboBox = new JComboBox<String>(datasets.getNames());
		panel.add(comboBox);
		comboBox.setSelectedIndex(this.selectedDsIndex);
		comboBox.addActionListener(new AnnotationSelectionDialogActionListener(this));
		// annotation combobox
		// create string arrays
		this.annotationNames = new ArrayList<String[]>(this.annotation.size());
		String[] tempString;
		Iterator<ArrayList<AnnotationController>> it = this.annotation.iterator();
		while(it.hasNext()) {
			tempAnnoList = it.next();
			tempString = new String[tempAnnoList.size()];
			for(int i = 0; i < tempAnnoList.size(); i++) {
				tempString[i] = tempAnnoList.get(i).getEntryId();
			}
			this.annotationNames.add(tempString);
		}
		// ... and the combobox
		this.message = panel;
		this.changeAnnoComboBox();
	}
	
	/**
	 * Shows the dialog and returns the {@link gst.data.AnnotationController} selected by the user.
	 * @return selected {@code AnnotationController}
	 */
	public AnnotationController show() {
		int dialogReturn = JOptionPane.showConfirmDialog(MainWindow.getInstance(),		// parent component
														 message,						// message to display
														 "Annotationsauswahl",			// title string
														 JOptionPane.OK_CANCEL_OPTION,	// option type
														 JOptionPane.PLAIN_MESSAGE);	// message type
		if(dialogReturn == JOptionPane.OK_OPTION && message instanceof JPanel) {
			return annotation.get(this.selectedDsIndex).get(this.annoComboBox.getSelectedIndex());
		}
		return null;
	}
	
	/**
	 * Changes the combobox with the annotations according to the selected dataset.
	 */
	private void changeAnnoComboBox() {
		if(this.annoComboBox != null) {
			((JPanel)this.message).remove(this.annoComboBox);
		}
		this.annoComboBox = new JComboBox<String>(this.annotationNames.get(this.selectedDsIndex));
		this.annoComboBox.setSize(300, this.annoComboBox.getHeight());
		((JPanel)this.message).add(this.annoComboBox);
		this.message.revalidate();
		this.message.repaint();
	}
	
	/**
	 * Initializes the member variables {@link #message}, {@link #annotationNames} and {@link #annoComboBox} if there is no data loaded.
	 */
	private void initEmptyDialog() {
		Debug.println(Debug.annotationSelectionDialog, "AnnotationSelectionDialog has no data to display");
		this.message = new JLabel("keine Daten zur Auswahl verfügbar");
		this.annotationNames = new ArrayList<String[]>(0);
		this.annoComboBox = null;
	}

	/**
	 * Simple {@code ActionListener} that changes the annotation combobox when a new dataset is selected.
	 * @author Enrico Grunitz
	 * @version 0.1.0 (06.08.2012)
	 */
	private class AnnotationSelectionDialogActionListener implements ActionListener {
		private AnnotationSelectionDialog dialog;
		
		/** @param dlg handle to the dialog itself */
		public AnnotationSelectionDialogActionListener(AnnotationSelectionDialog dlg) {
			this.dialog = dlg;
		}
		
		/** @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) */
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getSource() instanceof JComboBox) {
				this.dialog.selectedDsIndex = ((JComboBox<String>)event.getSource()).getSelectedIndex();
				this.dialog.changeAnnoComboBox();
			}
		}
		
	}
}
