/**
 * DatasetManagerDialog.java created on 02.10.2012
 */

package gst.ui.dialog;

import gst.data.UnisensDataset;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;

/**
 * Dialog for managing the datasets and it's data entries by the user.
 * @author Enrico Grunitz
 * @version 0.1 (02.10.2012)
 */
public final class DatasetManagerDialog extends JDialog{
	/** dummy serialization ID */							private static final long serialVersionUID = 1L;
	/** array list of currently loaded datasets */			private ArrayList<UnisensDataset> dsArray;
	/** String array of dataset names */					private ArrayList<String> datasetNames;
	
	/* GUI elements */
	/** list of datasets */									private JList<String> guiDatasets;
	/** button for loading datasets */						private JButton btnLoadDs;
	/** button for saving datasets */						private JButton btnSaveDs;
	/** button for closing datasets */						private JButton btnCloseDs;
	/** tree list of datasets data */						private JTree guiData;
	/** text area of this dialog */							private JLabel guiText;
	/** button for closing this dialog */					private JButton btnCloseDialog;

	public DatasetManagerDialog(ArrayList<UnisensDataset> datasets, Frame owner) {
		super(owner, "Datensatz-Manager", true);
		if(datasets == null) {
			throw new NullPointerException("Cannot open dataset manager without dataset list.");
		}
		// initialize member variables and create gui components
		this.dsArray = datasets;
		this.guiDatasets = this.initDatasetList();
		this.guiDatasets.setBorder(BorderFactory.createLoweredBevelBorder());
		this.btnLoadDs = new JButton("Datensatz laden...");
		this.btnSaveDs = new JButton("Datensatz speichern");
		this.btnCloseDs = new JButton("Datensatz schlieﬂen");

		// edit layout of dialog
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.setLayout(layout);
		layout.setHonorsVisibility(false);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(this.guiDatasets)
				.addComponent(this.btnLoadDs)
				.addComponent(this.btnSaveDs)
				.addComponent(this.btnCloseDs));
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(this.guiDatasets)
				.addComponent(this.btnLoadDs)
				.addComponent(this.btnSaveDs)
				.addComponent(this.btnCloseDs));
		// modify sizes
		this.setSize(700, 560);
		this.guiDatasets.setMinimumSize(new Dimension(200, 350));

		// show dialog
		this.setVisible(true);
	}
	
	/**
	 * Initializes {@value datasetNames} and creates a new {@link javax.swing.JList}.
	 * @return the newly created JList
	 */
	private JList<String> initDatasetList() {
		this.datasetNames = new ArrayList<String>(this.dsArray.size());
		Iterator<UnisensDataset> it = this.dsArray.iterator();
		while(it.hasNext()) {
			this.datasetNames.add(it.next().getName());
		}
		return new JList<String>(this.datasetNames.toArray(new String[0]));
	}
	
	
}
