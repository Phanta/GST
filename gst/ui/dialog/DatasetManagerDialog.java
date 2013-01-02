/**
 * DatasetManagerDialog.java created on 02.10.2012
 */

package gst.ui.dialog;

import gst.data.DataController;
import gst.data.DatasetList;
import gst.data.UnisensDataset;
import gst.test.Debug;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * Dialog for managing the datasets and its data entries by the user.
 * @author Enrico Grunitz
 * @version 0.1.0.4 (08.10.2012)
 */
public final class DatasetManagerDialog extends JDialog
										implements ActionListener,
												   ListSelectionListener {
	/** dummy serialization ID */							private static final long serialVersionUID = 1L;
	/** array list of currently loaded datasets */			private DatasetList datasets;
	/** array of the root nodes for every dataset */		private ArrayList<DefaultMutableTreeNode> datasetTrees;
	
	/* GUI elements */
	/** list of datasets */									private JList<String> guiDatasets;
	/** button for loading datasets */						private JButton btnLoadDs;
	/** button for saving datasets */						private JButton btnSaveDs;
	/** button for closing datasets */						private JButton btnCloseDs;
	/** tree list of datasets data */						private JTree guiData;
	/** {@code JScrollPane} for data list */				private JScrollPane guiDataScrollPane;
	/** text area of this dialog */							private JLabel guiText;
	/** button for closing this dialog */					private JButton btnCloseDialog;

	public DatasetManagerDialog(Frame owner) {
		super(owner, "Datensatz-Manager", true);
		// initialize member variables
		this.datasets = DatasetList.getInstance();
		this.initDatasetTrees();
		
		// create gui components
		this.guiDatasets = new JList<String>(this.datasets.getNames());
		this.guiDatasets.setBorder(BorderFactory.createLoweredBevelBorder());
		this.guiDatasets.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.guiDatasets.addListSelectionListener(this);
		
		this.btnLoadDs = new JButton("Datensatz laden...");
		this.btnLoadDs.addActionListener(this);
		
		this.btnSaveDs = new JButton("Datensatz speichern");
		this.btnSaveDs.addActionListener(this);
		this.btnSaveDs.setEnabled(false);
		
		this.btnCloseDs = new JButton("Datensatz schlieﬂen");
		this.btnCloseDs.addActionListener(this);
		this.btnCloseDs.setEnabled(false);
		
		this.guiData = new JTree(new DefaultTreeModel((TreeNode)null));	// need to cast .getModel() to DefaultTreeModel
		this.guiData.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.guiDataScrollPane = new JScrollPane(this.guiData,
												 ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
												 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.guiDataScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
		
		this.guiText = new JLabel("<html></html>");	// using html to make it max size
		this.guiText.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		this.btnCloseDialog = new JButton("Dialog schlieﬂen");
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
			.addComponent(this.guiDataScrollPane)
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
				.addComponent(this.guiDataScrollPane)
				.addComponent(this.guiText))
			.addComponent(this.btnCloseDialog));
		
		// modify sizes
		this.setSize(1024, 565);
		this.guiDatasets.setMinimumSize(new Dimension(200, 350));
		this.guiDataScrollPane.setMinimumSize(new Dimension(250, 450));
		this.guiDataScrollPane.setMaximumSize(new Dimension(250, 480));
		this.guiText.setMinimumSize(new Dimension(300, 480));

		// show dialog
		this.setVisible(true);
	}
	
	
	/**
	 * Initializes the {@code TreeNode}-array.
	 */
	private void initDatasetTrees() {
		this.datasetTrees = new ArrayList<DefaultMutableTreeNode>(this.datasets.size());
		for(int i = 0; i < this.datasets.size(); i++) {
			this.datasetTrees.add(this.createTreeNode(this.datasets.get(i)));
		}
	}
	
	/**
	 * Creates a {@code TreeNode} for the dataset. Every entry is a sub note of the root and every channel is a sub-node
	 * of the entry-nodes.
	 * @param ds the dataset to create the tree for
	 * @return root-node of dataset tree 
	 */
	private DefaultMutableTreeNode createTreeNode(UnisensDataset ds) {
		if(ds == null) {
			throw new NullPointerException("Cannot create TreeNode from null.");
		}
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(ds.getName());
		DefaultMutableTreeNode tempNode;
		// save unique node for every entryId 
		HashMap<String, DefaultMutableTreeNode> nodeMap = new HashMap<String, DefaultMutableTreeNode>();
		ArrayList<String> readOnlyKeys = new ArrayList<String>();	// for saving keys to mark read-only
		Iterator<DataController> it = ds.getControllerList().iterator();
		while(it.hasNext()) {
			DataController currentCtrl = it.next();
			if(nodeMap.containsKey(currentCtrl.getEntryId())) {
				// entry already known
				if(currentCtrl.getChannelName() != null) {
					tempNode = new DefaultMutableTreeNode(currentCtrl.getChannelName());
					nodeMap.get(currentCtrl.getEntryId()).add(tempNode);
				} // else should not happen
			} else {
				// create new entry node
				tempNode = new DefaultMutableTreeNode(currentCtrl.getEntryId());
				nodeMap.put(currentCtrl.getEntryId(), tempNode);
				if(currentCtrl.isReadOnly()) {
					readOnlyKeys.add(currentCtrl.getEntryId());
				}
				root.add(tempNode);
				// add channel sub-node if necessary
				if(currentCtrl.getChannelName() != null) {
					tempNode.add(new DefaultMutableTreeNode(currentCtrl.getChannelName()));
				}
			}
		}
		// mark read-only entries
		Iterator<String> keyIt = readOnlyKeys.iterator();
		while(keyIt.hasNext()) {
			DefaultMutableTreeNode node = nodeMap.get(keyIt.next());
			node.setUserObject(((String)node.getUserObject()) + " (schreibgesch¸tzt)");
		}
		return root;
	}

	/**
	 * Performs tasks as {@code ActionListener} for this dialog's buttons.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == this.btnCloseDialog) {
			// close dialog
			this.setVisible(false);
		} else if(event.getSource() == this.btnLoadDs) {
			// load dataset
			int oldSize = this.datasets.size();
			int newIndex = this.datasets.loadDataset();
			if(oldSize != this.datasets.size()) {	// updating only if we got a new dataset
				this.guiDatasets.setListData(this.datasets.getNames());
				this.datasetTrees.add(this.createTreeNode(this.datasets.get(newIndex)));
			}
			if(newIndex != -1) {
				// set selection loaded dataset
				this.guiDatasets.setSelectedIndex(newIndex);
			}
		} else if(event.getSource() == this.btnSaveDs) {
			// save selected dataset
			int selectedIndex = this.guiDatasets.getSelectedIndex();
			if(selectedIndex >= 0) {
				this.datasets.save(selectedIndex);
			}
		} else if(event.getSource() == this.btnCloseDs) {
			// close dataset
			int selectedIndex = this.guiDatasets.getSelectedIndex();
			if(selectedIndex >= 0) {
				this.datasets.close(selectedIndex);
				// remove items from ArrayLists
				this.datasetTrees.remove(selectedIndex);
				this.guiDatasets.setListData(this.datasets.getNames());
			}
		} else {
			Debug.println(Debug.datasetManagerDialog, "unknown source of action: " + event.toString());
		}
	}

	/**
	 * Performs tasks as {@code ListSelectionListener} for this dialog's JList element.
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent event) {
		if(event.getSource() == this.guiDatasets) {
			// dataset list selection changed
			if(event.getValueIsAdjusting() == false) {
				int selectedIndex = this.guiDatasets.getSelectedIndex();
				if(selectedIndex >= 0) {
					// update tree view
					((DefaultTreeModel)(this.guiData.getModel())).setRoot(this.datasetTrees.get(selectedIndex));
					// update text
					this.guiText.setText("<html>" +
							"Datensatz: " + this.datasets.get(selectedIndex).getName() + "<br>" +
							"Pfad: " + this.datasets.get(selectedIndex).getPath() + "<br><br>" +
							"Kommentar: " + this.datasets.get(selectedIndex).getComment() + "<br>" +
							"</html>");
					// update button states
					this.btnCloseDs.setEnabled(true);
					this.btnSaveDs.setEnabled(true);
				} else {
					// update tree view
					((DefaultTreeModel)(this.guiData.getModel())).setRoot(null);
					// update text
					this.guiText.setText("<html></html>");
					// update button states
					this.btnCloseDs.setEnabled(false);
					this.btnSaveDs.setEnabled(false);
				}
			}
		} else {
			Debug.println(Debug.datasetManagerDialog, "list selection occured from unknown source: " + event.toString());
		}
		
	}
	
}
