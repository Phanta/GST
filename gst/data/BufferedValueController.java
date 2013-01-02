/**
 * BufferedValueController.java created on 08.10.2012
 */

package gst.data;

import gst.test.Debug;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;
import org.unisens.DataType;
import org.unisens.Value;
import org.unisens.ValuesEntry;

/**
 * Buffered version of a {@link gst.data.ValueController} which supports reading and writing of data. Only double-type data and
 * one single channel are supported.
 * @author Enrico Grunitz
 * @version 0.0.1.1 (16.10.2012)
 */
public class BufferedValueController extends ValueController {
	/** buffer of the values of the controlled channel and entry */		private ArrayList<DataPoint<Double>> buffer;
	/** last accessed index */											private int lastAccessedIndex = 0;
	/** event for adding data */										private DataChangeEvent addEvent;
	/** event for changeing data values */								private DataChangeEvent valueEvent;
	/** event for removing data */										private DataChangeEvent removeEvent;
	
	/**
	 * Constructor.
	 * @param entry the dataset entry to control
	 */
	public BufferedValueController(ValuesEntry entry) {
		super(entry);
		this.isBuffered = true;
		this.setChannelToControl(0);
		this.addEvent = new DataChangeEvent(this, DataChangeEvent.Type.ADDED);
		this.valueEvent = new DataChangeEvent(this, DataChangeEvent.Type.VALUE_CHANGED);
		this.removeEvent = new DataChangeEvent(this, DataChangeEvent.Type.REMOVED);
		this.initAndFillBuffer();
	}
	
	/**
	 * Inserts a new data point into the buffer and may notify registered {@link gst.data.DataChangeListener}s.
	 * @param time point in time of the data point
	 * @param value value of the data point
	 * @param notify if true all {@code DataChangeListener}s are notified
	 */
	public void addDataPoint(double time, double value, boolean notify) {
		this.addEvent.addDataPoint(time, value);
		this.insert(new DataPoint<Double>(this.roundSampleStamp(time), value));
		if(notify == true) {
			this.notifyListeners(this.addEvent);
			this.addEvent = new DataChangeEvent(this, DataChangeEvent.Type.ADDED);
		}
		return;
	}
	
	/**
	 * Inserts a new data point into the buffer and notifies the listeners. Convenience method to always notify listeners.
	 * @param time point in time of the data point
	 * @param value value of the data point
	 */
	public void addDataPoint(double time, double value) {
		this.addDataPoint(time, value, true);
		return;
	}
	
	/**
	 * Changes the value of the data point with the time {@code time} and its value {@code oldValue} to {@code newValue}.
	 * @param time point in time of the data point
	 * @param oldValue value of the data point
	 * @param newValue new value for the data point
	 * @param notify if {@code true} all registered {@link gst.data.DataChangeListener}s are notified about data change
	 * @return {@code true} if modify was success else {@code false}
	 */
	public boolean modifyDataPoint(double time, double oldValue, double newValue, boolean notify) {
		if(this.buffer.isEmpty()) {
			return false;
		}
		int index = this.seek(new DataPoint<Double>(this.roundSampleStamp(time), oldValue));
		if(index == -1) {
			return false;
		}
		this.valueEvent.addDataPoint(time, newValue, oldValue);
		this.buffer.get(index).setData(newValue);
		if(notify == true) {
			this.notifyListeners(this.valueEvent);
			this.valueEvent = new DataChangeEvent(this, DataChangeEvent.Type.VALUE_CHANGED);
		}
		return true;
	}
	
	/**
	 * Convenience method for {@link #modifyDataPoint(double, double, double, boolean) modifyDataPoint(time, oldValue, newValue, notify = true)}.<br>
	 * Changes the value of the data point with the time {@code time} and its value {@code oldValue} to {@code newValue}. All
	 * registered  {@link gst.data.DataChangeListener}s are notified about data change.
	 * @param time point in time of the data point
	 * @param oldValue value of the data point
	 * @param newValue new value for the data point
	 * @return {@code true} if modify was success else {@code false}
	 */
	public boolean modifyDataPoint(double time, double oldValue, double newValue) {
		return this.modifyDataPoint(time, oldValue, newValue, true);
	}
	
	/**
	 * Removes all data-points from the controller. Seriously there will be no confirmation dialog, no "Are you sure? Yes. No.
	 * Maybe."-questioning. So be absolutely, uber extremely sure you don't need the data anymore. And never ever say I didn't
	 * warn you about this behavior.
	 */
	public void clearDataPoints() {
		this.buffer.clear();
		this.updateLastAccess(0);
	}
	
