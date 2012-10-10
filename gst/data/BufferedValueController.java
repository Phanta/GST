/**
 * BufferedValueController.java created on 08.10.2012
 */

package gst.data;

import gst.test.Debug;

import java.io.IOException;
import java.util.ArrayList;

import org.unisens.DataType;
import org.unisens.Value;
import org.unisens.ValuesEntry;

/**
 * Buffered version of a {@link gst.data.ValueController} which supports reading and writing of data. Only double-type data and
 * one single channel are supported.
 * @author Enrico Grunitz
 * @version 0.0.0.1 (10.10.2012)
 */
public class BufferedValueController extends ValueController {
	/** buffer of the values of the controlled channel and entry */		private ArrayList<DataPoint<Double>> buffer;
	
	/**
	 * 
	 * @param entry
	 * @param channelIndex
	 */
	public BufferedValueController(ValuesEntry entry) {
		super(entry);
		this.isBuffered = true;
		this.setChannelToControl(0);
		this.readBuffer();
	}
	
	/**
	 * 
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
			values = ((ValuesEntry)this.entry).readScaled((int)((ValuesEntry)this.entry).getCount());
		} catch(IOException ioe) {
			Debug.println(Debug.bufferedValueController, "failed to read file " + this.entry.getId());
			return;
		}
		this.buffer = new ArrayList<DataPoint<Double>>(values.length);
		for(int i = 0; i < values.length; i++) {
			this.buffer.add(new DataPoint<Double>(values[i].getSampleStamp(), (double)values[i].getData()));
		}
	}
	
	/** @see gst.data.DataController#saveImpl() */
	@Override
	public void saveImpl() {
		// TODO implementation
		Debug.println(Debug.bufferedValueController, "BufferedValueController.saveImpl() not yet implemented");
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
		
	}
	
}
