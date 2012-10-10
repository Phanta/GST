/**
 * UnisensDataset.java created on 21.06.2012
 */

package gst.data;

import gst.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.unisens.DataType;
import org.unisens.DuplicateIdException;
import org.unisens.Entry;
import org.unisens.CustomEntry;
import org.unisens.EventEntry;
import org.unisens.MeasurementEntry;
import org.unisens.SignalEntry;
import org.unisens.ValuesEntry;
import org.unisens.Unisens;
import org.unisens.UnisensParseException;
import org.unisens.ri.UnisensImpl;

/**
 * This is a convenience wrapper class for org.unisens.Unisens objects.
 * @author Enrico Grunitz
 * @version 0.1.2.4 (10.10.2012)
 */
public class UnisensDataset {
	/** key for custom attribute: name */			protected static final String KEY_NAME = "DatasetName";
	
	/** number of objects generated */				private static int datasetCount = 0;
	
	/**
	 * Enumeration of different {@code Entry}-types.
	 * @author Enrico Grunitz
	 * @version 1.0.3 (07.08.2012)
	 */
	public enum EntryType {
		/** instanceof SignalEntry */ 					SIGNAL,
		/** instanceof ValueEntry */					VALUE,
		/** instanceof EventEntry */					EVENT,
		/** instanceof CustomEntry */					CUSTOM,
		/** not an instance of the four Entry types*/	UNKNOWN;
		
		@Override
		public String toString() {
			switch(this) {
			case SIGNAL:
				return "Signal";
			case VALUE:
				return "Value";
			case EVENT:
				return "Event";
			case CUSTOM:
				return "Custom";
			case UNKNOWN:
			default:
				return "unknown";
			}
		}
		
		/**
		 * Returns the corresponding EntryType of the given Entry.
		 * @param entry the entry to which the type should be generated
		 * @return type of the given entry
		 */
		public static EntryType getType(Entry entry) {
			if(entry instanceof SignalEntry) {
				return SIGNAL;
			} else if(entry instanceof ValuesEntry) {
				return VALUE;
			} else if(entry instanceof EventEntry) {
				return EVENT;
			} else if(entry instanceof CustomEntry) {
				return CUSTOM;
			} else {
				return UNKNOWN;
			}
		}
	}
	
	/** name of this dataset */						private String name = "";
	/** the unisens object enclosed */				private Unisens us = null;
	/** list of controllers of the data */			private ArrayList<DataController> ctrlList;
	
	/**
	 * Convenience constructor. Constructs the dataset with a relative path to the current working directory.
	 * @param relativPath relative path
	 */
	public UnisensDataset(String relativPath) {
		this(relativPath, false);
	}
	
	
	/**
	 * Constructs the unisens object with the given path.
	 * @param path the path
	 * @param isAbsolutePath true if {@code path} is an absolute path and false if {@code path} is relative to the current working directory
	 */
	public UnisensDataset(String path, boolean isAbsolutePath) {
		String absolutePath = "";
		final String fileSep = System.getProperty("file.separator"); 
		if(isAbsolutePath != true) {
			// generate the absolute path
			absolutePath = System.getProperty("user.dir");	// current directory
			if(!(path.startsWith(fileSep) ^ absolutePath.endsWith(fileSep))) {
				if(path.startsWith(fileSep) && absolutePath.endsWith(fileSep)) {
					// we have one file separator too much
					absolutePath.substring(0, absolutePath.length() - 1);
				} else {
					// there is no file separator
					absolutePath.concat(fileSep);
				}
			}
			// exact one file separator is between absolutePath and (relativ) path
			absolutePath.concat(path);
		} else {
			// given path is absolute
			absolutePath = path;
		}
		try {
			us = new UnisensImpl(absolutePath);
		} catch(UnisensParseException upe) {
			System.out.println("couldn't open or create unisens directory '" + absolutePath + "'");
			upe.printStackTrace();
			us = null;
			return;
		}
		// checking for stored name
		HashMap<String, String> custAttrib = us.getCustomAttributes();
		if(custAttrib.containsKey(KEY_NAME)) {
			this.name = custAttrib.get(KEY_NAME);
		} else {
			name = "UnisensDataset" + datasetCount;
			us.addCustomAttribute(KEY_NAME, name);
			datasetCount++;
		}
		ctrlList = this.createControllers();
		return;
	}
	
