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
 * @version 0.0.0.3 (11.10.2012)
 */
public class BufferedValueController extends ValueController {
	/** buffer of the values of the controlled channel and entry */		private ArrayList<DataPoint<Double>> buffer;
	/** last accessed index */											private int lastAccessedIndex;
	
	/**
	 * Constructor.
	 * @param entry the dataset entry to control
	 */
	public BufferedValueController(ValuesEntry entry) {
		super(entry);
		this.isBuffered = true;
		this.setChannelToControl(0);
		this.readBuffer();
		this.updateLastAccess(0);
	}
	
	/** @see gst.data.ValueController#getDataPoints(double, double, int) */
	@Override public XYSeries getDataPoints(double startTime, double endTime, int maxPoints) {
		// TODO implementation
		return null;
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
		int insertIndex = 0;
		long curSS = dp.getSampleStamp();
		if(this.buffer.isEmpty()) {
			insertIndex = 0;
		} else if(curSS < this.buffer.get(this.lastAccessedIndex).getSampleStamp()) {
			// search before lastAccessedIndex
			for(int i = this.lastAccessedIndex; i >= 0; i--) {
				if(i == 0) {
					// checking first buffer element
					if(this.buffer.get(i).getSampleStamp() > curSS) {
						insertIndex = i;
						break;
					} else {
						insertIndex = i + 1;
						break;
					}
				}
				if(this.buffer.get(i).getSampleStamp() < curSS) {
					insertIndex = i + 1;
					break;
				}
			}
		} else {
			//search after lastAccesedIndex
			for(int i = this.lastAccessedIndex; i <= this.buffer.size(); i++) {
				if(i == this.buffer.size()) {
					// nothing found until end
					insertIndex = this.buffer.size();
					break;
				}
				if(this.buffer.get(i).getSampleStamp() > curSS) {
					// found first index that is bigger than the the item to insert
					insertIndex = i;
					break;
				}
			}
		}
		this.buffer.add(insertIndex, dp);
		this.updateLastAccess(insertIndex);
		return;
	}
	
	/**
	 * Reads all data from file and fills the buffer.
	 */
	private void readBuffer() {
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
		}
	}
	
	/**
	 * Updates {@link #lastAccessedIndex} to the given value, but checks size of the buffer. If {@code index} is lower 0 it is
	 * set to 0. If it is higher or equal {@code buffer.size()} it is set to {@code buffer.size() - 1}. If {@buffer.size()} is 0
	 * it is also set to 0.
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
	 * 
	 * @author Enrico Grunitz
	 * @version 0.0.0.1 (08.10.2012)
	 * @param <T> 
	 */
	private class DataPoint<T> {
		private long samplestamp;
		private T data;
		
		public DataPoint(long sampleStamp, T value) {
			this.samplestamp = sampleStamp;
			this.data = value;
		}
		
		public long getSampleStamp() {
			return this.samplestamp;
		}
		
		public T getData() {
			return this.data;
		}
		
		public void setSampleStamp(long sampleStamp) {
			this.samplestamp = sampleStamp;
		}
		
		public void setData(T data) {
			this.data = data;
		}
		
		public Value toValue() {
			return new Value(this.samplestamp, this.data);
		}
		
	}
	
}
