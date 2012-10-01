/**
 * DataController.java created on 17.07.2012
 */

package gst.data;

import java.awt.Color;
import java.io.File;

import gst.data.UnisensDataset.EntryType;

import org.jfree.data.xy.XYSeries;
import org.unisens.Entry;

/**
 * Controller class for managing data access for SignalViews.
 * @author Enrico Grunitz
 * @version 0.1.4.1 (01.10.2012)
 */
public abstract class DataController {
	/** seperator used for full names */			public static final String SEPERATOR = " -> "; 
	
	/** the entry of this controller */				protected Entry entry;
	/** type of the controlled entry */				protected EntryType type;
	/** measurement time of the first entry */		protected double basetime;
	/** number of channels of the entry */			protected int channelCount;
	/** true if the controller is buffered */		protected boolean isBuffered;
	/** preferred color for this data */			private Color prefColor;
	
	/**
	 * Constructs the controller object for an entry with the given ID of the dataset.
	 * @param ds dataset which contains the entry
	 * @param entryId ID of the entry inside the dataset
	 */
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
		String path = this.entry.getUnisens().getPath();
		if(!(path.endsWith("\\") || path.endsWith("/"))) {
			path += "/";
		}
		File file = new File(path + this.getEntryId());
		if(file.canWrite()) {
			return false;
		} else { 
			return true;
		}
	}
	
	/**
	 * Saves the data entry to disc. Calls the saveImpl() method for the entry.
	 */
	public void save() {
		if(this.isBuffered) {
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
	
	/** @return {@link #prefColor} */
	public Color getPreferredColor() {
		return this.prefColor;
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
	 * This function saves the data to its corresponding file. Called if {@link #isBuffered} is set to true.
	 */
	abstract protected void saveImpl();
}
