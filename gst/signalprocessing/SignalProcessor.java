/**
 * SignalProcessor.java created on 12.10.2012
 */

package gst.signalprocessing;

import gst.Settings;
import gst.data.DataController;

import java.util.ArrayList;

/**
 * Abstract class of a signal processing function.
 * @author Enrico Grunitz
 * @version 0.0.0.3 (16.10.2012)
 */
public abstract class SignalProcessor {
	/** {@code ArrayList} of source {@link gst.data.DataController}s */	private ArrayList<DataController> sourceArr;
	/** {@code ArrayList} of target {@link gst.data.DataController}s */ private ArrayList<DataController> targetArr;
	/** allow multiple sources be the same? */							protected boolean allowDuplicateSources;
	/** allow multiple targets be the same? */							protected boolean allowDuplicateTargets;
	
	/**
	 * Convenience constructor for a {@code SignalProcessor} with ten source and target {@link gst.data.DataController} and
	 * allowing multiple instances of sources and targets to be the same.
	 */
	protected SignalProcessor() {
		this(10, 10, true, true);
		return;
	}
	
	/**
	 * Constructor.
	 * @param numSources number of source-{@link gst.data.DataController} to prepare array for
	 * @param numTargets number of target-{@code gst.data.DataController} to prepare array for
	 * @param duplicateSources if true more than one instance of one specific {@code DataController} is allowed in source array
	 * @param duplicateTargets if true more than one instance of one specific {@code DataController} is allowed in target array
	 */
	protected SignalProcessor(int numSources, int numTargets, boolean duplicateSources, boolean duplicateTargets) {
		this.sourceArr = new ArrayList<DataController>(numSources);
		this.targetArr = new ArrayList<DataController>(numTargets);
		this.allowDuplicateSources = duplicateSources;
		this.allowDuplicateTargets = duplicateTargets;
		return;
	}
	
	/**
	 * Returns the source-{@link gst.data.DataController} with the given index.
	 * @param index index of the {@code DataController}
	 * @return the selected {@code DataController}
	 */
	protected DataController source(int index) {
		if(index < 0 || index >= this.sourceArr.size()) {
			throw new IndexOutOfBoundsException("Source index = " + index + " out of bounds [0, " + this.sourceArr.size() + ").");
		}
		return this.sourceArr.get(index);
	}
	
	/**
	 * Returns the number of source {@link gst.data.DataController}s.
	 * @return number of source {@code gst.data.DataController}s
	 */
	protected int sourceSize() {
		return this.sourceArr.size();
	}

	/**
	 * Adds the given {@link gst.data.DataController} to the sources.
	 * @param dc the {@code DataController} to add to sources
	 * @throws NullPointerException if {@code dc} is null
	 */
	public void addSource(DataController dc) {
		if(dc == null) {
			throw new NullPointerException("Source-signal for signal processing shall not be null!");
		}
		if(this.allowDuplicateSources || !this.sourceArr.contains(dc)) {
			this.sourceArr.add(dc);
		}
	}
	
	/**
	 * Returns the target-{@link gst.data.DataController} with the given index.
	 * @param index index of the {@code DataController}
	 * @return the selected {@code DataController}
	 */
	protected DataController target(int index) {
		if(index < 0 || index >= this.targetArr.size()) {
			throw new IndexOutOfBoundsException("Target index = " + index + " out of bounds [0, " + this.targetArr.size() + ").");
		}
		return this.targetArr.get(index);
	}
	
	/**
	 * Returns the number of targeted {@link gst.data.DataController}s.
	 * @return number of targeted {@code gst.data.DataController}s
	 */
	protected int targetSize() {
		return this.targetArr.size();
	}

	/**
	 * Adds the given {@link gst.data.DataController} to the targets.
	 * @param dc the {@code DataController} to add to targets
	 * @throws NullPointerException if {@code dc} is null
	 */
	public void addTarget(DataController dc) {
		if(dc == null) {
			throw new NullPointerException("Target-signal for signal processing shall not be null!");
		}
		if(this.allowDuplicateTargets || !this.targetArr.contains(dc)) {
			this.targetArr.add(dc);
		}
	}
	
	/**
	 * Performs the signal processing function by calling the {@link #performFunctionality()} function and setting the
	 * {@code source} and {@code sourceId} fields of the targeted dataset entries.
	 */
	public int run() {
		String source = Settings.getInstance().getAppName() + " > " + this.getName();
		String sourceId = Settings.getInstance().getAppId() + " > " + this.getName() + " > " + this.getParameterString();
		for(int i = 0; i < this.targetArr.size(); i++) {
			this.targetArr.get(i).setSource(source, sourceId);
		}
		return this.performFunctionality();
	}
	
	@Override public String toString() {
		return this.getName() + " > " + this.getParameterString();
	}
	
	/**
	 * Implements the functionality of this signal processing function. 
	 * @return return value defined by implementation
	 */
	abstract protected int performFunctionality();
	
	/**
	 * Returns the name this signal processing function is called. Used for setting the {@code source} field of target entries
	 * of the dataset.
	 * @return the name this signal processing function is called
	 */
	abstract public String getName();
	
	/**
	 * Returns a {@code String} which contains all necessary parameters. Used for setting the {@code sourceId} field of target
	 * entries in the dataset.
	 * @return a comprehensive parameter list
	 */
	abstract public String getParameterString();
	
}
