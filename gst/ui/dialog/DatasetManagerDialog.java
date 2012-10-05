/**
 * DatasetManagerDialog.java created on 02.10.2012
 */

package gst.ui.dialog;

import gst.data.DataController;
import gst.data.UnisensDataset;
import gst.test.Debug;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * Dialog for managing the datasets and it's data entries by the user.
 * @author Enrico Grunitz
 * @version 0.1 (05.10.2012)
 */
public final class DatasetManagerDialog extends JDialog
										implements ActionListener,
												   ListSelectionListener {
	/** dummy serialization ID */							private static final long serialVersionUID = 1L;
	/** array list of currently loaded datasets */			private ArrayList<UnisensDataset> datasets;
	/** String array of dataset names */					private ArrayList<String> datasetNames;
	/** array of the root nodes for every dataset */		private ArrayList<DefaultMutableTreeNode> datasetTrees;
	
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
		// initialize member variables
		this.datasets = datasets;
		this.initDatasetTrees();
		
		// create gui components
		this.guiDatasets = this.initDatasetList();
		this.guiDatasets.setBorder(BorderFactory.createLoweredBevelBorder());
		this.guiDatasets.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.guiDatasets.addListSelectionListener(this);
		this.btnLoadDs = new JButton("Datensatz laden...");
		this.btnSaveDs = new JButton("Datensatz speichern");
		this.btnCloseDs = new JButton("Datensatz schlieﬂen");
		this.guiData = new JTree((TreeNode)null);
		this.guiData.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane dataScrollPane = new JScrollPane(this.guiData,
													 ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
													 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		dataScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
		this.guiText = new JLabel("<html>test Text<br>and more ...</html>");
		this.guiText.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.btnCloseDialog = new JButton("Schlieﬂen");
		this.btnCloseDialog.addActionListener(this);

		// edit layout
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.setLayout(layout);
		layout.setHonorsVisibility(false);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(this.guiDatasets)
				.addComponent(this.btnLoadDs)
				.addComponent(this.btnSaveDs)
				.addComponent(this.btnCloseDs))
			.addComponent(dataScrollPane)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(this.guiText)
				.addComponent(this.btnCloseDialog)));
		layout.setVerticalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(this.guiDatasets)
					.addComponent(this.btnLoadDs)
					.addComponent(this.btnSaveDs)
					.addComponent(this.btnCloseDs))
				.addComponent(dataScrollPane)
				.addComponent(this.guiText))
			.addComponent(this.btnCloseDialog));
		
		// modify sizes
		this.setSize(700, 560);
		this.guiDatasets.setMinimumSize(new Dimension(200, 350));
		this.guiData.setMinimumSize(new Dimension(200, 450));

		// show dialog
		this.setVisible(true);
	}
	
	/**
	 * Initializes {@value datasetNames} and creates a new {@link javax.swing.JList}.
	 * @return the created JList
	 */
	private JList<String> initDatasetList() {
		this.datasetNames = new ArrayList<String>(this.datasets.size());
		Iterator<UnisensDataset> it = this.datasets.iterator();
		while(it.hasNext()) {
			this.datasetNames.add(it.next().getName());
		}
		return new JList<String>(this.datasetNames.toArray(new String[0]));
	}
	
	/**
	 * Initializes the {@code TreeNode}-array.
	 */
	private void initDatasetTrees() {
		this.datasetTrees = new ArrayList<DefaultMutableTreeNode>(this.datasets.size());
		Iterator<UnisensDataset> it = this.datasets.iterator();
		while(it.hasNext()) {
			this.datasetTrees.add(this.createTreeNode(it.next()));
		}
	}
	
	private DefaultMutableTreeNode createTreeNode(UnisensDataset ds) {
		if(ds == null) {
			throw new NullPointerException("Cannot create TreeNode from null.");
		}
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(ds.getName());
		// TODO implement child nodes
		DefaultMutableTreeNode tempNode;
		List<DataController> ctrl = ds.getControllerList();
		Iterator<DataController> it = ctrl.iterator();
		while(it.hasNext()) {
			DataController currentCtrl = it.next();
			tempNode = new DefaultMutableTreeNode(currentCtrl.getFullName());
			root.add(tempNode);
			
		}
		return root;
	}

	/**
	 * Performs tasks as {@code ActionListener} for this dialog.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == this.btnCloseDialog) {
			this.setVisible(false);
		} else {
			Debug.println(Debug.datasetManagerDialog, "unknown source of action: " + event.toString());
		}
	}

	/**
	 * Performs tasks as {@code ListSelectionListener} for this dialog.
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent event) {
		if(event.getSource() == this.guiDatasets) {
			if(event.getValueIsAdjusting() == false) {
				int selectedIndex = this.guiDatasets.getSelectedIndex();
				//this.guiData.set
			}
		} else {
			Debug.println(Debug.datasetManagerDialog, "list selection occured from unknown source: " + event.toString());
		}
		
	}
	
}
