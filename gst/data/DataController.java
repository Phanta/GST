/**
 * DataController.java created on 17.07.2012
 */

package gst.data;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import gst.data.UnisensDataset.EntryType;
import gst.test.Debug;

import org.jfree.data.xy.XYSeries;
import org.unisens.Entry;
import org.unisens.TimedEntry;

/**
 * Controller class for managing data access for SignalViews.
 * @author Enrico Grunitz
 * @version 0.1.5.6 (16.10.2012)
 */
public abstract class DataController {
	/** seperator used for full names */			public static final String SEPERATOR = " -> "; 
	
	/** the entry of this controller */				protected Entry entry;
	/** type of the controlled entry */				protected EntryType type;
	/** measurement time of the first entry */		protected double basetime;
	/** number of channels of the entry */			protected int channelCount;
	/** true if the controller is buffered */		protected boolean isBuffered;
	/** preferred color for this data */			private Color prefColor;
	/** list of registered {@link gst.data.DataChangeListener} */
													private ArrayList<DataChangeListener> listeners;
	
	/**
	 * Constructs the controller object for an entry with the given ID of the dataset.
	 * @param ds dataset which contains the entry
	 * @param entryId ID of the entry inside the dataset
	 */
	@Deprecated
	protected DataController(UnisensDataset ds, String entryId) {
		this(ds.getEntry(entryId));
		return;
	}
	
	/**
	 * Constructs the controller object for the given entry.
	 * @param entry the entry (not null)
	 */
	protected DataController(Entry entry) {
		if(entry == null) {
			throw new NullPointerException("constructor from null Entry failed");
		}
		this.entry = entry;
		this.type = EntryType.getType(this.entry);
		this.basetime = 0.0;
		this.channelCount = 1;
		this.isBuffered = false;
		this.prefColor = Color.red;
		this.listeners = new ArrayList<DataChangeListener>();
		return;
	}
	
	/**
	 * Returns the number of channels of the controlled data.
	 * @return number of channels
	 */
	public int getNumChannels() {
		return channelCount;
	}
	
