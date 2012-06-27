/**
 * UnisensDataset.java created on 21.06.2012
 */

package gst.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.unisens.Entry;
import org.unisens.CustomEntry;
import org.unisens.EventEntry;
import org.unisens.SignalEntry;
import org.unisens.ValuesEntry;
import org.unisens.Unisens;
import org.unisens.UnisensParseException;
import org.unisens.ri.UnisensImpl;

/**
 * This is a convenience wrapper class for org.unisens.Unisens objects.
 * @author Enrico Grunitz
 * @version 0.1 (27.06.2012)
 */
public class UnisensDataset {
	/** key for custom attribute: name */			protected static final String KEY_NAME = "DatasetName";
	
	/** number of objects generated */				private static int datasetCount = 0;
	
	/**
	 * Enumeration of different {@code Entry}-types.
	 * @author Enrico Grunitz
	 * @version 1.0 (27.06.2012)
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
		name = "UnisensDataset" + datasetCount;
		us.addCustomAttribute(KEY_NAME, name);
		datasetCount++;
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
	 * Returns the data of this dataset.
	 * @return a {@link java.util.List List} of the data-IDs
	 */
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
	 * Returns the Entry with the geven ID.
	 * @param entryId the ID of the requested entry
	 * @return the entry object
	 */
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
	 * Saves the dataset.
	 * @return true if successful, else false
	 */
	public boolean save() {
		try {
			us.save();
		} catch(FileNotFoundException fnfe) {
			// TODO this block doesn't catch anything (catched by UnisensImpl)
			System.out.println("Couldn't save unisens dataset '" + name + "'. File not found or access denied!");
			return false;
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
	
}
