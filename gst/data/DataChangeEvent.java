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
	
	public DataChangeEvent(DataController source, Type type) {
		super(source);
		this.type = type;
		this.time = new ArrayList<Double>();
		this.value = new ArrayList<Double>();
		this.oldData = new ArrayList<Double>();
		return;
	}
	
	/* package visible */void addDataPoint(double time, double value, double old) {
		this.time.add(time);
		this.value.add(value);
		this.oldData.add(old);
	}
	
	/* package visible */void addDataPoint(double time, double value) {
		if(this.type != Type.REMOVED && this.type != Type.ADDED) {
			throw new UnsupportedOperationException("adding a data point with no old value/time is unsupported for types TIME_CHANGED or VALUE_CHANGED");
		}
		this.time.add(time);
		this.value.add(value);
	}
	
	/* package visible */void addDataPoint(double time) {
		if(this.type != Type.REMOVED && this.type != Type.ADDED) {
			throw new UnsupportedOperationException("adding a data point with no old value/time is unsupported for types TIME_CHANGED or VALUE_CHANGED");
		}
		this.time.add(time);
	}
	
	public int size() {
		return this.time.size();
	}
	
	public Type getType() {
		return this.type;
	}
	
	public double getTime(int index) {
		return this.time.get(index);
	}
	
	public double getValue(int index) {
		return this.value.get(index);
	}
	
	public double getOldValue(int index) {
		if(this.type != Type.ADDED || this.type != Type.REMOVED) {
			throw new UnsupportedOperationException("there is no old value of removed or added data");
		}
		return this.oldData.get(index);
	}
	
	@Override public DataController getSource() {
		return (DataController)super.getSource();
	}
	
}
