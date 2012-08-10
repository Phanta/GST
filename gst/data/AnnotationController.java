/**
 * AnnotationController.java created on 19.07.2012
 */

package gst.data;

import gst.test.Debug;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jfree.data.xy.XYSeries;
import org.unisens.Event;
import org.unisens.EventEntry;

/**
 * Buffered {@link gst.data.DataController} implementation for {@code EventEntry}-type data in an {@link gst.data.UnisensDataset}.
 * @author Enrico Grunitz
 * @version 0.2.2 (10.08.2012)
 * @see gst.data.DataController
 */
public class AnnotationController extends DataController {
	/** sample number of the first event */						private long startSampleNumber;
	/** sample number of the last event */						private long endSampleNumber;
	/** {@code ArrayList} of all {@code Event}s */				private ArrayList<Event> buffer;
	/** exists the file? */										private boolean fileExists;
	/** index of last accessed element */						private int lastAccessedIndex;

	/**
	 * Creates the {@code AnnotationController} object for the given {@code EventEntry}.
	 * @param entry the EventEntry
	 */
	public AnnotationController(EventEntry entry) {
		super(entry);
		this.buffer = new ArrayList<Event>();
		// check if the file exists
		String fileName = this.entry.getUnisens().getPath();
		if(fileName.endsWith("unisens.xml")) {
			fileName.substring(0, fileName.length() - 11);
		}
		fileName += this.entry.getId();
		File testFile = new File(fileName);
		this.fileExists = testFile.exists(); 
		if(this.fileExists) {
			this.fillBuffer();
		}
		this.quickSort(0, this.buffer.size() - 1);
		this.initBorderSampleNumbers();
		this.isBuffered = true;
		this.lastAccessedIndex = 0;
	}
	
	/**
	 * Adds a new annotation to this Controller.
	 * @param time time in miliseconds of the annotation
	 * @param type the type
	 * @param comment the comment
	 */
	public void addAnnotation(double time, String type, String comment) {
		if(type == null) {
			type = "";
		}
		if(comment == null) {
			comment = "";
		}
		long sampleStamp = (long)Math.ceil((time - this.basetime) * ((EventEntry)this.entry).getSampleRate());
		int insertIndex = this.findIndexSmaller(sampleStamp) + 1;
		this.updateBorderSampleNumbers(sampleStamp);
		this.buffer.add(insertIndex, new Event(sampleStamp, type, comment));
	}
	
	/**
	 * Returns a list of annotations that are located at the given point in time.
	 * @param time point in time
	 * @return list of annotations
	 */
	public AnnotationList getAnnotation(double time) {
		long sampleStamp = (long)Math.round((time - this.basetime) * ((EventEntry)this.entry).getSampleRate());
		ArrayList<Event> events = new ArrayList<Event>(1);
		int index = this.findIndexSmaller(sampleStamp);
		while(index < this.buffer.size() && this.buffer.get(index).getSampleStamp() <= sampleStamp) {
			if(this.buffer.get(index).getSampleStamp() == sampleStamp) {
				events.add(this.buffer.get(index));
			}
			index++;
		}
		Debug.println(Debug.annotationController, "found " + events.size() + " events");
		return new AnnotationList(events, this.basetime, ((EventEntry)this.entry).getSampleRate());
	}
	
	/**
	 * Returns a List of annotations that are between {@code time - range} and {@code time + range}.
	 * @param time central point in time
	 * @param range time range value
	 * @return list of found annotations
	 */
	public AnnotationList getAnnotatioin(double time, double range) {
		// TODO implement
		return null;
	}
	
	/**
	 * Removes all events in the given list from this controller.
	 * @param annoList list of events to remove
	 */
	public void removeAnnotation(AnnotationList annoList) {
		for(int i = 0; i < annoList.size(); i++) {
			if(this.buffer.remove(annoList.getEvent(i))) {
				Debug.println(Debug.annotationController, "item successful removed");
			} else {
				Debug.println(Debug.annotationController, "item NOT removed");
			}
		}
		if(this.lastAccessedIndex >= this.buffer.size()) {
			this.lastAccessedIndex = this.buffer.size() - 1;
		}
		this.initBorderSampleNumbers();
	}
	
	/**
	 * Reads data from entry and adds them to the buffer. Doesn't check for existing file.
	 */
	private void fillBuffer() {
		try {
			buffer.addAll(((EventEntry)this.entry).read(0, (int)((EventEntry)this.entry).getCount()));
		} catch(IOException ioe) {
			Debug.println(Debug.annotationController, "IOException while filling buffer");
		}
	}
	
	/**
	 * Sets {@link #startSampleNumber} and {@link #endSampleNumber} to the given value if it is smaller or bigger than the old values.
	 * @param sampleStamp
	 */
	private void updateBorderSampleNumbers(long sampleStamp) {
		if(this.startSampleNumber > sampleStamp) {
			this.startSampleNumber = sampleStamp;
		} else if(this.endSampleNumber < sampleStamp) {
			this.endSampleNumber = sampleStamp;
		}
	}
	