	/**
	 * Renames the dataset. null and empty Strings are ignored.
	 * @param name new name
	 */
	public void setName(String name) {
		if(name != null && !name.isEmpty()) {
			this.name = name;
			us.addCustomAttribute(KEY_NAME, this.name);
		}
		return;
	}
	
	/**
	 * Returns the name of the dataset.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the comment for this dataset.
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		us.setComment(comment);
		return;
	}
	
	/**
	 * Returns the comment for this dataset.
	 * @return the comment
	 */
	public String getComment() {
		return us.getComment();
	}
	
	/**
	 * Returns the file-path of the stored dataset.
	 * @return path of dataset
	 */
	public String getPath() {
		return this.us.getPath();
	}
	
	/**
	 * Returns the data of this dataset.
	 * @return a {@link java.util.List List} of the data-IDs
	 */
	@Deprecated
	public List<String> getDataIds() {
		List<Entry> entries = us.getEntries();
		List<String> ids = new ArrayList<String>(entries.size());
		Iterator<Entry> it = entries.iterator();
		while(it.hasNext()) {
			ids.add(it.next().getId());
		}
		return ids;
	}
	
	/**
	 * Returns the Entry with the given ID.
	 * @param entryId the ID of the requested entry
	 * @return the entry object
	 */
	@Deprecated
	public Entry getEntry(String entryId) {
		if(entryId == null) {
			throw new NullPointerException("ID of a dataset entry cannot be null");
		}
		return us.getEntry(entryId);
	}
	
	/**
	 * Returns a {@link java.util.List List} of content class strings of the entries of this dataset.
	 * @return {@code List<String>} of classes of the entries
	 */
	@Deprecated
	public List<String> getContentClasses() {
		List<Entry> entries = us.getEntries();
		List<String> classes = new ArrayList<String>(entries.size());
		Iterator<Entry> it = entries.iterator();
		while(it.hasNext()) {
			classes.add(it.next().getContentClass());
		}
		return classes;
	}
	
	/**
	 * Returns the {@link java.util.List List} of entry types of this dataset.
	 * @return list of EntryTypes
	 */
	public List<EntryType> getEntryTypes() {
		List<Entry> entries = us.getEntries();
		List<EntryType> types = new ArrayList<EntryType>(entries.size());
		Iterator<Entry> it = entries.iterator();
		while(it.hasNext()) {
			types.add(EntryType.getType(it.next()));
		}
		return types;
	}
	
