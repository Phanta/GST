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
	
	/**
	 * @param eventList list of events to encapsule
	 * @param baseTime offset time for the events
	 * @param sampleRate (virtual) samplerate of the events
	 */
	public AnnotationList(List<Event> eventList, double baseTime, double sampleRate) {
		if(eventList == null) {
			throw new NullPointerException("cannot create AnnotationList from null");
		}
		this.events = eventList;
		this.baseTime = baseTime;
		this.inverseSampleRate = 1 / sampleRate;
		return;
	}
	
	/**
	 * Returns the event with the given index. Index is 0 based.
	 * @param index index of the requested event
	 * @return the selected event
	 */
	/* package visible */ Event getEvent(int index) {
		return events.get(index);
	}
	
	/**
	 * Returns the number of events in this list.
	 * @return size of the list
	 */
	public int size() {
		return events.size();
	}
	
	/**
	 * Calculates the time point of the event with the given index.
	 * @param index index of the event
	 * @return point in time of selected event
	 */
	public double getTime(int index) {
		return (baseTime + events.get(index).getSampleStamp() * inverseSampleRate);
	}
	
	/**
	 * Gives the type string of an event. 
	 * @param index index of the event
	 * @return string of event type
	 */
	public String getType(int index) {
		return events.get(index).getType();
	}
	
	/**
	 * Gives the comment string of an event.
	 * @param index index of the event in the list
	 * @return comment for the event
	 */
	public String getComment(int index) {
		return events.get(index).getComment();
	}
}