	/**
	 * Removes the data point of the given time and value from the buffer. If removal is successful true is returned, otherwise
	 * false.
	 * @param time point in time of the data point
	 * @param value value of the data point
	 * @param notify if true all registered {@link gst.data.DataChangeListener}s are notified of data removal
	 * @return true if removal was successful
	 */
	public boolean removeDataPoint(double time, double value, boolean notify) {
		if(this.buffer.isEmpty()) {
			return false;
		}
		DataPoint<Double> dp = new DataPoint<Double>(this.roundSampleStamp(time), value);
		int index = this.seek(dp);
		if(index == -1) {
			// no element in buffer with matching sample-stamp
			return false;
		} else {
			this.removeEvent.addDataPoint(time, value);
			this.buffer.remove(index);
			this.updateLastAccess(index - 1);
			if(notify == true) {
				this.notifyListeners(this.removeEvent);
				this.removeEvent = new DataChangeEvent(this, DataChangeEvent.Type.REMOVED);
			}
			return true;
		}
	}
	
	/**
	 * Convenience method for {@link #removeDataPoint(double, double, boolean)} with notification set to {@code true}.<br>
	 * Removes the data point of the given time and value from the buffer. If removal is successful true is returned, otherwise
	 * false. All register {@link gst.data.DataChangeListener}s are notified upon data removal.
	 * @param time point in time of the data point
	 * @param value value of the data point
	 * @return true if removal was successful
	 */
	public boolean removeDataPoint(double time, double value) {
		return this.removeDataPoint(time, value, true);
	}
	
	/**
	 * Removes all data at the given point in time from the buffer. The number of removed elements is returned. If {@code notify}
	 * is {@code true} all registered {@code gst.data.DataChangeListener} are notified of the changed data.
	 * @param time point in time to remove all data points from
	 * @param notify if {@code true} all {@code DataChangeListener}s are notified of changed data
	 * @return number of removed data points
	 */
	public int removeDataAt(double time, boolean notify) {
		long sampleStamp = this.roundSampleStamp(time);
		int index = this.seek(sampleStamp);
		int counter = 0;
		while(index != -1) {
			this.removeEvent.addDataPoint(time, this.buffer.get(index).getData());
			this.buffer.remove(index);
			counter++;
			index = this.seek(sampleStamp);
		}
		if(counter > 0 && notify == true) {
			this.notifyListeners(this.removeEvent);
			this.removeEvent = new DataChangeEvent(this, DataChangeEvent.Type.REMOVED);
		}
		return counter;
	}
	
	/**
	 * Convenience method for {@link #removeDataAt(double, boolean) removeDataAt(double time, boolean notify = true)}.<br> 
	 * Removes all data at the given point in time from the buffer. The number of removed elements is returned. All registered
	 * {@code gst.data.DataChangeListener} are notified of the changed data.
	 * @param time point in time to remove all data points from
	 * @return number of removed data points
	 */
	public int removeDataAt(double time) {
		return this.removeDataAt(time, true);
	}
		
	/** @see gst.data.ValueController#getDataPoints(double, double, int) */
	@Override public XYSeries getDataPoints(double startTime, double endTime, int maxPoints) {
		XYSeries series = new XYSeries(this.getFullName());
		if(this.buffer.isEmpty()) {
			return series;
		}
		int startIndex = this.seekLower(this.lowSampleStamp(startTime));
		if(startIndex < 0) {
			startIndex = 0;
		}
		int endIndex = this.seekHigher(this.highSampleStamp(endTime));
		if(endIndex > this.buffer.size()) {
			endIndex = this.buffer.size();
		}
		for(int i = startIndex; i < endIndex; i++) {
			series.add(this.timeOf(this.buffer.get(i).getSampleStamp()), this.buffer.get(i).getData());
		}
		return series;
	}
	
	/** @see gst.data.DataController#getMaxX() */
	@Override public double getMaxX() {
		if(this.buffer.size() < 1) {
			return 0;
		} else {
			return this.timeOf(this.buffer.get(this.buffer.size() - 1).getSampleStamp());
		}
	}

	/** @see gst.data.DataController#getMinX() */
	@Override public double getMinX() {
		if(this.buffer.size() < 1) {
			return 0;
		} else {
			return this.timeOf(this.buffer.get(0).getSampleStamp());
		}
	}
	
	/** @see gst.data.DataController#saveImpl() */
	@Override public void saveImpl() {
		File file = this.getFile();
		File backup = new File(file.getPath().concat(".bak"));
		if(this.fileExists()) {
			// have to delete file, 'cause we only can append data
			if(!file.canWrite()) {
				Debug.println(Debug.bufferedValueController, "Cannot save " + file.toString() + ". Lacking write permission!");
				return;
			}
			this.entry.close();		// closing file or delete/rename will fail
			if(!file.renameTo(backup)) {
				Debug.println(Debug.bufferedValueController, "Cannot save " + file.toString() + ". Cannot create backup file!");
				return;
			}
		}
		// create file and write data
		this.createFile();
		try {
			((ValuesEntry)this.entry).append(this.valueArrayFromBuffer());
		} catch(IOException ioe) {
			Debug.println(Debug.bufferedValueController, "Cannot save " + file.toString() + ". Error while writing data!");
			return;
		}
		// delete backup
		if(!backup.delete()) {
			Debug.println(Debug.bufferedValueController, "Cannot delete backup-file " + backup.toString() + ".");
			return;
		}
		Debug.println(Debug.bufferedValueController, "Saveing " + file.toString() + " successful.");
		return;
	}
	
