/**
 * SignalController.java created on 17.07.2012
 */

package gst.data;

import java.io.IOException;
import java.util.List;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.unisens.Event;
import org.unisens.SignalEntry;

/**
 * {@code ViewController} implementation for {@code SignalEntry}-type data in an {@code UnisensDataset}.
 * @author Enrico Grunitz
 * @version 0.1 (18.07.2012)
 * @see gst.data.DataController
 */
public class SignalController extends DataController {
	
	/** index of controlled channel */				private int channelIndex;

	public SignalController(SignalEntry entry) {
		super(entry);
		this.channelCount = entry.getChannelCount();
		channelIndex = 0;
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
	 * Sets the index to the named channel which view should be controlled.
	 * @param channelName name of the selected channel
	 */
	public void setChannelToControl(String channelName) {
		if(channelName == null) {
			throw new NullPointerException("channel name must be not null");
		}
		String[] names = ((SignalEntry)this.entry).getChannelNames();
		for(int i = 0; i < this.channelCount; i++) {
			if(names[i].equals(channelName)) {
				this.channelIndex = i;
				return;
			}
		}
		throw new IllegalArgumentException("could not find channel named '" + channelName + "'");
	}

	/**
	 * @see gst.data.DataController#getDataPoints(double, double, int)
	 */
	@Override
	public XYSeriesCollection getDataPoints(double startTime, double endTime, int maxPoints) {
		double sampleRate = ((SignalEntry)this.entry).getSampleRate();
		XYSeries series = new XYSeries("");
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		if(maxPoints <= 0) {
			return dataset;
		}
		if(endTime < startTime) {
			// i can handle negative time-spans
			double temp = startTime;
			startTime = endTime;
			endTime = temp;
		}
		// calculate indices from time variables
		long iStart = (long)Math.ceil((startTime - this.basetime) * sampleRate); 
		long iEnd = (long)Math.round((endTime - this.basetime) * sampleRate);
		// read data from file
		double[][] dataArray = null;
		try {
			dataArray = ((SignalEntry)this.entry).readScaled(iStart, (int)(iEnd - iStart));
		} catch(IOException ioe) {
			System.out.println("couldn't read signal data from file");
			System.exit(1);
		}
		// add data to collection
		double timeStep = (endTime - startTime) / maxPoints;
		double time;
		double timeBarrier = this.basetime;
		// each time the current time is bigger than the timeBarrier, the current value is added to the collection and
		//   the timeBarrier is raised
		for( int i = 0; i < dataArray.length; i++) {
			time = this.basetime + ((iStart + i) / sampleRate);
			if(time >= timeBarrier) {
				series.add(time, dataArray[i][this.channelIndex], false);
				timeBarrier += timeStep;
			}
		}
		return dataset;
	}
	
	/**
	 * @see gst.data.DataController#getMinX()
	 */
	@Override
	public double getMinX() {
		return this.basetime + 0;
	}
	
	/**
	 * @see gst.data.DataController#getMaxX()
	 */
	@Override
	public double getMaxX() {
		return this.basetime + ((SignalEntry)this.entry).getCount() / ((SignalEntry)this.entry).getSampleRate();
	}
	
	/**
	 * @see gst.data.DataController#getPhysicalUnit()
	 */
	@Override
	public String getPhysicalUnit() {
		return ((SignalEntry)entry).getUnit();
	}

	/**
	 * @see gst.data.DataController#getAnnotations(double, double)
	 */
	@Override
	public AnnotationList getAnnotations(double startTime, double endTime) {
		return null;
	}
}
