/**
 * ValueController.java created on 18.07.2012
 */

package gst.data;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.unisens.Value;
import org.unisens.ValuesEntry;

/**
 * 
 * @author Enrico Grunitz
 * @version 0.1 (19.07.2012)
 */
public class ValueController extends ViewController {
	/** index of controlled channel */				private int channelIndex = 0;
	/** sample number of first entry */				private long firstSampleNumber = 0;
	/** sample number of last entry */				private long lastSampleNumber = 0;
	/** number of samples */						private long numSamples = 0;

	public ValueController(ValuesEntry entry) {
		super(entry);
		this.channelCount = entry.getChannelCount();
		channelIndex = 0;
		this.numSamples = entry.getCount();
		Value[] firstData = null;
		Value[] lastData = null;
		try {
			firstData = entry.read(0, 1);
			lastData = entry.read(this.numSamples - 1, 1);
		} catch(IOException ioe) {
			System.out.println("couldn't read first and last value entry of " + entry.getId());
			return;
		}
		firstSampleNumber = firstData[0].getSampleStamp();
		lastSampleNumber = lastData[0].getSampleStamp();
		return;
	}
	
	/**
	 * Sets the index to the channel which view should be controlled.
	 * @param index of the channel
	 */
	public void setChannelToControl(int index) {
		if(index < 0 || index >= this.channelCount) {
			throw new IndexOutOfBoundsException("channel index invalid: 0 <= " + index + " < " + this.channelCount);
		}
		this.channelIndex = index;
		return;
	}

	/**
	 * Sets the index to the named channel which view should be controlled. Convenience method for {@code void setChannelToControl(int)}.
	 * @param channelName name of the selected channel
	 */
	public void setChannelToControl(String channelName) {
		if(channelName == null) {
			throw new NullPointerException("channel name must be not null");
		}
		String[] names = ((ValuesEntry)this.entry).getChannelNames();
		for(int i = 0; i < this.channelCount; i++) {
			if(names[i].equals(channelName)) {
				this.setChannelToControl(i);
				return;
			}
		}
		throw new IllegalArgumentException("could not find channel named '" + channelName + "'");
	}

	/**
	 * @see gst.data.ViewController#getDataPoints(double, double, int)
	 */
	@Override
	public XYSeriesCollection getDataPoints(double startTime, double endTime, int maxPoints) {
		double sampleRate = ((ValuesEntry)this.entry).getSampleRate();
		XYSeries series = new XYSeries("");
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		if(maxPoints <= 0) {
			return dataset;
		}
		// calculate indices from time variables
		long iStart = (long)Math.ceil((startTime - this.basetime) * sampleRate); 
		long iEnd = (long)Math.round((endTime - this.basetime) * sampleRate);
		// read data from file
			// TODO simplest version of getting data: read until find first and last data point in time window
			// store data of all points in between
		Value[] valueArray = null;
		ArrayList<Value> valueList = new ArrayList<Value>(maxPoints);
		boolean notFinished = true, notEOF = true;
		long position = 0;
		do {
			try {
				valueArray = ((ValuesEntry)this.entry).readScaled(position, 1);
			} catch(IOException ioe) {
				if(!(ioe instanceof EOFException)) {
					System.out.println("couldn't read value data from file");
					System.exit(1);
				} else {
					System.out.println("EOF reached");
					notEOF = false;
				}
			}
			position++;
			if(valueArray != null && valueArray.length != 0) {
				if(valueArray[0].getSampleStamp() >= iStart && valueArray[0].getSampleStamp() <= iEnd) {
					valueList.add(valueArray[0]);
				}
				if(valueArray[0].getSampleStamp() > iEnd) {
					notFinished = false;
				}
			} else {
				notFinished = false;
			}
		} while(notFinished && notEOF);
		// fill XYSeries
		if(valueList.size() <= maxPoints) {
			Iterator<Value> it = valueList.iterator();
			while(it.hasNext()) {
				Value value = it.next();
				// not sure if simple cast correctly converts runtime class to double
				series.add(this.basetime + value.getSampleStamp() / sampleRate, ((double[])value.getData())[this.channelIndex]);
			}
		} else {
			// FIXME does not support maxPoints - returns all data
			Iterator<Value> it = valueList.iterator();
			while(it.hasNext()) {
				Value value = it.next();
				// not sure if simple cast correctly converts runtime class to double
				series.add(this.basetime + value.getSampleStamp() / sampleRate, ((double[])value.getData())[this.channelIndex]);
			}
		}
		return dataset;
	}

	/**
	 * @see gst.data.ViewController#getMaxX()
	 */
	@Override
	public double getMaxX() {
		return this.basetime + (this.lastSampleNumber / ((ValuesEntry)this.entry).getSampleRate());
	}

	/**
	 * @see gst.data.ViewController#getMinX()
	 */
	@Override
	public double getMinX() {
		return this.basetime + (this.firstSampleNumber / ((ValuesEntry)this.entry).getSampleRate());
	}

	/**
	 * @see gst.data.ViewController#getPhysicalUnit()
	 */
	@Override
	public String getPhysicalUnit() {
		return ((ValuesEntry)this.entry).getUnit();
	}
}