	/**
	 * Inserts the given {@link gst.data.BufferedValueController.DataPoint} into the buffer below the element that has a
	 * higher samplestamp than the given point. Also updates the {@link #lastAccessedIndex} accordingly.
	 * @param dp the {@code DataPoint to insert}
	 */
	private void insert(DataPoint<Double> dp) {
		if(dp == null) {
			throw new NullPointerException("cannot insert null into buffer");
		}
		int insertIndex;
		if(this.buffer.isEmpty()) {
			insertIndex = 0;
		} else {
			insertIndex = this.seekHigher(dp.getSampleStamp());
		}
		this.buffer.add(insertIndex, dp);
		this.updateLastAccess(insertIndex);
		return;
	}
	
	/**
	 * Reads all data from file and fills the buffer. Notifies Listeners at the end.
	 */
	private void initAndFillBuffer() {
		if(!(((ValuesEntry)this.entry).getDataType() == DataType.DOUBLE)) {
			throw new UnsupportedOperationException("cannot handle buffer values of non double types");
		}
		if(((ValuesEntry)this.entry).getChannelCount() != 1) {
			throw new UnsupportedOperationException("cannot handle buffered values of more than one channel");
		}
		Value[] values;
		try {
			values = ((ValuesEntry)this.entry).readScaled(0, (int)((ValuesEntry)this.entry).getCount());
		} catch(IOException ioe) {
			Debug.println(Debug.bufferedValueController, "failed to read file " + this.entry.getId());
			return;
		}
		this.buffer = new ArrayList<DataPoint<Double>>(values.length);
		for(int i = 0; i < values.length; i++) {
			this.insert(new DataPoint<Double>(values[i].getSampleStamp(), ((double[])values[i].getData())[0]));
			this.addEvent.addDataPoint(this.timeOf(values[i].getSampleStamp()), ((double[])values[i].getData())[0]);
		}
		if(values.length > 0) {
			this.notifyListeners(this.addEvent);
			this.addEvent = new DataChangeEvent(this, DataChangeEvent.Type.ADDED);
		}
	}
	
	/**
	 * Searches for the index of the buffer-element which sample-stamp is lower than the given sample-stamp. Starting search
	 * from {@link #lastAccessedIndex}.
	 * @param sampleStamp
	 * @return index of the element which sample-stamp is the highest below the given, -1 if there is no lower sample-stamp
	 */
	private int seekLower(long sampleStamp) {
		if(sampleStamp <= this.buffer.get(this.lastAccessedIndex).getSampleStamp()) {
			// ss >= lastSS -> seeking backward
			for(int i = this.lastAccessedIndex; i >= 0; i--) {
				if(i == 0) { // reached beginning of buffer
					if(this.buffer.get(i).getSampleStamp() >= sampleStamp) {
						return -1;
					} else {
						return 0;
					}
				}
				if(sampleStamp > this.buffer.get(i).getSampleStamp()) {
					return i;
				}
			}
		} else {
			// ss < lastSS -> seeking forward
			for(int i = this.lastAccessedIndex; i < this.buffer.size(); i++) {
				if(i == this.buffer.size() - 1) {	// reached end of buffer
					if(sampleStamp <= this.buffer.get(i).getSampleStamp()) {
						return i - 1;
					} else {
						return i;
					}
				}
				if(sampleStamp <= this.buffer.get(i).getSampleStamp()) {
					return i - 1;
				}
			}
		}
		Debug.println(Debug.bufferedValueController, "BufferedValueController.seekLower() reached unreachable code");
		return -1;
	}
	
	/**
	 * Seeks the first element in the buffer, that has the given sample-stamp.
	 * @param sampleStamp the sample-stamp to look for
	 * @return index of the first element with the given sample-stamp or -1 if there is no such element in the buffer
	 */
	private int seek(long sampleStamp) {
		if(this.buffer.size() == 0) {
			return -1;
		}
		int index = this.seekLower(sampleStamp) + 1;
		if(index < this.buffer.size()) {
			if(this.buffer.get(index).getSampleStamp() == sampleStamp) {
				return index;
			} else {
				// must be bigger than the sample-stamp, but let's double check
				if(!(this.buffer.get(index).getSampleStamp() > sampleStamp)) {
					Debug.println(Debug.bufferedValueController, "BufferedValueController.seekLower(long) failed");
				}
				return -1;
			}
		} else {
			return -1;
		}
	}
	
