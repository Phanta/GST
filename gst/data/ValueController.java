/**
 * ValueController.java created on 18.07.2012
 */

package gst.data;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jfree.data.xy.XYSeries;
import org.unisens.MeasurementEntry;
import org.unisens.Value;
import org.unisens.ValuesEntry;

/**
 * {@code ViewController} implementation for {@code ValuesEntry}-type data in an {@code UnisensDataset}.
 * @author Enrico Grunitz
 * @version 0.1.2.4 (15.10.2012)
 * @see gst.data.DataController
 */
public class ValueController extends DataController {
	/** index of controlled channel */				private int channelIndex = 0;
	/** sample number of first entry */				private long firstSampleNumber = 0;
	/** sample number of last entry */				private long lastSampleNumber = 0;

	/**
	 * Creates a {@code ValueController} for the given dataset entry.
	 * @param entry
	 */
	public ValueController(ValuesEntry entry) {
		super(entry);
		this.channelCount = entry.getChannelCount();
		this.updateBorderSampleNumber();
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
	 * Tries to read first and last data point of this entry and updates {@link #firstSampleNumber} and
	 * {@link #lastSampleNumber} accordingly. If reading fails the corresponding border-samplenumber is set to 0.
	 */
	private void updateBorderSampleNumber() {
		Value[] firstData = null;
		Value[] lastData = null;
		if(((ValuesEntry)this.entry).getCount() > 0) {		// try reading first value
			try {
				firstData = ((ValuesEntry)entry).read(0, 1);
			} catch(IOException ioe) {
				System.out.println("couldn't read first value entry of " + entry.getId());
				firstData = null;
			}
		}
		if(((ValuesEntry)this.entry).getCount() > 1) {		// try reading last value
			try {
				lastData = ((ValuesEntry)entry).read(((ValuesEntry)entry).getCount() - 1, 1);
			} catch(IOException ioe) {
				System.out.println("couldn't read last value entry of " + entry.getId());
				lastData = null;
			}
		}
		// setting border samplestamps 
		if(firstData != null) {
			this.firstSampleNumber = firstData[0].getSampleStamp();
		} else {
			this.firstSampleNumber = 0;
		}
		if(lastData != null) {
			this.lastSampleNumber = lastData[0].getSampleStamp();
		} else {
			this.lastSampleNumber = this.firstSampleNumber;
		}
	}

	/** @see gst.data.DataController#getDataPoints(double, double, int) */
	@Override
	public XYSeries getDataPoints(double startTime, double endTime, int maxPoints) {
		//double sampleRate = ((ValuesEntry)this.entry).getSampleRate();
		// name of series is interpreted as ID so it has to be unique, full name should do that
		XYSeries series = new XYSeries(this.getFullName());
		if(maxPoints <= 0) {
			return series;
		}
		if(endTime < startTime) {
			// i can handle negative time-spans
			double temp = startTime;
			startTime = endTime;
			endTime = temp;
		}
		// calculate indices from time variables
		long iStart = this.lowSampleStamp(startTime); 
		long iEnd = this.highSampleStamp(endTime);
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
				series.add(this.timeOf(value.getSampleStamp()), ((double[])value.getData())[this.channelIndex]);
			}
		} else {
			// FIXME does not support maxPoints - returns all data
			Iterator<Value> it = valueList.iterator();
			while(it.hasNext()) {
				Value value = it.next();
				// not sure if simple cast correctly converts runtime class to double
				series.add(this.timeOf(value.getSampleStamp()), ((double[])value.getData())[this.channelIndex]);
			}
		}
		return series;
	}

	/** @see gst.data.DataController#getMaxX() */
	@Override
	public double getMaxX() {
		return this.timeOf(this.lastSampleNumber);
	}

	/** @see gst.data.DataController#getMinX() */
	@Override
	public double getMinX() {
		return this.timeOf(this.firstSampleNumber);
	}

	/** @see gst.data.DataController#getPhysicalUnit() */
	@Override
	public String getPhysicalUnit() {
		return ((ValuesEntry)this.entry).getUnit();
	}

	/** @see gst.data.DataController#setPhysicalUnit(java.lang.String) */
	@Override public void setPhysicalUnit(String physUnit) {
		((MeasurementEntry)this.entry).setUnit(physUnit);
	}

	/** @see gst.data.DataController#getAnnotations(double, double) */
	@Override
	public AnnotationList getAnnotations(double startTime, double endTime) {
		return null;
	}
	
	/** @see gst.data.DataController#getFullName() */
	@Override
	public String getFullName() {
		return super.getFullName() + DataController.SEPERATOR + this.getChannelName();
	}
	
	/** @see gst.data.DataController#saveImpl() */
	@Override
	protected void saveImpl() {}

	/** @see gst.data.DataController#getChannelName() */
	@Override
	public String getChannelName() {
		return (((ValuesEntry)this.entry).getChannelNames())[this.channelIndex];
	}

}
