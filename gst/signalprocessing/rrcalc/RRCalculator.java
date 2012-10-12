/**
 * RRCalculator.java created on 12.10.2012
 */

package gst.signalprocessing.rrcalc;

import gst.data.AnnotationList;
import gst.data.BufferedValueController;
import gst.data.DataController;

import gst.signalprocessing.SignalProcessor;
import gst.test.Debug;

/**
 * This {@link gst.signalprocessing.SignalProcessor} calculates the RR-times of annotations.
 * @author Enrico Grunitz
 * @version 0.0.0.1 (12.10.2012)
 */
public class RRCalculator extends SignalProcessor {
	/** name of this {@link SignalProcessor} */			final private static String PROC_NAME = "RR-Calulator";
														final private static String PREF_ERROR = "Failed operation: ";
														final private static String PREF_SOURCE = "Quelle: ";

	public RRCalculator(DataController source, BufferedValueController target) {
		super(1, 1, true, true);
		this.addSource(source);
		this.addTarget(target);
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
		}
		// prepare target signals
		BufferedValueController bvc = (BufferedValueController)this.target(0);
		bvc.clearDataPoints();
		// Go-Go-Gadget-o-difference-calculator
		double prev = aList.getTime(0);
		double cur = 0;
		double diff = 0;
		for(int i = 1; i < aList.size(); i++) {
			cur = aList.getTime(i);
			diff = cur - prev;
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
		return PREF_SOURCE + this.source(0).getFullName();
	}

}
