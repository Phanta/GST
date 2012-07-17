/**
 * SignalController.java created on 17.07.2012
 */

package gst.data;

import java.io.IOException;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.unisens.SignalEntry;

/**
 * ViewController for SignalEntry-type data in an UnisensDataset.
 * @author Enrico Grunitz
 * @version 0.1 (17.07.2012)
 */
public class SignalController extends ViewController {
	
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
	 * @see gst.data.ViewController#getDataPoints(double, double, int)
	 */
	@Override
	public XYSeriesCollection getDataPoints(double startTime, double endTime, int maxPoints) {
		double sampleRate = ((SignalEntry)this.entry).getSampleRate();
		XYSeries series = new XYSeries("");
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		// calculate indices from time variables
		long iStart = (long)Math.ceil((startTime - this.basetime) * sampleRate); 
		long iEnd = (long)Math.round((endTime - this.basetime) * sampleRate);
		// read data from file
		double [][] dataArray = null;
		try {
			dataArray = ((SignalEntry)this.entry).readScaled(iStart, (int)(iEnd - iStart));
		} catch(IOException ioe) {
			System.out.println("couldn't read data from file");
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
	 * @see gst.data.ViewController#getMinX()
	 */
	@Override
	public double getMinX() {
		return this.basetime + 0;
	}
	
	/**
	 * @see gst.data.ViewController#getMaxX()
	 */
	@Override
	public double getMaxX() {
		return this.basetime + ((SignalEntry)this.entry).getCount() / ((SignalEntry)this.entry).getSampleRate();
	}
	
	/**
	 * @see gst.data.ViewController#getPhysicalUnit()
	 */
	@Override
	public String getPhysicalUnit() {
		return ((SignalEntry)entry).getUnit();
	}
}