	/**
	 * Searches the buffer for a {@link gst.data.BufferedValueController.DataPoint} which equals the given.
	 * @param dp the {@code DataPoint} to look for
	 * @return the index of the first matching {@code DataPoint} or -1 if there is no such point
	 */
	private int seek(DataPoint<Double> dp) {
		int iStart = this.seek(dp.getSampleStamp());
		if(iStart == -1) {
			return -1;
		}
		for(int i = iStart; i < this.buffer.size(); i++) {
			if(this.buffer.get(i).getSampleStamp() > dp.getSampleStamp()) {
				return -1;
			}
			if(this.buffer.get(i).equals(dp)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the index of the buffer-element which has the lowest sample-stamp above the given.
	 * @param sampleStamp the sample-stamp to seek for
	 * @return index of the element with the smallest sampleStamp above the given, {@code buffer.size()} if there is no bigger
	 * 		   element
	 */
	private int seekHigher(long sampleStamp) {
		int lowerIndex = this.seekLower(sampleStamp);
		for(int i = lowerIndex + 1; i < this.buffer.size(); i++) {
			// skip all elements that have the same sample-stamp as the current one
			if(sampleStamp < this.buffer.get(i).getSampleStamp()) {
				return i;
			}
		}
		return this.buffer.size();
	}
	
	/**
	 * Updates {@link #lastAccessedIndex} to the given value, but checks size of the buffer. If {@code index} is lower 0 it is
	 * set to 0. If it is higher or equal {@code buffer.size()} it is set to {@code buffer.size() - 1}. If {@code buffer.size()}
	 * is 0 it is also set to 0.
	 * @param index the index to set to
	 */
	private void updateLastAccess(int index) {
		if(index < 0 || this.buffer.isEmpty()) {
			this.lastAccessedIndex = 0;
		} else if(index >= this.buffer.size()) {
			this.lastAccessedIndex = this.buffer.size() - 1;
		} else {
			this.lastAccessedIndex = index;
		}
	}
	
	/**
	 * Creates a new array of {@code org.unisens.Value} from data-points in the buffer.
	 * @return array of {@code Value}s of the buffers data 
	 */
	private Value[] valueArrayFromBuffer() {
		Value[] values = new Value[this.buffer.size()];
		for(int i = 0; i < this.buffer.size(); i++) {
			values[i] = this.buffer.get(i).toValue();
		}
		return values;
	}
	
	/**
	 * Wrapper for a generic data point. Every data point has a samplestamp ({@code long}) and value.
	 * @author Enrico Grunitz
	 * @version 0.0.0.2 (12.10.2012)
	 * @param <T> data type of data points value
	 */
	private class DataPoint<T> {
		/** samplestamp */			private long samplestamp;
		/** data points value */	private T data;
		
		/**
		 * Constructs a {@code DataPoint} with the given samplestamp and value.
		 * @param sampleStamp samplestamp of the {@code DataPoint}
		 * @param value value of th {@code DataPoint}
		 */
		public DataPoint(long sampleStamp, T value) {
			this.samplestamp = sampleStamp;
			this.data = value;
		}
		
		/**
		 * @return samplestamp of the {@code DataPoint}
		 */
		public long getSampleStamp() {
			return this.samplestamp;
		}
		
		/**
		 * @return value of the {@code DataPoint}
		 */
		public T getData() {
			return this.data;
		}
		
		/**
		 * Sets the samplestamp of the {@code DataPoint}.
		 * @param sampleStamp samplestamp to set
		 */
		public void setSampleStamp(long sampleStamp) {
			this.samplestamp = sampleStamp;
		}
		
		/**
		 * Sets the value of the {@code DataPoint}.
		 * @param data value for the {@code DataPoint}
		 */
		public void setData(T data) {
			this.data = data;
		}
		
		/**
		 * Converts the {@code DataPoint} to a {@code org.unisens.Value}.
		 * @return the corresponding {@code org.unisens.Value}
		 */
		public Value toValue() {
			return new Value(this.samplestamp, this.data);
		}
		
		/**
		 * Returns true if the given {@code DataPoint} has the same value and samplestamp.
		 * @param dp the {@code DataPoint} to compare to
		 * @return true if samplestamp and values of the given {@code DataPoint} are the same as this
		 */
		public boolean equals(DataPoint<T> dp) {
			if(dp == null) {
				return false;
			}
			if(dp.samplestamp == this.samplestamp && dp.data.equals(this.data)) {
				return true;
			} else {
				return false;
			}
		}
		
	}
	
}
