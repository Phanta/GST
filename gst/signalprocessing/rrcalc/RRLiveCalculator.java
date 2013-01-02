/**
 * RRLiveCalculator.java created on 16.10.2012
 */

package gst.signalprocessing.rrcalc;

import gst.data.AnnotationList;
import gst.data.BufferedValueController;
import gst.data.DataChangeEvent;
import gst.data.DataController;
import gst.signalprocessing.LiveSignalProcessor;
import gst.signalprocessing.SignalProcessor;
import gst.test.Debug;

/**
 * This {@link gst.signalprocessing.SignalProcessor} calculates the RR-times of annotations. In addition to functionality
 * of {@link gst.signalprocessing.rrcalc.RRCalculator} this class supports the 'live' update of the generated data.
 * @author Enrico Grunitz
 * @version 0.0.0.1 (16.10.2012)
 */
public class RRLiveCalculator extends LiveSignalProcessor {

	/** name of this {@link SignalProcessor} */			final private static String PROC_NAME = "Live-RR-Calculator";
														final private static String PREF_ERROR = "Failed operation: ";
														final private static String PREF_SOURCE = "Quelle: ";
														final private static String PREF_TARGET = "; Ziel: ";

	public RRLiveCalculator(DataController source, BufferedValueController target) {
		super(1, 1, true, true);
		if(!source.isAnnotation()) {
			throw new IllegalArgumentException("source of " + PROC_NAME + " must be an annotation");
		}
		this.addSource(source);
		this.addTarget(target);
	}
														
	/** @see gst.signalprocessing.LiveSignalProcessor#preLiveAction() */
	@Override
	protected void preLiveAction() {
		// just run this once to get valid values
		this.performFunctionality();
	}

	/** @see gst.signalprocessing.LiveSignalProcessor#handleEvent(gst.data.DataChangeEvent) */
	@Override
	protected void handleEvent(DataChangeEvent event) {
		// this is fake live support
		this.performFunctionality();
		/*BufferedValueController bvc = (BufferedValueController)this.target(0);
		switch(event.getType()) {
		case ADDED:
			
		}*/
	}

	/**
	 * Calculates the intervals between the annotations of the source signal and saves them as values in the target dataset
	 * entry. The get the time as the second annotation of the difference calculation has.
	 * @return -1 if calculation fails, else 0
	 * @see gst.signalprocessing.SignalProcessor#performFunctionality()
	 */
	@Override protected int performFunctionality() {
		if(!this.source(0).isAnnotation()) {
			Debug.println(Debug.rrCalculator, PREF_ERROR + "Source signal of RRCalculator isn't an annotation.");
			return -1;
		}
		if(this.target(0).isReadOnly()) {
			Debug.println(Debug.rrCalculator, PREF_ERROR + "Target signal of RRCalculator is read-only.");
			return -1;
		}
		// get source data
		DataController anno = this.source(0);
		AnnotationList aList = anno.getAnnotations(anno.getMinX(), anno.getMaxX());
		if(aList.size() <= 1) {
			Debug.println(Debug.rrCalculator, PREF_ERROR + "Source signal has to few annotations.");
			return -1;
		}
		// prepare target signals
		BufferedValueController bvc = (BufferedValueController)this.target(0);
		bvc.clearDataPoints();
		bvc.setPhysicalUnit("ms");
		// Go-Go-Gadget-o-difference-calculator
		double prev = aList.getTime(0);
		double cur = 0;
		double diff = 0;
		for(int i = 1; i < aList.size(); i++) {
			cur = aList.getTime(i);
			diff = (cur - prev) * 1000;		// save it in miliseconds
			prev = cur;
			if(i == aList.size() - 1) {
				// last calculation cycle
				bvc.addDataPoint(cur, diff, true);
			} else {
				bvc.addDataPoint(cur, diff, false);
			}
		}
		return 0;
	}

	/** @see gst.signalprocessing.SignalProcessor#getName() */
	@Override public String getName() {
		return PROC_NAME;
	}

	/** @see gst.signalprocessing.SignalProcessor#getParameterString() */
	@Override public String getParameterString() {
		return PREF_SOURCE + this.source(0).getFullName() + PREF_TARGET + this.target(0).getFullName();
	}

}
