/**
 * AnnotationController.java created on 19.07.2012
 */

package gst.data;

import gst.test.Debug;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.data.xy.XYSeries;
import org.unisens.Event;
import org.unisens.EventEntry;

/**
 * {@code ViewController} implementation for {@code EventEntry}-type data in an {@code UnisensDataset}.
 * @author Enrico Grunitz
 * @version 0.1.2 (06.08.2012)
 * @see gst.data.DataController
 */
public class AnnotationController extends DataController {
	/** sample number of the first event */						private long startSampleNumber = 0;
	/** sample number of the last event */						private long endSampleNumber = 0;

	/**
	 * Creates the {@code AnnotationController} object for the given {@code EventEntry}.
	 * @param entry the EventEntry
	 */
	public AnnotationController(EventEntry entry) {
		super(entry);
		ArrayList<Event> borderEvents = new ArrayList<Event>(2);
		try {
			borderEvents.addAll(entry.read(0, 1));
			borderEvents.addAll(entry.read(entry.getCount() - 1, 1));
		} catch(FileNotFoundException fnfe) {
			System.out.println("coouldn't find file " + this.entry.getName());
		} catch(IOException ioe) {
			System.out.println("couldn't read first and last event entry of " + entry.getId());
			return;
		}
		if(borderEvents.size() != 2) {
			System.out.println("error while reading two events");
			//System.exit(1);
		}
		startSampleNumber = borderEvents.get(0).getSampleStamp();
		endSampleNumber = borderEvents.get(1).getSampleStamp();
	}
	
	public void addAnnotation(double time, String type, String comment) {
		if(type == null) {
			type = "";
		}
		if(comment == null) {
			comment = "";
		}
		long sampleStamp = (long)Math.ceil((time - this.basetime) * ((EventEntry)this.entry).getSampleRate());
		try {
			((EventEntry)this.entry).append(new Event(sampleStamp, type, comment));
		} catch(IOException ioe) {
			Debug.println(Debug.annotationController, "ioe while adding entry");
		}
	}
	
	/**
	 * @see gst.data.DataController#getAnnotations(double, double)
	 */
	@Override
	public AnnotationList getAnnotations(double startTime, double endTime) {
		//ArrayList<Event> list = new ArrayList<Event>();
		if(endTime < startTime) {
			// i can handle negative time-spans
			double temp = startTime;
			startTime = endTime;
			endTime = temp;
		}
		// calculate sample numbers from times
		long startSample = (long)Math.ceil((startTime - this.basetime) * ((EventEntry)this.entry).getSampleRate());
		long endSample = (long)Math.round((endTime - this.basetime) * ((EventEntry)this.entry).getSampleRate());
		// read data from file
		List<Event> data = null;
		try {
			data = ((EventEntry)this.entry).read(0, (int)((EventEntry)this.entry).getCount());
		} catch(IOException ioe) {
			System.out.println("couldn't read data from file '" + this.entry.getId() + "'");
		}
		// search for sample indices
		Iterator<Event> it = data.iterator();
		int startIndex = -1, endIndex = -1;
		Event event = null;
		for(int i = 0; it.hasNext(); i++) {
			event = it.next();
			if(startIndex == -1 && event.getSampleStamp() >= startSample) {
				startIndex = i;
			} else if(event.getSampleStamp() > endSample) {
				endIndex = i;	// List.subList(startIndex, endIndex) has an exclusive endIndex, so no -1
				break;	// found end sample
			}
		}
		// fill the return List
		if(startIndex == -1) {
			// found no start so there are no valid annotations
			return new AnnotationList(new ArrayList<Event>(0)/*list*/, this.basetime, ((EventEntry)this.entry).getSampleRate());
		}
		if(endIndex == -1) {
			// there is a start but no end - so end of data is the end
			endIndex = data.size();	// List.subList(startIndex, endIndex) has an exclusive endIndex, so no -1
		}
		//list.addAll(data.subList(startIndex, endIndex));
		return new AnnotationList(data.subList(startIndex, endIndex)/*list*/, this.basetime, ((EventEntry)this.entry).getSampleRate());
	}

	/**
	 * @see gst.data.DataController#getDataPoints(double, double, int)
	 */
	@Override
	public XYSeries getDataPoints(double startTime, double endTime, int maxPoints) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see gst.data.DataController#getMaxX()
	 */
	@Override
	public double getMaxX() {
		return this.basetime + (this.endSampleNumber / ((EventEntry)this.entry).getSampleRate());
	}

	/**
	 * @see gst.data.DataController#getMinX()
	 */
	@Override
	public double getMinX() {
		return this.basetime + (this.startSampleNumber / ((EventEntry)this.entry).getSampleRate());
	}

	/**
	 * @see gst.data.DataController#getPhysicalUnit()
	 */
	@Override
	public String getPhysicalUnit() {
		return "";		// annotation do not have any physical units (hopefully)
	}
	
}
