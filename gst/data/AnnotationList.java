/**
 * Annotation.java created on 19.07.2012
 */

package gst.data;

import java.util.List;

import org.unisens.Event;

/**
 * Wrapper class for a {@code List<Event>} to store time information. Despite it's name it does note implement the {@code List}-interface. 
 * @author Enrico Grunitz
 * @version 0.1.1 (07.08.2012)
 */
public class AnnotationList {
	/** list of the encapsuled events */				private List<Event> events = null;
	/** time offset */									private double baseTime = 0;
	/** time multiplier */								private double inverseSampleRate = 0;
	
	public AnnotationList(List<Event> eventList, double baseTime, double sampleRate) {
		if(eventList == null) {
			throw new NullPointerException("cannot create AnnotationList from null");
		}
		this.events = eventList;
		this.baseTime = baseTime;
		this.inverseSampleRate = 1 / sampleRate;
		return;
	}
	
	/* package visible */ Event getEvent(int index) {
		return events.get(index);
	}
	
	public int size() {
		return events.size();
	}
	
	public double getTime(int index) {
		return (baseTime + events.get(index).getSampleStamp() * inverseSampleRate);
	}
	
	public String getType(int index) {
		return events.get(index).getType();
	}
	
	public String getComment(int index) {
		return events.get(index).getComment();
	}
}