	/**
	 * Returns true if the controlled entry is annotation like.
	 * @return true if the data is annotation like, else false 
	 */
	public boolean isAnnotation() {
		if(type.equals(EntryType.EVENT)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns true if this entry contains read-only data.
	 * @return true if date entry is read-only
	 */
	public boolean isReadOnly() {
		return !this.getFile().canWrite();
	}
	
	/**
	 * Create the file if it not exists.
	 */
	public void createFile() {
		File dummyFile = this.getFile();
		Debug.println(Debug.controller, "creating dummy file " + dummyFile.getAbsolutePath());
		if(!dummyFile.exists()) {
			try {
				dummyFile.createNewFile();
				dummyFile.setWritable(true);
			} catch(IOException ioe) {
				Debug.println(Debug.controller, "cannot create new file '" + dummyFile.getAbsolutePath() + "'");
			}
		} else {
			Debug.println(Debug.controller, "file '" + dummyFile.getAbsolutePath() + "' already exists");
		}
	}
	
	/**
	 * Checks existence of a file for the encapsuled entry.
	 * @return true if this's entry has an existing file
	 */
	protected boolean fileExists() {
		return this.getFile().exists();
	}
	
	/**
	 * Returns the file of this entry.
	 * @return the file of this data entry
	 */
	protected File getFile() {
		File unisensFile = new File(this.entry.getUnisens().getPath());
		// remove unisens file to get the directory
		if(unisensFile.isFile()) {
			unisensFile = unisensFile.getParentFile();
		}
		if(unisensFile == null) {
			throw new NullPointerException("error while creating file for entry " + this.entry.getId());
		}
		// create file of entry
		return new File(unisensFile, this.entry.getId());
	}
	
	/**
	 * Saves the data entry to disc. Calls the saveImpl() method for the entry.
	 */
	public void save() {
		if(this.isBuffered && !this.isReadOnly()) {
			this.saveImpl();
		}
	}
	
	/**
	 * @return name of the controlled entry
	 */
	public String getEntryId() {
		return entry.getId();
	}
	
	/**
	 * Generates the full name of this entry. Has to be overwritten by subclasses that handle channels. Generated as Unisens +
	 * {@link #SEPERATOR} + EntryId (+ {@link #SEPERATOR} + Channel). 
	 * @return the full name of this entry 
	 */
	public String getFullName() {
		return this.entry.getUnisens().getMeasurementId() + SEPERATOR + this.entry.getId();
	}
	
	/**
	 * Returns the time corresponding to the given SampleStamp. Throws a {@code UnsupportedOperationException} if the entry
	 * is not an instance of {@code org.unisens.TimedEntry}.
	 * @param sampleStamp the sampleStamp to convert.
	 * @return the time in seconds of this sample stamp
	 */
	protected double timeOf(long sampleStamp) {
		if(this.entry instanceof TimedEntry) {
			return this.basetime + (sampleStamp / ((TimedEntry)this.entry).getSampleRate());
		} else {
			throw new UnsupportedOperationException("time conversion only works for TimedEntries");
		}
	}
	
	/**
	 * Returns the highest samplestamp that is lower or equal the given point in time. Only works for entries of
	 * {@code org.unisens.TimedEntry}.
	 * @param time point in time to convert
	 * @return samplestamp lower or equal the given time
	 */
	protected long lowSampleStamp(double time) {
		if(this.entry instanceof TimedEntry) {
			return (long)Math.floor((time - this.basetime) * ((TimedEntry)this.entry).getSampleRate());
		} else {
			throw new UnsupportedOperationException("time conversion only works for TimedEntries");
		}
	}
	
	/**
	 * Returns the closest sample-stamp to the given point in time. Only works for entries of {@code org.unisens.TimedEntry}.
	 * @param time to convert into sample-stamp
	 * @return the sample-stamp closest to the given point in time
	 */
	protected long roundSampleStamp(double time) {
		if(this.entry instanceof TimedEntry) {
			return (long)Math.round((time - this.basetime) * ((TimedEntry)this.entry).getSampleRate());
		} else {
			throw new UnsupportedOperationException("time conversion only works for TimedEntries");
		}
	}

	/**
	 * Returns the lowest samplestamp that is higher or equal the given point in time. Only works for entries of
	 * {@code org.unisens.TimedEntry}.
	 * @param time point in time to convert
	 * @return samplestamp higher or equal the given time
	 */
	protected long highSampleStamp(double time) {
		if(this.entry instanceof TimedEntry) {
			return (long)Math.ceil((time - this.basetime) * ((TimedEntry)this.entry).getSampleRate());
		} else {
			throw new UnsupportedOperationException("time conversion only works for TimedEntries");
		}
	}

	/**
	 * Returns the name of the channel of the controllers entry. Returns {@code null} if the channel is not named.
	 * @return channel name
	 */
	abstract public String getChannelName();
	
	/** @return {@link #prefColor} */
	public Color getPreferredColor() {
		return this.prefColor;
	}
	
	/**
	 * Sets the {@code source} and {@code sourceId} fields of the controlled dataset entry to the given {@code String}s.
	 * @param source the source {@code String}
	 * @param sourceId the sourceId {@code String}
	 */
	public void setSource(String source, String sourceId) {
		this.entry.setSource(source);
		this.entry.setSourceId(sourceId);
	}
	
	/**
	 * Sets the preferred color for this data.
	 * @param color the new color
	 * @throws {@code NullPointerException} if {@code color} is null
	 */
	public void setPreferredColor(Color color) {
		if(color == null) {
			throw new NullPointerException("null preferred color not possible");
		}
		this.prefColor = color;
	}
	
	/**
	 * Notifies all registered Listeners of changed data. Has to be called by subclasses.
	 * @param event the event to notify the listeners off
	 */
	protected void notifyListeners(DataChangeEvent event) {
		Iterator<DataChangeListener> it = this.listeners.iterator();
		while(it.hasNext()) {
			it.next().dataChangeReaction(event);
		}
		return;
	}
	
	/**
	 * Registers the given {@link gst.data.DataChangeListener} to this controller.
	 * @param dcl the {@code DataChangeListener} to notify about changes
	 */
	public void register(DataChangeListener dcl) {
		if(!this.listeners.contains(dcl)) {
			this.listeners.add(dcl);
		}
		return;
	}
	
	/**
	 * Removes the {@link DataChangeListener} from the notification list.
	 * @param dcl {@code DataChangeListener} to remove
	 */
	public void remove(DataChangeListener dcl) {
		this.listeners.remove(dcl);
		return;
	}
	
	/**
	 * Returns a {@code List} of the annotations of controlled {@code Entry}. All annotations are between {@code startTime} and
	 * {@code endTime}. If the entry doesn't has annotations null is returned.
	 * @param startTime beginning time
	 * @param endTime ending time
	 * @return list of annotations or null
	 */
	abstract public AnnotationList getAnnotations(double startTime, double endTime);
	/**
	 * Returns the data points as an {@code XYSeries}. All data points are between {@code startTime} and {@code endTime}.
	 * @param startTime beginning time
	 * @param endTime ending time
	 * @param maxPoints number of maximum data points
	 * @return the array of data points or null if there are only annotations
	 */
	abstract public XYSeries getDataPoints(double startTime, double endTime, int maxPoints);
	/**
	 * Gives the time in seconds of the last measured data point.
	 * @return time in seconds
	 */
	abstract public double getMaxX();
	/**
	 * Gives the time in seconds of the first measured data point.
	 * @return time in seconds
	 */
	abstract public double getMinX();
	/**
	 * Returns the string that specifies the physical unit of the acquired variables.
	 * @return physical unit as {@code String}
	 */
	abstract public String getPhysicalUnit();
	/**
	 * Sets the physical unit specifying string. Ignored if not applicable.
	 */
	abstract public void setPhysicalUnit(String physUnit);
	/**
	 * This function saves the data to its corresponding file. Called if {@link #isBuffered} is set to true.
	 */
	abstract protected void saveImpl();
}