	/**
	 * Creates a new {@link gst.data.AnnotationController} and an {@code org.unisens.Entry} for the given file name.
	 * @param fileName the file name of the new controller
	 * @return {@code AnnotationController} for the newly created {@code Entry}
	 */
	public AnnotationController createAnnotation(String fileName) {
		EventEntry newEntry = null;
		try {
			newEntry = us.createEventEntry(fileName, 1000.0);
		} catch(DuplicateIdException die) {
			System.out.println("ERROR\tcouldn't create new EventEntry - DuplicatedId");
			return null;
		}
		if(newEntry == null) {
			System.out.println("ERROR\tcouldn't create new EventEntry");
			return null;
		}
		newEntry.setSource(Settings.getInstance().getAppName());
		newEntry.setSourceId(Settings.getInstance().getAppId());
		AnnotationController ctrl = new AnnotationController(newEntry);
		ctrl.createFile();
		this.ctrlList.add(ctrl);
		return ctrl;
	}
	
	
	/**
	 * Creates a {@link gst.data.BufferedValueController} for a new {@code org.unisens.ValuesEntry} of this dataset.
	 * @param fileName file name of the entry
	 * @return the {@code BufferedValueController} of the newly created dataset entry
	 */
	public BufferedValueController createValues(String fileName) {
		ValuesEntry newEntry = null;
		try {
			newEntry = us.createValuesEntry(fileName, new String[]{"ch1"}, DataType.DOUBLE, 1000.0);
		} catch(DuplicateIdException die) {
			System.out.println("ERROR\tcouldn't create new ValuesEntry - DuplicatedId '" + fileName + "'");
			return null;
		}
		if(newEntry == null) {
			System.out.println("ERROR\tcreated new null ValuesEntry '" + fileName + "'");
			return null;
		}
		newEntry.setSource(Settings.getInstance().getAppName());
		newEntry.setSourceId(Settings.getInstance().getAppId());
		BufferedValueController ctrl = new BufferedValueController(newEntry);
		ctrl.createFile();
		this.ctrlList.add(ctrl);
		return ctrl;
	}

	/**
	 * Creates {@link gst.data.DataController}s for all data in this {@code UnisensDataset}.
	 * @return list of created {@code DataController}
	 */
	private ArrayList<DataController> createControllers() {
		List<Entry> entryList = this.us.getEntries();
		ArrayList<DataController> ctrlList = new ArrayList<DataController>();
		Iterator<Entry> it = entryList.iterator();
		int numChannels;
		Entry curEntry;
		EntryType curType;
		DataController curCtrl;
		while(it.hasNext()) {
			curEntry = it.next();
			numChannels = getChannelCount(curEntry);
			curType = EntryType.getType(curEntry);
			for(int i = 0; i < numChannels; i++) {
				switch(curType) {
				case SIGNAL:
					curCtrl = new SignalController((SignalEntry)curEntry);
					((SignalController)curCtrl).setChannelToControl(i);
					ctrlList.add(curCtrl);
					break;
				case VALUE:
					curCtrl = new ValueController((ValuesEntry)curEntry);
					((ValueController)curCtrl).setChannelToControl(i);
					ctrlList.add(curCtrl);
					break;
				case EVENT:
					curCtrl = new AnnotationController((EventEntry)curEntry);
					ctrlList.add(curCtrl);
					break;
				default:
					System.out.println("ERROR\tcannot create controller for entry-type '" + curType.toString() + "'");
				}
			}
		}
		return ctrlList;
	}
	
	/**
	 * Returns a {@code List} with a {@link gst.data.DataController} for each entry and channel.
	 * @return controller list
	 */
	public List<DataController> getControllerList() {
		return this.ctrlList;
	}
	
	/**
	 * Saves the dataset.
	 * @return true if successful, else false
	 */
	public boolean save() {
		// save all buffered data
		Iterator<DataController> it = this.ctrlList.iterator();
		while(it.hasNext()) {
			it.next().save();
		}
		// save unisens object
		try {
			us.save();
		} catch(IOException ioe) {
			System.out.println("couldn't save unisens dataset '" + name + "'");
			ioe.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Closes all entries of the unisens object.
	 */
	public void close() {
		us.closeAll();
		return;
	}
	
	/**
	 * Returns the number of channels of the given entry. Custom and unknown entries return always 0.
	 * @param entry the entry
	 * @return number of channels
	 */
	private static int getChannelCount(Entry entry) {
		switch(EntryType.getType(entry)) {
		case SIGNAL:
		case VALUE:
			return ((MeasurementEntry)entry).getChannelCount();
		case EVENT:
			return 1;
		case CUSTOM:
		case UNKNOWN:
			return 0;
		default:
			System.out.println("ERROR\tentry of unhandled type detected in UnisensDataset.getChannelCount(Entry)");
			return 0;
		}
	}

}
