/**
 * DataChangeEvent.java created on 16.10.2012
 */

package gst.data;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * This event occurs if one or more data points of a {@link gst.data.DataController} are added, removed, changed time component
 * or changed in value.
 * @author Enrico Grunitz
 * @version 0.0.0.1 (16.10.2012)
 */
public class DataChangeEvent extends EventObject {

	public enum Type {
		REMOVED,
		ADDED,
		TIME_CHANGED,
		VALUE_CHANGED;
	}
	
	/** type of this event */						private Type type;
	/** points in time of changed data points */	private ArrayList<Double> time;
	/** values of changed data points */			private ArrayList<Double> value;
	/** old value of time or its value */			private ArrayList<Double> oldData;
	
	/**
	 * Creates a {@code DataChangeEvent} of the given source and type.
	 * @param source {@link gst.data.DataController} that has changed data
	 * @param type {@link gst.data.DataChangeEvent.Type} of this event
	 */
	public DataChangeEvent(DataController source, Type type) {
		super(source);
		this.type = type;
		this.time = new ArrayList<Double>();
		this.value = new ArrayList<Double>();
		this.oldData = new ArrayList<Double>();
		return;
	}
	
	/**
	 * Adds a data point at with a point in time an old and a new value.
	 * @param time time of the data point
	 * @param value (new) value of the data point
	 * @param old old value of the data point
	 */
	/* package visible */void addDataPoint(double time, double value, double old) {
		this.time.add(time);
		this.value.add(value);
		this.oldData.add(old);
	}
	
	/**
	 * Adds a data point to the list of changed points.
	 * @param time time of data point
	 * @param value value of data point
	 * @throws UnsupportedOperationException if the type of this event is not {@link gst.data.DataChangeEvent.Type#REMOVED}
	 * or {@link gst.data.DataChangeEvent.Type#ADDED}
	 */
	/* package visible */void addDataPoint(double time, double value) {
		if(this.type != Type.REMOVED && this.type != Type.ADDED) {
			throw new UnsupportedOperationException("adding a data point with no old value/time is unsupported for types TIME_CHANGED or VALUE_CHANGED");
		}
		this.time.add(time);
		this.value.add(value);
	}
	
	/**
	 * Adds a data point to the list of changed points.
	 * @param time time of data point
	 * @throws UnsupportedOperationException if the type of this event is not {@link gst.data.DataChangeEvent.Type#REMOVED}
	 * or {@link gst.data.DataChangeEvent.Type#ADDED}
	 */
	/* package visible */void addDataPoint(double time) {
		if(this.type != Type.REMOVED && this.type != Type.ADDED) {
			throw new UnsupportedOperationException("adding a data point with no old value/time is unsupported for types TIME_CHANGED or VALUE_CHANGED");
		}
		this.time.add(time);
	}
	
	/**
	 * @return number of data points affected by this event
	 */
	public int size() {
		return this.time.size();
	}
	
	/**
	 * @return the {@link gst.data.DataChangeEvent.Type} of this event 
	 */
	public Type getType() {
		return this.type;
	}
	
	/**
	 * Returns the time of the indexed data point.
	 * @param index index of the data point
	 * @return point in time of this data point
	 */
	public double getTime(int index) {
		return this.time.get(index);
	}
	
	/**
	 * Returns the (new) value of the indexed data point.
	 * @param index index of the data point
	 * @return the vale of the data point
	 */
	public double getValue(int index) {
		return this.value.get(index);
	}
	
	/**
	 * Returns the old value of the indexed data point.
	 * @param index index of the data point
	 * @return old value
	 */
	public double getOldValue(int index) {
		if(this.type != Type.ADDED || this.type != Type.REMOVED) {
			throw new UnsupportedOperationException("there is no old value of removed or added data");
		}
		return this.oldData.get(index);
	}
	
	/**
	 * Casting source to {@link gst.data.DataController}.
	 * @see java.util.EventObject#getSource()
	 */
	@Override public DataController getSource() {
		return (DataController)super.getSource();
	}
	
}
