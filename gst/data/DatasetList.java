/**
 * DatasetList.java created on 08.10.2012
 */

package gst.data;

import gst.ui.dialog.LoadDatasetDialog;

import java.util.ArrayList;

/**
 * 
 * @author Enrico Grunitz
 * @version 0.0.0.1 (08.10.2012)
 */
public class DatasetList {
	/** instance of this class */						private static DatasetList instance = new DatasetList(); 
	/** list of datasets */								private ArrayList<UnisensDataset> datasets;
	
	/**
	 * Private Constructor. Use {@link #getInstance()} to get an object.
	 */
	private DatasetList() {
		this.datasets = new ArrayList<UnisensDataset>();
		return;
	}
	
	/**
	 * Returns the instance of this class' object.
	 * @return the singleton instance
	 */
	public static DatasetList getInstance() {
		return instance;
	}
	
	/**
	 * Returns the number of elements in this list.
	 * @return the number of elements in this list
	 */
	public int size() {
		return this.datasets.size();
	}
	
	/**
	 * Returns {@code true} if this list contains no elements.
	 * @return {@code true} if this list contains no elements
	 */
	public boolean isEmpty() {
		return this.datasets.isEmpty();
	}
	
	/**
	 * Returns the dataset with the given index.
	 * @param index index of dataset
	 * @return the requested dataset
	 */
	public UnisensDataset get(int index) {
		if(index < 0 || index >= this.datasets.size()) {
			throw new IndexOutOfBoundsException("index out of bounds: 0 <= " + index + " < " + this.datasets.size());
		}
		return this.datasets.get(index);
	}
	
	/**
	 * Returns an array of the dataset names. The names are in the same order as the elements in this list.
	 * @return string array of dataset names
	 */
	public String[] getNames() {
		String[] names = new String[this.size()];
		for(int i = 0; i < this.size(); i++) {
			names[i] = this.datasets.get(i).getName();
		}
		return names;
	}

	/**
	 * Closes the specified dataset and removes it from the list.
	 * @param index index of the dataset to close
	 */
	public void close(int index) {
		if(index < 0 || index >= this.datasets.size()) {
			throw new IndexOutOfBoundsException("index out of bounds: 0 <= " + index + " < " + this.datasets.size());
		}
		this.datasets.get(index).close();
		this.datasets.remove(index);
	}
	
	/**
	 * Saves the specified dataset.
	 * @param index index of the dataset to save
	 */
	public void save(int index) {
		if(index < 0 || index >= this.datasets.size()) {
			throw new IndexOutOfBoundsException("index out of bounds: 0 <= " + index + " < " + this.datasets.size());
		}
		this.datasets.get(index).save();
	}
	
	/**
	 * Opens a {@link gst.ui.dialog.LoadDatasetDialog} and loads the user specified dataset. If it is already loaded
	 * nothing happens. Returns the index of the loaded dataset.
	 * @return index of loaded dataset in this list or -1 if abborted by user
	 */
	public int loadDataset() {
		UnisensDataset newDs = LoadDatasetDialog.getInstance().show();
		if(newDs == null) {
			return -1;
		}
		for(int i = 0; i < this.datasets.size(); i++) {
			if(newDs.getPath().equals(this.datasets.get(i).getPath())) {
				return i;
			}
		}
		this.datasets.add(newDs);
		return this.size() - 1;
	}
	
}