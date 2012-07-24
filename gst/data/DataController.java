/**
 * DataController.java created on 17.07.2012
 */

package gst.data;

import gst.data.UnisensDataset.EntryType;

import org.jfree.data.xy.XYSeries;
import org.unisens.Entry;

/**
 * Controller class for managing data access for SignalViews.
 * @author Enrico Grunitz
 * @version 0.1 (18.07.2012)
 */
public abstract class DataController {
	/** the entry of this controller */				protected Entry entry;
	/** type of the controlled entry */				protected EntryType type;
	/** measurement time of the first entry */		protected double basetime;
	/** number of channels of the entry */			protected int channelCount;
	
	/**
	 * Constructs the controller object for an entry with the given ID of the dataset.
	 * @param ds dataset which contains the entry
	 * @param entryId ID of the entry inside the dataset
	 */
	protected DataController(UnisensDataset ds, String entryId) {
		if(ds == null) {
			throw new NullPointerException();
		}
		this.entry = ds.getEntry(entryId);
		if(this.entry == null) {
			throw new IllegalArgumentException();
		}
		this.type = EntryType.getType(this.entry);
		basetime = 0.0;
		channelCount = 1;
		return;
	}
	
	/**
	 * Constructs the controller object for the given entry.
	 * @param entry the entry (not null)
	 */
	protected DataController(Entry entry) {
		if(entry == null) {
			throw new NullPointerException();
		}
		this.entry = entry;
		this.type = EntryType.getType(this.entry);
		basetime = 0.0;
		channelCount = 1;
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
}
