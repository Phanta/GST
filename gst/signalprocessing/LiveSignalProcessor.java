/**
 * LiveSignalProcessor.java created on 16.10.2012
 */

package gst.signalprocessing;

import gst.data.DataChangeEvent;
import gst.data.DataChangeListener;
import gst.data.DataController;

/**
 * This {@link gst.signalprocessing.SignalProcessor} reacts on data changes on its source signals and accordingly modifies the
 * targeted signals. This behavior can be enabled an disabled by the {@link #start()} and {@link #stop()} methods.
 * @author Enrico Grunitz
 * @version 0.0.0.1 (16.10.2012)
 */
public abstract class LiveSignalProcessor extends SignalProcessor implements DataChangeListener {
	/** is this {@code LiveSignalProcessor}  running? */		private boolean isRunning = false;
	
	protected LiveSignalProcessor() {
		super();
	}
	
	protected LiveSignalProcessor(int numSources, int numTargets, boolean duplicateSources, boolean duplicateTargets) {
		super(numSources, numTargets, duplicateSources, duplicateTargets);
	}
	
	/**
	 * Adds the given {@link gst.data.DataController} to the sources and registers this {@code SignalProcessor} as
	 * {@link gst.data.DataChangeListener}.
	 * @param dc the {@code DataController} to add to sources
	 * @throws NullPointerException if {@code dc} is null
	 * @see gst.signalprocessing.SignalProcessor#addSource(DataController)
	 */
	@Override public void addSource(DataController dc) {
		super.addSource(dc);
		dc.register(this);
	}
	
	/** @see gst.data.DataChangeListener#dataChangeReaction(gst.data.DataChangeEvent) */
	@Override public void dataChangeReaction(DataChangeEvent event) {
		if(this.isRunning) {
			this.handleEvent(event);
		}
	}
	
	/**
	 * Starts the live signal processing.
	 */
	public void start() {
		this.preLiveAction();
		this.isRunning = true;
	}
	
	/**
	 * Stops the live signal processing.
	 */
	public void stop() {
		this.isRunning = false;
	}
	
	/**
	 * Returns {@code true} if this signalProcessor is performing his live processing.
	 * @return {@code true} if live performance is enabled, else {@code false}
	 */
	public boolean isRunning() {
		return this.isRunning;
	}
	
	/**
	 * This method is called right before the event handling starts. May be used for preparation. 
	 */
	abstract protected void preLiveAction();
	
	/**
	 * Function that performs the signal processing for the given event.
	 * @param event {@link gst.data.DataChangeEvent} to process
	 */
	abstract protected void handleEvent(DataChangeEvent event);
	
	/** @see gst.signalprocessing.SignalProcessor#performFunctionality() */
	@Override abstract protected int performFunctionality();

	/** @see gst.signalprocessing.SignalProcessor#getName() */
	@Override abstract public String getName();

	/** @see gst.signalprocessing.SignalProcessor#getParameterString() */
	@Override abstract public String getParameterString();

}
