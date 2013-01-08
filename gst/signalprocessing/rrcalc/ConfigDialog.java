/**
 * ConfigDialog.java created on 12.10.2012
 */

package gst.signalprocessing.rrcalc;

import gst.data.BufferedValueController;
import gst.data.DataController;
import gst.data.DatasetList;
import gst.test.Debug;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Configuration dialog for {@link gst.signalprocessing.rrcalc.RRCalculator}.
 * @author Enrico Grunitz
 * @version 0.0.0.3 (16.10.2012)
 */
public class ConfigDialog extends JDialog implements ActionListener {

	/** space between components */						private static final int SPACE = 5;
	/** space between components */						private static final int SPACE_SMALL = 2;

	/** combobox for dataset selection */				private JComboBox<String> cbDataset;
	/** ArrayList of String arrays */					private ArrayList<String[]> annoName;
	/** ArrayList of AnnotationController */			private ArrayList<DataController[]> annoCtrl; 
	/** combobox of available annotations */			private JComboBox<String> cbAnnotation;
	/** textfield for new value entry */				private JTextField tfValue;
	/** button for accepting choice */					private JButton btnOk;
	/** button for canceling operation */				private JButton btnCancel;
	
	public ConfigDialog() {
		super();
		this.setTitle("RR-Berechnung konfigurieren");
		DatasetList dsl = DatasetList.getInstance();
		this.annoName = new ArrayList<String[]>();
		this.annoCtrl = new ArrayList<DataController[]>();
		// create gui elements
		this.cbDataset = new JComboBox<String>();
		this.cbDataset.addActionListener(this);
		this.cbAnnotation = new JComboBox<String>();
		//this.cbAnnotation.addActionListener(this);
		this.prepareComboboxes(dsl);
		this.tfValue = new JTextField(".csv");
		this.btnOk = new JButton("Annehmen");
		this.btnOk.addActionListener(this);
		this.btnCancel = new JButton("Abbrechen");
		this.btnCancel.addActionListener(this);
		
		// add elements to the dialog and layout them
		this.getContentPane().setLayout(new BorderLayout());
		Box box = Box.createVerticalBox();
		box.add(new JLabel("Datensatz wählen:"));
		box.add(Box.createVerticalStrut(SPACE_SMALL));
		box.add(this.cbDataset);
		box.add(Box.createVerticalStrut(SPACE));
		box.add(new JLabel("Annotation wählen:"));
		box.add(Box.createVerticalStrut(SPACE_SMALL));
		box.add(this.cbAnnotation);
		box.add(Box.createVerticalStrut(SPACE));
		box.add(new JLabel("Name für Werte eingeben:"));
		box.add(Box.createVerticalStrut(SPACE_SMALL));
		box.add(this.tfValue);
		box.setBorder(BorderFactory.createEmptyBorder(SPACE, SPACE, SPACE, SPACE));
		this.getContentPane().add(box, BorderLayout.CENTER);
		
		box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(this.btnOk);
		box.add(Box.createHorizontalStrut(SPACE));
		box.add(this.btnCancel);
		box.setBorder(BorderFactory.createEmptyBorder(SPACE, SPACE, SPACE, SPACE));
		this.getContentPane().add(box, BorderLayout.SOUTH);
		this.setSize(350, 200);
	}
	
	/** @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) */
	@Override public void actionPerformed(ActionEvent event) {
		DatasetList dsl = DatasetList.getInstance();
		if(event.getSource() == this.btnOk) {
			// get settings and perform signal processing
			this.setVisible(false);
			if(dsl == null || dsl.isEmpty()) {	// no datasets available
				return;	// nothing to do here
			}
			if(this.cbAnnotation.getSelectedIndex() == -1 || this.cbDataset.getSelectedIndex() == -1) {
				Debug.println(Debug.rrConfig, "kein Datensatz oder keine Annotation gewählt");
				return;
			}
			DataController source = this.annoCtrl.get(this.cbDataset.getSelectedIndex())[this.cbAnnotation.getSelectedIndex()];
			BufferedValueController bvc = dsl.get(this.cbDataset.getSelectedIndex()).createValues(this.tfValue.getText());
			//(new RRLiveCalculator(source, bvc)).start();
			this.createSignalProcessor(source, bvc);
			return;
		} else if(event.getSource() == this.btnCancel) {
			// close dialog without action
			this.setVisible(false);
			return;
		} else if(event.getSource() == this.cbDataset) {
			// change source of annotation comboBox on datasset selection change
			if(this.isVisible()) {
				Debug.println(Debug.rrConfig, "event from dataset combobox: " + event.toString());
				int index = this.cbDataset.getSelectedIndex();
				// update annotation comboBox
				this.cbAnnotation.setModel(new DefaultComboBoxModel<String>(this.annoName.get(index)));
			}
			return;
		} else if(event.getSource() == this.cbAnnotation) {
			// nothing to do here
			return;
		} else {
			// dialog is called
			Debug.println(Debug.rrConfig, "Dialog called: " + event.toString());
			this.prepareComboboxes(dsl);		// updateing comboboxes
			this.setVisible(true);
			return;
		}
	}
	
	/**
	 * Creates the {@link gst.signalprocessing.SignalProcessor}.
	 * @param source {@link gst.data.DataController} of source signal
	 * @param target {@link gst.data.BufferedValueController} of the target signal
	 */
	protected void createSignalProcessor(DataController source, BufferedValueController target) {
		if((new RRCalculator(source, target)).run() != 0) {
			Debug.println(Debug.rrConfig, "operation failed!");
		}
	}
	
	/**
	 * Sets the model of the dataset-combobox to represent the given {@link gst.data.DatasetList}. Prepares the
	 * {@code ArrayList}s {@link #annoCtrl} and {@link #annoName} to reflect the annotations of the datasets.
	 * @param dsl the {@code DatasetList} to prepare for
	 */
	private void prepareComboboxes(DatasetList dsl) {
		this.annoName.clear();
		this.annoCtrl.clear();
		if(dsl == null || dsl.size() == 0) {
			// set empty comboboxes
			this.cbDataset.setModel(new DefaultComboBoxModel<String>());
			this.cbAnnotation.setModel(new DefaultComboBoxModel<String>());
			return;
		}
		// update dataset combobox
		this.cbDataset.setModel(new DefaultComboBoxModel<String>(dsl.getNames()));
		this.cbDataset.setSelectedIndex(0);
		// prepare annotation combobox support lists/arrays
		for(int iDs = 0; iDs < dsl.size(); iDs++) {	// for every dataset
			// collect annotation datacontroller
			ArrayList<DataController> annoDcs = new ArrayList<DataController>();
			ArrayList<String> annoNames = new ArrayList<String>();
			Iterator<DataController> it = dsl.get(iDs).getControllerList().iterator();
			while(it.hasNext()) {
				DataController dc = it.next(); 
				if(dc.isAnnotation()) {
					annoDcs.add(dc);	// save datacontroller
					annoNames.add(dc.getEntryId());	// save name
				}
			}
			// collect names of annotations
			this.annoName.add(annoNames.toArray(new String[annoNames.size()]));
			this.annoCtrl.add(annoDcs.toArray(new DataController[annoDcs.size()]));
		}
		this.cbAnnotation.setModel(new DefaultComboBoxModel<String>(this.annoName.get(0)));
		this.cbAnnotation.setSelectedIndex(0);
	}

}
