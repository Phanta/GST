/**
 * ValueController.java created on 18.07.2012
 */

package gst.data;

import java.io.IOException;

import org.jfree.data.xy.XYSeriesCollection;
import org.unisens.SignalEntry;
import org.unisens.Value;
import org.unisens.ValuesEntry;

/**
 * 
 * @author Enrico Grunitz
 * @version 0.1 (18.07.2012)
 */
public class ValueController extends ViewController {
	/** index of controlled channel */				private int channelIndex;
	/** sample number of first entry */				private long firstSampleNumber;
	/** sample number of last entry */				private long lastSampleNumber;
	/** number of samples */						private long numSamples;

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
	 * Sets the index to the named channel which view should be controlled.
	 * @param channelName name of the selected channel
	 */
	public void setChannelToControl(String channelName) {
		if(channelName == null) {
			throw new NullPointerException("channel name must be not null");
		}
		String[] names = ((ValuesEntry)this.entry).getChannelNames();
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
	public XYSeriesCollection getDataPoints(double startTime, double endTime,
			int maxPoints) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see gst.data.ViewController#getMaxX()
	 */
	@Override
	public double getMaxX() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see gst.data.ViewController#getMinX()
	 */
	@Override
	public double getMinX() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see gst.data.ViewController#getPhysicalUnit()
	 */
	@Override
	public String getPhysicalUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