	/**
	 * Adds reasonable data to the fields {@link #startSampleNumber} and {@link #endSampleNumber}.
	 */
	private void initBorderSampleNumbers() {
		switch(this.buffer.size()) {
		case 0:
			this.startSampleNumber = 0;
			this.endSampleNumber = 0;
			break;
		case 1:
			this.startSampleNumber = this.buffer.get(0).getSampleStamp();
			this.endSampleNumber = this.startSampleNumber;
			break;
		default:
			this.startSampleNumber = this.buffer.get(0).getSampleStamp();
			this.endSampleNumber = this.buffer.get(this.buffer.size() - 1).getSampleStamp();
		}
	}
	
	/**
	 * Sorts the elements of the list between index {@code low} and {@code high}.
	 * @param low index to start with
	 * @param high index of the last item to sort
	 */
	private void quickSort(int low, int high) {
		if(low < high) {
			int pivot = high;
			int i = low;
			int j = pivot - 1;
			while(i <= j) {
				if(this.buffer.get(i).getSampleStamp() > this.buffer.get(pivot).getSampleStamp()) {
					Event temp = this.buffer.get(i);
					this.buffer.set(i, this.buffer.get(j));
					this.buffer.set(j, temp);
					j--;
				} else {
					i++;
				}
			}
			Event temp = this.buffer.get(i);
			this.buffer.set(i, this.buffer.get(pivot));
			this.buffer.set(pivot, temp);
			this.quickSort(low, i - 1);
			this.quickSort(i + 1, high);
		}
	}
	
	/**
	 * Returns the index of the biggest element of the buffer that is smaller or equal than the given sample stamp.
	 * @param smapleStamp the sampleStamp to look for
	 * @return index of the searched element
	 * 		   -1 if there is no entry greater than the given sample stamp and
	 */
	private int findIndexSmaller(long sampleStamp) {
		if(this.buffer.size() == 0) {
			this.lastAccessedIndex = 0;
			return -1;
		} else if(this.buffer.get(this.lastAccessedIndex).getSampleStamp() == sampleStamp) {
			return this.lastAccessedIndex;
		} else if(this.startSampleNumber >= sampleStamp) {
			this.lastAccessedIndex = 0;
			return -1;
		} else if(this.endSampleNumber <= sampleStamp) {
			this.lastAccessedIndex = this.buffer.size() - 1;
			return this.buffer.size() - 1;
		}
		if(this.buffer.get(this.lastAccessedIndex).getSampleStamp() < sampleStamp) {
			// forward search
			int i;
			for(i = this.lastAccessedIndex + 1; i < this.buffer.size(); i++) {
				if(this.buffer.get(i).getSampleStamp() > sampleStamp) {
					break;
				}
			}
			this.lastAccessedIndex = i - 1;
		} else {
			// reverse search
			int i;
			for(i = this.lastAccessedIndex - 1; i > 0; i--) {
				if(this.buffer.get(i).getSampleStamp() <= sampleStamp) {
					break;
				}
			}
			this.lastAccessedIndex = i;
		}
		return this.lastAccessedIndex;
	}
	
	/**
	 * @see gst.data.DataController#getAnnotations(double, double)
	 */
	@Override
	public AnnotationList getAnnotations(double startTime, double endTime) {
		if(endTime < startTime) {
			// i can handle negative time-spans
			double temp = startTime;
			startTime = endTime;
			endTime = temp;
		}
		// calculate sample numbers from times
		long startSample = (long)Math.ceil((startTime - this.basetime) * ((EventEntry)this.entry).getSampleRate());
		long endSample = (long)Math.round((endTime - this.basetime) * ((EventEntry)this.entry).getSampleRate());
		// create list of interesting events
		Iterator<Event> it = this.buffer.iterator();
		ArrayList<Event> events = new ArrayList<Event>();
		Event event = null;
		while(it.hasNext()) {
			event = it.next();
			if(isInside(event.getSampleStamp(), startSample, endSample)) {
				events.add(event);
			}
		}
		return new AnnotationList(events, this.basetime, ((EventEntry)this.entry).getSampleRate());
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
	
	/**
	 * Clears the data from the {@code Entry} and appends the whole buffer.
	 * @see gst.data.DataController#saveImpl()
	 */
	@Override
	protected void saveImpl() {
		try {
			((EventEntry)this.entry).empty();
			((EventEntry)this.entry).append(this.buffer);
		} catch(IOException ioe) {
			Debug.println(Debug.annotationController, "IOException while saving");
		}
	}
	
	private static boolean isInside(long test, long low, long high) {
		return (test >= low && test <= high);
	}
}
