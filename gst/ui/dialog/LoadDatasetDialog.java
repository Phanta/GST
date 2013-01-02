/**
 * LoadDatasetDialog.java created on 05.10.2012
 */

package gst.ui.dialog;

import gst.data.UnisensDataset;
import gst.ui.MainWindow;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Wrapper class for file chooser dialog. 
 * @author Enrico Grunitz
 * @version 0.0.0.1 (05.10.2012)
 */
public class LoadDatasetDialog {
	/** singleton instance */				private static LoadDatasetDialog instance = new LoadDatasetDialog();
	/** path to display */					private String path;
	
	private LoadDatasetDialog() {
		this.path = System.getProperty("user.home");
	}
	
	/**
	 * @return singleton instance of this class
	 */
	public static LoadDatasetDialog getInstance() {
		return instance;
	}
	
	/**
	 * Shows a file-open-dialog and let the user choose a dataset.
	 * @return the choosen dataset or null
	 */
	public UnisensDataset show() {
		JFileChooser dialog = new JFileChooser(new File(this.path));
		dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		dialog.removeChoosableFileFilter(dialog.getChoosableFileFilters()[0]);
		FileFilter filter = new FileNameExtensionFilter("XML Datei", "xml");
		dialog.addChoosableFileFilter(filter);
		int dialogReturnValue = dialog.showOpenDialog(MainWindow.getInstance());
		if(dialogReturnValue == JFileChooser.APPROVE_OPTION) {
			// selecting the directory which contains the unisens.xml
			if(dialog.getSelectedFile().isDirectory() ==  false) {
				this.path = dialog.getSelectedFile().getParentFile().getAbsolutePath();
			} else {
				this.path = dialog.getSelectedFile().getAbsolutePath();
			}
			return new UnisensDataset(this.path, true);
		}
		return null;
	}
	
}
